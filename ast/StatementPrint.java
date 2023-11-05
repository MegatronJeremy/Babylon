// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class StatementPrint extends Statement {

    private Expr Expr;
    private ConstNumList ConstNumList;

    public StatementPrint (Expr Expr, ConstNumList ConstNumList) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.ConstNumList=ConstNumList;
        if(ConstNumList!=null) ConstNumList.setParent(this);
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public ConstNumList getConstNumList() {
        return ConstNumList;
    }

    public void setConstNumList(ConstNumList ConstNumList) {
        this.ConstNumList=ConstNumList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
        if(ConstNumList!=null) ConstNumList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(ConstNumList!=null) ConstNumList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(ConstNumList!=null) ConstNumList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("StatementPrint(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstNumList!=null)
            buffer.append(ConstNumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [StatementPrint]");
        return buffer.toString();
    }
}
