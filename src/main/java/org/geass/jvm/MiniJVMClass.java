package org.geass.jvm;

import org.geass.classpy.classfile.ClassFile;
import org.geass.classpy.classfile.MethodInfo;
import org.geass.classpy.classfile.datatype.Table;

import java.util.List;
import java.util.Map;

public class MiniJVMClass {

    private String name;

    private MiniJVMClassLoader classLoader;

    private ClassFile classFile;

    public MiniJVMClass(String name, MiniJVMClassLoader classLoader, ClassFile classFile) {
        this.name = name;
        this.classLoader = classLoader;
        this.classFile = classFile;
    }

    /**
     * {@link Class#getClassLoader}
     */
    public MiniJVMClassLoader getClassLoader() {
        return classLoader;
    }

    public Object newInstance() {
        if (name.contains("MyClassLoader")) {
            return new MiniJVMObject(this, new MyClassLoader());
        } else if (name.contains("SimpleClass")) {
            return new MiniJVMObject(this, new SimpleClass());
        } else {
            throw new IllegalStateException("Not implemented yet!");
        }
    }

    public Table getMethods() {
        return classFile.getMethods();
    }

    public String getName() {
        return name;
    }

    public List<MethodInfo> getMethod(String methodName) {
        return classFile.getMethod(methodName);
    }

    public ClassFile getClassFile() {
        return classFile;
    }
}
