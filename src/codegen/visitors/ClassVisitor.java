package codegen.visitors;

import ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClassVisitor extends VisitorAdaptor {
    Integer previousFixupAddr = null;

    private void startStaticInitBlock() {
        if (previousFixupAddr == null) {
            // this is new pc start
            CodeGenerator.getInstance().mainPC = Code.pc;
        } else {
            // else link the previous jump
            Code.fixup(previousFixupAddr);
        }
    }

    private void endStaticInitBlock() {
        // jump to next block or to pc optimistically
        Code.putJump(0);

        // put both for now - later the false one will be overwritten
        previousFixupAddr = Code.pc - 2;
        CodeGenerator.getInstance().mainFixupAddr = previousFixupAddr;
    }

    public void visit(ExtendsClauseExists extendsClauseExists) {
        Struct superClassType = extendsClauseExists.getType().struct;

        Struct currentClassType = CodeGenerator.getInstance().currentClass.getType();
        assert currentClassType != null;

        Collection<Obj> superMembers = superClassType.getMembers();
        for (Obj obj : superMembers) {
            Obj cloned = currentClassType.getMembersTable().searchKey(obj.getName());
            cloned.setAdr(obj.getAdr()); // set address of cloned object here
        }
    }

    public void visit(StaticInitializerStart staticInitializerStart) {
        startStaticInitBlock();
    }

    public void visit(StaticInitializer staticInitializer) {
        endStaticInitBlock();
    }

    public void visit(ClassName className) {
        CodeGenerator.getInstance().currentClass = className.obj;
    }

    public void visit(ClassDecl classDecl) {
        Struct classType = CodeGenerator.getInstance().currentClass.getType();

        // generate virtual function table here
        List<Obj> methods = classType.getMembers()
                .stream().filter(obj -> obj.getKind() == Obj.Meth)
                .collect(Collectors.toList());

        // treat this as a static init block
        startStaticInitBlock();

        // add vftp to global memory and set the class pointer value
        CodeGenerator.getInstance().vftpMap.put(classType, Code.dataSize); // adr is equal to this value

        Consumer<Integer> writeBytecode =
                integer -> {
                    Code.loadConst(integer);
                    Code.put(Code.putstatic); // create new static var
                    Code.put2(Code.dataSize);
                    Code.dataSize++;
                };

        for (Obj method : methods) {
            for (char c : method.getName().toCharArray()) {
                writeBytecode.accept((int) c);
            }
            writeBytecode.accept(-1); // name terminator
            writeBytecode.accept(method.getAdr()); // actual address
        }
        writeBytecode.accept(-2); // end of vft for this class

        endStaticInitBlock();

        CodeGenerator.getInstance().currentClass = null;
    }
}
