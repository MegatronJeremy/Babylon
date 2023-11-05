// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class FormList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private FormList FormList;
    private Type Type;
    private VarType VarType;

    public FormList (FormList FormList, Type Type, VarType VarType) {
        this.FormList=FormList;
        if(FormList!=null) FormList.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.VarType=VarType;
        if(VarType!=null) VarType.setParent(this);
    }

    public FormList getFormList() {
        return FormList;
    }

    public void setFormList(FormList FormList) {
        this.FormList=FormList;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public VarType getVarType() {
        return VarType;
    }

    public void setVarType(VarType VarType) {
        this.VarType=VarType;
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
        if(FormList!=null) FormList.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(VarType!=null) VarType.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormList!=null) FormList.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(VarType!=null) VarType.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormList!=null) FormList.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(VarType!=null) VarType.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormList(\n");

        if(FormList!=null)
            buffer.append(FormList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarType!=null)
            buffer.append(VarType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormList]");
        return buffer.toString();
    }
}
