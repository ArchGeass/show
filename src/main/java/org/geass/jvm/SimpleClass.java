package org.geass.jvm;

/**
 * @Description: run with MiniJVM
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午2:41
 */
public class SimpleClass {
    public static void main(String[] args) {
        System.out.println(foo());
    }

    private static int foo() {
        return 68;
    }
}
