package org.geass.classpy.classfile.attribute;


import org.geass.classpy.classfile.ClassFilePart;
import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.bytecode.Instruction;
import org.geass.classpy.classfile.bytecode.InstructionFactory;
import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.classfile.datatype.U4;
import org.geass.classpy.classfile.jvm.Opcode;
import org.geass.classpy.common.FilePart;

import java.util.List;

/*
Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[code_length];
    u2 exception_table_length;
    {   u2 start_pc;
        u2 end_pc;
        u2 handler_pc;
        u2 catch_type;
    } exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class CodeAttribute extends AttributeInfo {

    {
        U4 codeLength = new U4();

        u2   ("max_stack");
        u2   ("max_locals");
        add  ("code_length", codeLength);
        add  ("code", new Code(codeLength));
        u2   ("exception_table_length");
        table("exception_table", ExceptionTableEntry.class);
        u2   ("attributes_count");
        table("attributes", AttributeInfo.class);
    }


    public static class ExceptionTableEntry extends ClassFilePart {

        {
            u2  ("start_pc");
            u2  ("end_pc");
            u2  ("handler_pc");
            u2cp("catch_type");
        }

    }


    public class Code extends ClassFilePart {

        private final U4 codeLength;

        public Code(U4 codeLength) {
            this.codeLength = codeLength;
        }

        @Override
        protected void readContent(ClassFileReader reader) {
            final int startPosition = reader.getPosition();
            final int endPosition = startPosition + codeLength.getValue();

            int position;
            while ((position = reader.getPosition()) < endPosition) {
                int pc = position - startPosition;
                byte b = reader.getByte(position);
                Opcode opcode = Opcode.valueOf(Byte.toUnsignedInt(b));
                Instruction instruction = InstructionFactory.create(opcode, pc);
                instruction.read(reader);
                add(instruction);
            }
        }

        @Override
        protected void postRead(ConstantPool cp) {
            List<FilePart> instructions = super.getParts();

            int maxPc = ((Instruction) instructions.get(instructions.size() - 1)).getPc();
            int pcWidth = String.valueOf(maxPc).length();
            String fmtStr = "%0" + pcWidth + "d";
            for (FilePart c : instructions) {
                Instruction instruction = (Instruction) c;
                instruction.setName(String.format(fmtStr, instruction.getPc()));
            }
        }

    }

}
