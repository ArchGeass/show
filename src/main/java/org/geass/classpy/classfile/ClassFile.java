package org.geass.classpy.classfile;


import org.geass.classpy.classfile.attribute.AttributeInfo;
import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.classfile.datatype.Table;
import org.geass.classpy.classfile.datatype.U2;
import org.geass.classpy.classfile.datatype.U2CpIndex;
import org.geass.classpy.classfile.jvm.AccessFlagType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 按照JVM规范生成字节码文件解析库
 *
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
*/
public class ClassFile extends ClassFilePart {

    {
        U2 cpCount = new U2();

        u4hex("magic");
        u2("minor_version");
        u2("major_version");
        add("constant_pool_count", cpCount);
        add("constant_pool", new ConstantPool(cpCount));
        u2af("access_flags", AccessFlagType.AF_CLASS);
        u2cp("this_class");
        u2cp("super_class");
        u2("interfaces_count");
        table("interfaces", U2CpIndex.class);
        u2("fields_count");
        table("fields", FieldInfo.class);
        u2("methods_count");
        table("methods", MethodInfo.class);
        u2("attributes_count");
        table("attributes", AttributeInfo.class);
    }

    public ConstantPool getConstantPool() {
        return (ConstantPool) super.get("constant_pool");
    }

    public Table getMethods() {
        return (Table) getParts()
                .stream()
                .filter(part -> part instanceof Table && part.getName().equals("methods"))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public List<MethodInfo> getMethod(String methodName) {
        return (List) getMethods().getParts()
                .stream()
                .filter(part -> part instanceof MethodInfo && part.getDesc().equals(methodName))
                .collect(Collectors.toList());
    }
}
