package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;
import semantics.util.ObjList;
import semantics.util.StructList;
import semantics.util.VisitorUtils;

import java.util.Collection;

public class DesignatorVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    DesignatorVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    private Obj findDesignator(String designName, SyntaxNode syntaxNode) {
        Obj typeNode = TabExtended.find(designName);

        if (typeNode == TabExtended.noObj) {
            LogUtils.logError("Variable " + designName + " not declared.", syntaxNode);
        }

        return typeNode;
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

            sb.append(LogUtils.structKindToString(lType.getKind()));
            if (lType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(LogUtils.structKindToString(lType.getElemType().getKind()));
                sb.append("]");
            }
            sb.append(" and ");

            sb.append(LogUtils.structKindToString(rType.getKind()));
            if (rType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(LogUtils.structKindToString(rType.getElemType().getKind()));
                sb.append("]");
            }

            LogUtils.logError(sb.toString(), designatorOpAssign);
        }
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        Obj func = designatorOpCall.getDesignator().obj;
        boolean validCall = true;

        if (Obj.Meth == func.getKind()) {
            LogUtils.logInfo("Found function call " + func.getName(), designatorOpCall);

            StructList structList = designatorOpCall.getActParsOpt().structlist;
            Collection<Obj> localSymbols = func.getLocalSymbols();

            int actPars = structList.size();
            int formPars = func.getLevel();

            if (formPars != actPars) {
                LogUtils.logError("Expected " + formPars + " parameters, but got " + actPars, designatorOpCall);

                designatorOpCall.obj = TabExtended.noObj;
                validCall = false;
            } else {
                int i = 0;
                for (Obj obj : localSymbols) {
                    Struct lType = obj.getType();
                    Struct rType = structList.get(i);

                    if (!VisitorUtils.checkAssignability(lType, rType, designatorOpCall)) {
                        validCall = false;
                    }
                }

                designatorOpCall.obj = func;
            }
        } else {
            LogUtils.logError("Designator " + func.getName() + " is not a function", designatorOpCall);

            validCall = false;
        }

        if (!validCall) {
            designatorOpCall.obj = TabExtended.noObj;
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
        // TODO when doing classes
        // Declare as field object type
    }

    public void visit(DesignatorIndOpBracket designatorIndOp) {
        Obj design = designatorIndOp.getDesignator().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Array) {
            LogUtils.logError("Invalid array indexing operation with type " + LogUtils.structKindToString(kind)
                    + " of variable " + design.getName(), designatorIndOp);

            designatorIndOp.obj = TabExtended.noObj;
        } else {
            // TODO see if this is good
            Struct elemType = design.getType().getElemType();
            // Declare as element object type
            designatorIndOp.obj = new Obj(Obj.Elem, design.getName(), elemType);
        }
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
        String qualifiedName = semanticPass.getQualifiedName(designatorName);
        Obj obj = TabExtended.find(qualifiedName);

        if (obj == TabExtended.noObj) {
            obj = findDesignator(designatorName, designator);
        }

        designator.obj = obj;
    }


}
