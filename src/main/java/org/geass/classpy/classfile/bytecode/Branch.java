package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.jvm.Opcode;

public class Branch extends Instruction {

    public Branch(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        short offset = reader.readShort();
        int jmpTo = pc + offset;
        setDesc(getDesc() + " " + jmpTo);
    }
    
}
