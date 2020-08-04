package org.geass.jvm;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/8/4,下午9:49
 */
public class InstanceofClass {
    public static void main(String[] args) throws ClassNotFoundException {
        MyClassLoader myClassLoader = new MyClassLoader();
        final MiniJVMClass klass = myClassLoader.loadClass("org.geass.jvm.SimpleClass");

        // 预期输出false
        System.out.println(klass.newInstance() instanceof SimpleClass);
    }
}
