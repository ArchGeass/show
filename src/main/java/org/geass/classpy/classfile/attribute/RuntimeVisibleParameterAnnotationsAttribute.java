package org.geass.classpy.classfile.attribute;


import org.geass.classpy.classfile.ClassFilePart;
import org.geass.classpy.classfile.attribute.RuntimeVisibleAnnotationsAttribute.AnnotationInfo;

/*
RuntimeVisibleParameterAnnotations_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 num_parameters;
    {   u2         num_annotations;
        annotation annotations[num_annotations];
    } parameter_annotations[num_parameters];
}
 */
public class RuntimeVisibleParameterAnnotationsAttribute extends AttributeInfo {

    {
        u1   ("num_parameters");
        table("parameter_annotations", ParameterAnnotationInfo.class);
    }
    
    
    public static class ParameterAnnotationInfo extends ClassFilePart {

        {
            u2   ("num_annotations");
            table("annotations", AnnotationInfo.class);
        }
        
    }
    
}
