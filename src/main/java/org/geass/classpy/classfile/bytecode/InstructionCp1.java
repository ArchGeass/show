package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.classfile.jvm.Opcode;

/**
 * The instruction whose operand is U1CpIndex.
 */
public class InstructionCp1 extends Instruction {

    {
        u1  ("opcode");
        u1cp("operand");
    }

    public InstructionCp1(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("operand").getDesc());
    }

}
