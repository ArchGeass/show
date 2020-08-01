package org.geass.classpy.classfile.descriptor;

public class ReferenceDescriptor implements TypeDescriptor {
    // java.lang.Object
    private String fqcn;
    private String descriptor;

    // Ljava/lang/Object;
    public ReferenceDescriptor(String descriptor) {
        this.descriptor = descriptor;
        this.fqcn = descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
    }

    @Override
    public String getName() {
        return fqcn;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}
