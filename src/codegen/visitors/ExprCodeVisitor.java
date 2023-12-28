package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;

public class ExprCodeVisitor extends VisitorAdaptor {
    public void visit(RelopEqual relopEqual) {relopEqual.integer = Code.eq;}
    public void visit(RelopNotEqual relopNotEqual) {relopNotEqual.integer = Code.ne;}
    public void visit(RelopGreater relopGreater) {relopGreater.integer = Code.gt;}
    public void visit(RelopGreaterEqual relopGreaterEqual) {relopGreaterEqual.integer = Code.ge;}
    public void visit(RelopLess relopLess) {relopLess.integer = Code.lt;}
    public void visit(RelopLessEqual relopLessEqual) {relopLessEqual.integer = Code.le;}
    public void visit(AddopPlus addopPlus) {addopPlus.integer = Code.add;}
    public void visit(AddopMinus addopMinus) {addopMinus.integer = Code.sub;}
    public void visit(MulopMultiply mulopMultiply) {mulopMultiply.integer = Code.mul;}
    public void visit(MulopDivide mulopDivide) {mulopDivide.integer = Code.div;}
    public void visit(MulopModulo mulopModulo) {mulopModulo.integer = Code.rem;}
}
