package org.geass.jvm;

public class MiniJVMObject {
    private MiniJVMClass klass;

    private Object realJavaObject;

    public MiniJVMObject(MiniJVMClass klass, Object realJavaObject) {
        this.klass = klass;
        this.realJavaObject = realJavaObject;
    }

    public MiniJVMClass getKlass() {
        return klass;
    }

    public Object getRealJavaObject() {
        return realJavaObject;
    }
}
