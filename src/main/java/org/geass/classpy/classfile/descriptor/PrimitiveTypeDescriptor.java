package org.geass.classpy.classfile.descriptor;

import java.util.stream.Stream;

public enum PrimitiveTypeDescriptor implements TypeDescriptor {
    BYTE("B"),
    CHAR("C"),
    DOUBLE("D"),
    FLOAT("F"),
    INT("I"),
    LONG("J"),
    SHORT("S"),
    BOOLEAN("Z"),
    VOID("V");
    private String descriptor;

    PrimitiveTypeDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public static PrimitiveTypeDescriptor of(String descriptor) {
        return Stream.of(values()).filter(e -> e.getDescriptor().equals(descriptor)).findFirst().get();
    }

    public static boolean isPrimitive(String descriptor) {
        return descriptor.length() == 1
                && Stream.of(values()).anyMatch(e -> e.getDescriptor().charAt(0) == descriptor.charAt(0));
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}
