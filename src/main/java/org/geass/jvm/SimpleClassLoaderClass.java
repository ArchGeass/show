package org.geass.jvm;

public class SimpleClassLoaderClass {
    public static void main(String[] args) throws ClassNotFoundException {
        MyClassLoader myClassLoader = new MyClassLoader();
        MiniJVMClass klass = myClassLoader.loadClass("org.geass.jvm.SimpleClass");
        //预期会抛出ClassCastException
        SimpleClass simpleClass = (SimpleClass) klass.newInstance();
    }
}
