package org.geass.classpy.classfile.bytecode;


import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.jvm.Opcode;

public class Multianewarray extends Instruction {

    {
        u1  ("opcode");
        u2cp("index");
        u1  ("dimensions");
    }

    public Multianewarray(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        setDesc(getDesc() + " "
                + super.get("index").getDesc() + ", "
                + super.getUInt("dimensions"));
    }
    
}
