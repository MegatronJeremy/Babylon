// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class OperationDeclBody implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ConstructorOrMethodDeclList ConstructorOrMethodDeclList;

    public OperationDeclBody (ConstructorOrMethodDeclList ConstructorOrMethodDeclList) {
        this.ConstructorOrMethodDeclList=ConstructorOrMethodDeclList;
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.setParent(this);
    }

    public ConstructorOrMethodDeclList getConstructorOrMethodDeclList() {
        return ConstructorOrMethodDeclList;
    }

    public void setConstructorOrMethodDeclList(ConstructorOrMethodDeclList ConstructorOrMethodDeclList) {
        this.ConstructorOrMethodDeclList=ConstructorOrMethodDeclList;
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
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("OperationDeclBody(\n");

        if(ConstructorOrMethodDeclList!=null)
            buffer.append(ConstructorOrMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [OperationDeclBody]");
        return buffer.toString();
    }
}
