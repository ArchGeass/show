package org.geass.jvm;

public class MyClassLoader extends MiniJVMClassLoader {
    public MyClassLoader() {
        super(new String[]{"target/classes"}, null);
    }

    @Override
    public MiniJVMClass loadClass(String name) throws ClassNotFoundException {
        return findAndDefineClass(name);
    }
}
