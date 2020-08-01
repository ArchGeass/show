package org.geass.classpy.classfile.descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MethodDescriptor implements TypeDescriptor {
    private static final Pattern TYPE_DESCRIPTOR_PATTERN = Pattern.compile("\\[*([BCDFIJSZV]|L(\\w|\\$|/)+;)");
    private final List<TypeDescriptor> paramTypes = new ArrayList<>();
    private final TypeDescriptor returnType;
    private final String descriptor;
    private final String name;

    // Object m(int i, double d, Thread t) {...}
    // (IDLjava/lang/Thread;)Ljava/lang/Object;
    public MethodDescriptor(String descriptor) {
        this.descriptor = descriptor;

        assertValid(descriptor.charAt(0) == '(');

        int rightParenthesisIndex = descriptor.indexOf(')');
        assertValid(rightParenthesisIndex != -1);

        returnType = TypeDescriptor.parse(descriptor.substring(rightParenthesisIndex + 1));

        Matcher matcher = TYPE_DESCRIPTOR_PATTERN.matcher(descriptor.substring(1, rightParenthesisIndex));

        while (matcher.find()) {
            paramTypes.add(TypeDescriptor.parse(matcher.group(0)));
        }

        this.name = returnType.getName()
                + " ("
                + paramTypes.stream().map(TypeDescriptor::getName).collect(Collectors.joining(", "))
                + ")";
    }

    private void assertValid(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("Invalid descriptor: " + descriptor);
        }
    }

    public List<TypeDescriptor> getParamTypes() {
        return paramTypes;
    }

    public TypeDescriptor getReturnType() {
        return returnType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }
}

