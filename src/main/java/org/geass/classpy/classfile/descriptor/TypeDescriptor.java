package org.geass.classpy.classfile.descriptor;

public interface TypeDescriptor {
    /**
     * Get the human-readable name, for example, int, java.lang.Object[]
     *
     * @return the human-readable name
     */
    String getName();

    /**
     * Get the raw descriptor recorded in class files
     *
     * @return the raw descriptor
     */
    String getDescriptor();

    static TypeDescriptor parse(String descriptor) {
        char firstChar = descriptor.charAt(0);
        if (PrimitiveTypeDescriptor.isPrimitive(descriptor)) {
            return PrimitiveTypeDescriptor.of(descriptor);
        } else if (firstChar == '[') {
            return new ArrayDescriptor(descriptor);
        } else if (firstChar == 'L') {
            return new ReferenceDescriptor(descriptor);
        } else {
            throw new IllegalArgumentException("Unrecognized descriptor: " + descriptor);
        }
    }
}







