// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class ClassBody implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private VarDeclList VarDeclList;
    private OperationDeclBody OperationDeclBody;

    public ClassBody (VarDeclList VarDeclList, OperationDeclBody OperationDeclBody) {
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
        this.OperationDeclBody=OperationDeclBody;
        if(OperationDeclBody!=null) OperationDeclBody.setParent(this);
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public OperationDeclBody getOperationDeclBody() {
        return OperationDeclBody;
    }

    public void setOperationDeclBody(OperationDeclBody OperationDeclBody) {
        this.OperationDeclBody=OperationDeclBody;
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
        if(VarDeclList!=null) VarDeclList.accept(visitor);
        if(OperationDeclBody!=null) OperationDeclBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
        if(OperationDeclBody!=null) OperationDeclBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        if(OperationDeclBody!=null) OperationDeclBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassBody(\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OperationDeclBody!=null)
            buffer.append(OperationDeclBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassBody]");
        return buffer.toString();
    }
}
