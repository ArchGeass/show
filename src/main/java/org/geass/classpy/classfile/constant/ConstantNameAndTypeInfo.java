package org.geass.classpy.classfile.constant;


import org.geass.classpy.classfile.datatype.U2;

/*
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
*/
public class ConstantNameAndTypeInfo extends ConstantInfo {

    {
        u2("name_index");
        u2("descriptor_index");
    }

    public int getNameIndex() {
        return super.getUInt("name_index");
    }

    @Override
    protected String loadDesc(ConstantPool cp) {
        String name = cp.getUtf8String(super.getUInt("name_index"));
        String type = cp.getUtf8String(super.getUInt("descriptor_index"));
        return name + "&" + type;
    }

    public String getName(ConstantPool constantPool) {
        int index = U2.class.cast(getParts().get(1)).getValue();
        return constantPool.getUtf8String(index);
    }

    public String getType(ConstantPool constantPool) {
        int index = U2.class.cast(getParts().get(2)).getValue();
        return constantPool.getUtf8String(index);
    }
    
}
