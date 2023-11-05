// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class ConstNumList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ConstNumList ConstNumList;
    private Integer N2;

    public ConstNumList (ConstNumList ConstNumList, Integer N2) {
        this.ConstNumList=ConstNumList;
        if(ConstNumList!=null) ConstNumList.setParent(this);
        this.N2=N2;
    }

    public ConstNumList getConstNumList() {
        return ConstNumList;
    }

    public void setConstNumList(ConstNumList ConstNumList) {
        this.ConstNumList=ConstNumList;
    }

    public Integer getN2() {
        return N2;
    }

    public void setN2(Integer N2) {
        this.N2=N2;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstNumList!=null) ConstNumList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstNumList!=null) ConstNumList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstNumList!=null) ConstNumList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstNumList(\n");

        if(ConstNumList!=null)
            buffer.append(ConstNumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+N2);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstNumList]");
        return buffer.toString();
    }
}
