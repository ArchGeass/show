package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.classfile.datatype.U2CpIndex;
import org.geass.classpy.classfile.jvm.Opcode;

/**
 * The instruction whose operand is U2CpIndex.
 */
public class InstructionCp2 extends Instruction {

    {
        u1("opcode");
        u2cp("operand");
    }

    public InstructionCp2(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("operand").getDesc());
    }

    public int getTargetMethodIndex() {
        if (getOpcode() != Opcode.invokeinterface
                && getOpcode() != Opcode.invokespecial
                && getOpcode() != Opcode.invokestatic
                && getOpcode() != Opcode.invokevirtual) {
            throw new IllegalStateException("Only invokeXX instructions have target method index!");
        }

        return U2CpIndex.class.cast(getParts().get(1)).getValue();
    }

    public int getTargetFieldIndex() {
        if (getOpcode() != Opcode.getstatic) {
            throw new IllegalStateException("Only getstatic instructions have target method index!");
        }
        return U2CpIndex.class.cast(getParts().get(1)).getValue();
    }
}
