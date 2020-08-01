package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.jvm.Opcode;

public class Sipush extends Instruction {

    public Sipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        short operand = reader.readShort();
        setDesc(getDesc() + " " + operand);
    }
    
}
