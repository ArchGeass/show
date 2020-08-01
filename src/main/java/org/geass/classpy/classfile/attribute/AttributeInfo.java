package org.geass.classpy.classfile.attribute;


import org.geass.classpy.classfile.ClassFilePart;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public abstract class AttributeInfo extends ClassFilePart {

    {
        u2("attribute_name_index");
        u4("attribute_length");
    }

}
