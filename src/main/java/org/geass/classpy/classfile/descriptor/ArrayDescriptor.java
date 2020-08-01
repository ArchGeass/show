package org.geass.classpy.classfile.descriptor;

public class ArrayDescriptor implements TypeDescriptor {
    // [ -> 1
    // [[ -> 2
    private final String name;
    private final String descriptor;
    private final int dimension;
    private final TypeDescriptor rawType;

    // [[Ljava/lang/Object;
    public ArrayDescriptor(String descriptor) {
        this.descriptor = descriptor;
        this.dimension = countStartingBracket(descriptor);
        if (this.dimension == 0) {
            throw new IllegalArgumentException("Invalid descriptor: " + descriptor);
        }
        this.rawType = TypeDescriptor.parse(descriptor.substring(dimension));
        this.name = rawType.getName() + brackets();
    }

    private String brackets() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }

    private int countStartingBracket(String descriptor) {
        int count = 0;
        for (char ch : descriptor.toCharArray()) {
            if (ch == '[') {
                count++;
            } else {
                return count;
            }
        }
        return count;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public TypeDescriptor getRawType() {
        return rawType;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}
