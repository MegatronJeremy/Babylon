// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class ConstructorOrMethodDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ConstructorOrMethodDeclList ConstructorOrMethodDeclList;
    private ConstructorOrMethodDecl ConstructorOrMethodDecl;

    public ConstructorOrMethodDeclList (ConstructorOrMethodDeclList ConstructorOrMethodDeclList, ConstructorOrMethodDecl ConstructorOrMethodDecl) {
        this.ConstructorOrMethodDeclList=ConstructorOrMethodDeclList;
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.setParent(this);
        this.ConstructorOrMethodDecl=ConstructorOrMethodDecl;
        if(ConstructorOrMethodDecl!=null) ConstructorOrMethodDecl.setParent(this);
    }

    public ConstructorOrMethodDeclList getConstructorOrMethodDeclList() {
        return ConstructorOrMethodDeclList;
    }

    public void setConstructorOrMethodDeclList(ConstructorOrMethodDeclList ConstructorOrMethodDeclList) {
        this.ConstructorOrMethodDeclList=ConstructorOrMethodDeclList;
    }

    public ConstructorOrMethodDecl getConstructorOrMethodDecl() {
        return ConstructorOrMethodDecl;
    }

    public void setConstructorOrMethodDecl(ConstructorOrMethodDecl ConstructorOrMethodDecl) {
        this.ConstructorOrMethodDecl=ConstructorOrMethodDecl;
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
        if(ConstructorOrMethodDecl!=null) ConstructorOrMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.traverseTopDown(visitor);
        if(ConstructorOrMethodDecl!=null) ConstructorOrMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstructorOrMethodDeclList!=null) ConstructorOrMethodDeclList.traverseBottomUp(visitor);
        if(ConstructorOrMethodDecl!=null) ConstructorOrMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstructorOrMethodDeclList(\n");

        if(ConstructorOrMethodDeclList!=null)
            buffer.append(ConstructorOrMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstructorOrMethodDecl!=null)
            buffer.append(ConstructorOrMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstructorOrMethodDeclList]");
        return buffer.toString();
    }
}
