// generated with ast extension for cup
// version 0.8
// 5/10/2023 21:58:9


package ast;

public class MulopMultiply extends Mulop {

    public MulopMultiply () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MulopMultiply(\n");

        buffer.append(tab);
        buffer.append(") [MulopMultiply]");
        return buffer.toString();
    }
}
