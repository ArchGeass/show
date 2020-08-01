package org.geass.classpy.classfile.constant;


import org.geass.classpy.classfile.datatype.U2;

/*
CONSTANT_Methodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
*/
public class ConstantMethodrefInfo extends ConstantFieldrefInfo {


    public ConstantNameAndTypeInfo getMethodNameAndType(ConstantPool constantPool) {
        int index = U2.class.cast(getParts().get(2)).getValue();
        return constantPool.getNameAndTypeInfo(index);
    }

}
