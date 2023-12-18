package semantics.visitors;

import ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import semantics.TabExtended;
import semantics.util.LogUtils;
import semantics.util.StructList;

import java.util.Collection;

public class DesignatorVisitor extends VisitorAdaptor {

    private final SemanticPass semanticPass;

    DesignatorVisitor(SemanticPass semanticPass) {
        this.semanticPass = semanticPass;
    }

    private Obj findDesignator(String designName, SyntaxNode syntaxNode) {
        Obj typeNode = TabExtended.find(designName);

        if (typeNode == TabExtended.noObj) {
            LogUtils.logError("Error on line " +
                    syntaxNode.getLine() + ": variable " + designName + " not declared.");
        }

        return typeNode;
    }

    public void visit(DesignatorAssignListStmt designatorAssignListStmt) {
        // TODO
    }

    public void visit(DesignatorAssignList designatorAssignList) {
        // TODO
    }

    public void visit(DesignatorOpAssign designatorOpAssign) {
        Struct lType = designatorOpAssign.getDesignator().obj.getType();
        Struct rType = designatorOpAssign.getExpr().struct;

        if (!rType.assignableTo(lType)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error on line ");
            sb.append(designatorOpAssign.getLine());
            sb.append(": incompatible assignment expression for types ");
            sb.append(LogUtils.kindToString(lType.getKind()));
            if (lType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(LogUtils.kindToString(lType.getElemType().getKind()));
                sb.append("]");
            }
            sb.append(" and ");
            sb.append(LogUtils.kindToString(rType.getKind()));
            if (rType.getKind() == Struct.Array) {
                sb.append("[");
                sb.append(LogUtils.kindToString(rType.getElemType().getKind()));
                sb.append("]");
            }
            LogUtils.logError(sb.toString());
        }
    }

    public void visit(DesignatorOpCall designatorOpCall) {
        Obj func = designatorOpCall.getDesignator().obj;
        boolean validCall = true;

        if (Obj.Meth == func.getKind()) {
            LogUtils.logInfo("Found function call on line " + designatorOpCall.getLine());

            StructList structList = designatorOpCall.getActParsOpt().structlist;
            Collection<Obj> localSymbols = func.getLocalSymbols();

            int actPars = structList.size();
            int formPars = func.getLevel();

            if (formPars != actPars) {
                LogUtils.logError("Error on line " + designatorOpCall.getLine()
                        + ": expected " + formPars + " parameters, but got "
                        + actPars);

                designatorOpCall.obj = TabExtended.noObj;
                validCall = false;
            } else {
                int i = 0;
                for (Obj obj : localSymbols) {
                    Struct lType = obj.getType();
                    Struct rType = structList.get(i);

                    if (!rType.assignableTo(lType)) {
                        LogUtils.logError("Error on line " + designatorOpCall.getLine()
                                + ": type " + LogUtils.kindToString(rType.getKind())
                                + " not assignable to type " + LogUtils.kindToString(lType.getKind()));

                        validCall = false;
                    }
                }

                designatorOpCall.obj = func;
            }
        } else {
            LogUtils.logError("Error on line " + designatorOpCall.getLine()
                    + ": designator " + func.getName()
                    + " is not a function");

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
            LogUtils.logError("Error on line " + designatorOpIncrement.getLine() +
                    ": invalid increment operation for type "
                    + LogUtils.kindToString(kind)
                    + " of variable" + design.getName());
        }
    }

    public void visit(DesignatorOpDecrement designatorOpDecrement) {
        Obj design = designatorOpDecrement.getDesignator().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Int) {
            LogUtils.logError("Error on line " + designatorOpDecrement.getLine() +
                    ": invalid decrement operation for type "
                    + LogUtils.kindToString(kind)
                    + " of variable " + design.getName());
        }
    }

    public void visit(DesignatorIndOpDot designatorIndOp) {
        // TODO when doing classes
    }

    public void visit(DesignatorIndOpBracket designatorIndOp) {
        Obj design = designatorIndOp.getDesignator().obj;
        int kind = design.getType().getKind();

        if (kind != Struct.Array) {
            LogUtils.logError("Error on line " + designatorIndOp.getLine() +
                    ": invalid array indexing operation with type "
                    + LogUtils.kindToString(kind)
                    + " of variable " + design.getName());

            designatorIndOp.obj = TabExtended.noObj;
        } else {
            // TODO see if this is good
            Struct elemType = design.getType().getElemType();
            designatorIndOp.obj = new Obj(design.getKind(), design.getName(), elemType);
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
