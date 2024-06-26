package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.adaptors.TabExtended;
import semantics.util.LogUtils;
import semantics.util.ObjList;
import semantics.util.StructList;
import semantics.util.VisitorUtils;

import java.util.*;

public class DesignatorVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    DesignatorVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    private Obj findDesignator(String qualifiedName, String unqualifiedName, SyntaxNode syntaxNode) {
        Obj obj = TabExtended.find(qualifiedName);

        if (obj == TabExtended.noObj) {
            obj = findDesignator(unqualifiedName, syntaxNode);
        }
        return obj;
    }

    private Obj findDesignator(String designName, SyntaxNode syntaxNode) {
        if (semanticPass.designatorNameInFunctionCall(syntaxNode)) {
            // two parents up -> must be a function call (cannot be through class/array field indirection)
            // delay until actual parameters scanned
            // at least this will be always called
            semanticPass.currentMethodCalledStack.push(designName);
            semanticPass.currentMethodCallIsClassStack.push(false);

            return TabExtended.noObj;
        }

        Obj typeNode = TabExtended.find(designName);
        if (typeNode == TabExtended.noObj) {
            LogUtils.logError("Symbol " + designName + " not declared.", syntaxNode);
        }

        return typeNode;
    }

    private Obj findDesignatorInClass(boolean reportError, DesignatorIndOpDot designatorIndOp, String identifier, SyntaxNode syntaxNode) {
        Obj design = designatorIndOp.getDesignator().obj;
        Struct type = design.getType();

        Obj obj;
        if (design.getKind() != Obj.Type) {
            // is normal member
            obj = type.getMembersTable().searchKey(identifier);
            if (obj == null) {
                // Edge case: try finding in outer scope - calling while class table is not yet formed
                Scope scopeToSearch = TabExtended.currentScope.getOuter(); // look in outer scope (not yet chained)
                obj = scopeToSearch.findSymbol(identifier);
            }
        } else {
            // is static member - name is global
            String name = type + "." + identifier;
            obj = TabExtended.find(name);
        }

        if (obj == null) {
            if (reportError) {
                LogUtils.logError("Symbol "
                                + identifier
                                + " is not a class member",
                        syntaxNode);
            }

            obj = TabExtended.noObj;
        }

        return obj;
    }

    public void visit(DesignatorAssignListStmt designatorAssignListStmt) {
        Obj rValue = designatorAssignListStmt.getDesignator1().obj;
        if (rValue.getType().getKind() != Struct.Array) {
            LogUtils.logError("Right side of assign list " + rValue.getName() + " is not an array type.",
                    designatorAssignListStmt.getDesignator());

            return;
        }

        Obj fillArray = designatorAssignListStmt.getDesignator().obj;
        if (fillArray.getType().getKind() != Struct.Array) {
            LogUtils.logError("Variable length object in assign list " + rValue.getName() + " must be an array type.",
                    designatorAssignListStmt.getDesignator());

            return;
        }

        Struct rType = rValue.getType().getElemType();
        for (Obj obj : designatorAssignListStmt.getDesignatorAssignList().objlist) {
            if (obj == TabExtended.noObj) {
                continue;
            }

            if (obj.getKind() != Obj.Var && obj.getKind() != Obj.Fld && obj.getKind() != Obj.Elem) {
                LogUtils.logError("Left side of assign list " + rValue.getName()
                                + " must be an of type variable, class field, or array element.",
                        designatorAssignListStmt.getDesignator());

                return;
            }

            Struct lType = obj.getType();
            VisitorUtils.checkAssignability(lType, rType, designatorAssignListStmt.getDesignator());
        }

        VisitorUtils.checkAssignability(fillArray.getType().getElemType(), rType, designatorAssignListStmt.getDesignator());
    }

    public void visit(DesignatorAssignListExists designatorAssignListExists) {
        designatorAssignListExists.objlist = designatorAssignListExists.getDesignatorAssignList().objlist;
        designatorAssignListExists.objlist.add(designatorAssignListExists.getDesignatorOpt().obj);
    }

    public void visit(DesignatorAssignListEmpty designatorAssignListEmpty) {
        designatorAssignListEmpty.objlist = new ObjList();
    }

    public void visit(DesignatorExists designatorExists) {
        designatorExists.obj = designatorExists.getDesignator().obj;
    }

    public void visit(DesignatorEmpty designatorEmpty) {
        designatorEmpty.obj = TabExtended.noObj;
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        Struct lType = designatorOpAssign.getDesignator().obj.getType();
        Struct rType = designatorOpAssign.getExpr().struct;

        if (!rType.assignableTo(lType)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Incompatible assignment expression for types ");

            sb.append(lType);
            if (lType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(lType.getElemType());
                sb.append("]");
            }
            sb.append(" and ");

            sb.append(rType);
            if (rType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(rType.getElemType());
                sb.append("]");
            }

            LogUtils.logError(sb.toString(), designatorOpAssign);
        }
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        // form true name
        assert !semanticPass.currentMethodCalledStack.isEmpty();
        Queue<StringBuilder> qualifiedFuncNameSBQueue = new LinkedList<>();
        qualifiedFuncNameSBQueue.add(new StringBuilder(semanticPass.currentMethodCalledStack.pop()));

        StructList structList = designatorOpCall.getActParsOpt().structlist;
        for (Struct type : structList) {
            int i = qualifiedFuncNameSBQueue.size();
            while (i-- > 0) {
                StringBuilder qualifiedFuncNameSB = qualifiedFuncNameSBQueue.poll();
                assert qualifiedFuncNameSB != null;

                StringBuilder baseFuncNameSB = new StringBuilder(qualifiedFuncNameSB);
                baseFuncNameSB.append("$").append(type);
                qualifiedFuncNameSBQueue.add(baseFuncNameSB);

                // for classes add all supertypes
                if (type.getKind() == Struct.Class) {
                    Struct superType = type.getElemType();
                    while (superType != TabExtended.noType) {
                        StringBuilder superFuncNameSB = new StringBuilder(qualifiedFuncNameSB);
                        superFuncNameSB.append("$").append(superType);
                        qualifiedFuncNameSBQueue.add(superFuncNameSB);

                        superType = superType.getElemType();
                    }
                }
            }
        }
        boolean currentMethodCallIsClass = semanticPass.currentMethodCallIsClassStack.pop();
        DesignatorIndOpDot designatorIndOpDot = null;
        if (currentMethodCallIsClass) {
            designatorIndOpDot = semanticPass.currentMethodCallNodeStack.pop();
        }

        while (!qualifiedFuncNameSBQueue.isEmpty()) {
            StringBuilder qualifiedFuncNameSB = qualifiedFuncNameSBQueue.poll();

            qualifiedFuncNameSB.append("$");
            String fullFuncName = qualifiedFuncNameSB.toString();

            Obj func;
            if (currentMethodCallIsClass) {
                // find in class object
                func = findDesignatorInClass(false, designatorIndOpDot, fullFuncName, designatorOpCall);
            } else {
                // find in scope
                String qualifiedFuncName = semanticPass.getNamespaceQualifiedName(fullFuncName);
                func = findDesignator(qualifiedFuncName, fullFuncName, designatorOpCall);
            }

            boolean validCall = true;
            if (Obj.Meth == func.getKind()) {
                Collection<Obj> localSymbols = func.getLocalSymbols();

                LogUtils.logInfo("Found function call " + func.getName(), designatorOpCall);

                int actPars = structList.size();
                int formPars = func.getFpPos();

                boolean thisSkipped = true;
                if (formPars > 0) {
                    Iterator<Obj> iter = localSymbols.iterator();
                    if (iter.hasNext()) {
                        // handle special case when method has hidden this parameter
                        String name = iter.next().getName();
                        if (Objects.equals(name, "this")) {
                            formPars--;
                            thisSkipped = false;
                        }
                    }
                }

                if (formPars != actPars) {
                    LogUtils.logError("Expected " + formPars + " parameter(s), but got " + actPars, designatorOpCall);
                } else {
                    int i = 0, sz = structList.size();
                    for (Obj obj : localSymbols) {
                        if (i == sz) {
                            break;
                        }

                        if (!thisSkipped) {
                            thisSkipped = true;
                            continue;
                        }

                        Struct lType = obj.getType();
                        Struct rType = structList.get(i);

                        if (!VisitorUtils.checkAssignability(lType, rType, designatorOpCall)) {
                            validCall = false;
                        }

                        i = i + 1;
                    }

                    if (validCall) {
                        // call is valid
                        designatorOpCall.obj = func;
                        designatorOpCall.getDesignator().obj = func;
                        return;
                    }
                }

                // call is not valid
                designatorOpCall.obj = TabExtended.noObj;
                designatorOpCall.getDesignator().obj = TabExtended.noObj;
                return;
            } else if (func != TabExtended.noObj || qualifiedFuncNameSBQueue.isEmpty()) {
                LogUtils.logError("Designator " + fullFuncName + " is not a function", designatorOpCall);

                designatorOpCall.obj = TabExtended.noObj;
                return;
            }
        }
    }

    public void visit(DesignatorOpIncrement designatorOpIncrement) {
        Obj design = designatorOpIncrement.getDesignator().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Int) {
            LogUtils.logError("Invalid increment operation for type " + LogUtils.structKindToString(kind)
                    + " of variable" + design.getName(), designatorOpIncrement);
        }
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        Obj design = designatorOpDecrement.getDesignator().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Int) {
            LogUtils.logError("Invalid decrement operation for type " + LogUtils.structKindToString(kind)
                    + " of variable " + design.getName(), designatorOpDecrement);
        }
    }

    public void visit(DesignatorIndOpDot designatorIndOp) {
        Obj design = designatorIndOp.getDesignator().obj;
        Struct type = design.getType();
        String identifier = designatorIndOp.getIdentifier();

        if (type.getKind() != Struct.Class) {
            LogUtils.logError("Dot operation only allowed on class type",
                    designatorIndOp);
            designatorIndOp.obj = TabExtended.noObj;

            return;
        }

        Obj obj = null;

        // skip this for now if in function call
        if (!semanticPass.designatorIndOpInFunctionCall(designatorIndOp)) {
            obj = findDesignatorInClass(true, designatorIndOp, identifier, designatorIndOp);
        } else {
            // save for later
            semanticPass.currentMethodCalledStack.push(identifier);
            semanticPass.currentMethodCallIsClassStack.push(true);
            semanticPass.currentMethodCallNodeStack.push(designatorIndOp);
        }

        if (obj == null) {
            obj = TabExtended.noObj;
        }

        designatorIndOp.obj = obj;
    }

    public void visit(DesignatorIndOpBracket designatorIndOp) {
        Obj design = designatorIndOp.getDesignatorArr().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Array) {
            LogUtils.logError("Invalid array indexing operation with type " + LogUtils.structKindToString(kind)
                    + " of symbol " + design.getName(), designatorIndOp);

            designatorIndOp.obj = TabExtended.noObj;
        } else {
            // this is good
            Struct elemType = design.getType().getElemType();
            // Declare as element object type
            designatorIndOp.obj = new Obj(Obj.Elem, design.getName(), elemType);
        }
    }

    public void visit(DesignatorArr designatorArr) {
        designatorArr.obj = designatorArr.getDesignator().obj;
    }

    public void visit(DesignatorNoInd designatorNoInd) {
        designatorNoInd.obj = designatorNoInd.getDesignatorName().obj;
    }

    public void visit(DesignatorNameNamespace designator) {
        // Look only for qualified name
        String designatorName = designator.getDesignNamespace() +
                "::" + designator.getDesignName();

        designator.obj = findDesignator(designatorName, designator);
    }

    public void visit(DesignatorNameNoNamespace designator) {
        // First look for implicit namespace and then for global namespace if not found
        String designatorName = designator.getDesignName();
        String qualifiedName = semanticPass.getQualifiedNameLookup(designatorName);

        designator.obj = findDesignator(qualifiedName, designatorName, designator);
    }
}
