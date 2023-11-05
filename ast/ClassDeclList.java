// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class ClassDeclList extends ClassDecl {

    private String I1;
    private ExtendsClause ExtendsClause;
    private ClassBody ClassBody;

    public ClassDeclList (String I1, ExtendsClause ExtendsClause, ClassBody ClassBody) {
        this.I1=I1;
        this.ExtendsClause=ExtendsClause;
        if(ExtendsClause!=null) ExtendsClause.setParent(this);
        this.ClassBody=ClassBody;
        if(ClassBody!=null) ClassBody.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public ExtendsClause getExtendsClause() {
        return ExtendsClause;
    }

    public void setExtendsClause(ExtendsClause ExtendsClause) {
        this.ExtendsClause=ExtendsClause;
    }

    public ClassBody getClassBody() {
        return ClassBody;
    }

    public void setClassBody(ClassBody ClassBody) {
        this.ClassBody=ClassBody;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExtendsClause!=null) ExtendsClause.accept(visitor);
        if(ClassBody!=null) ClassBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExtendsClause!=null) ExtendsClause.traverseTopDown(visitor);
        if(ClassBody!=null) ClassBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExtendsClause!=null) ExtendsClause.traverseBottomUp(visitor);
        if(ClassBody!=null) ClassBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclList(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(ExtendsClause!=null)
            buffer.append(ExtendsClause.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassBody!=null)
            buffer.append(ClassBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclList]");
        return buffer.toString();
    }
}
