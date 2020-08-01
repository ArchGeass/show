package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.jvm.Opcode;

public class Bipush extends Instruction {

    public Bipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        byte operand = reader.readByte();
        setDesc(getDesc() + " " + operand);
    }

    public int getOperand() {
        return Integer.parseInt(getDesc().substring("bipush ".length()));
    }
}
