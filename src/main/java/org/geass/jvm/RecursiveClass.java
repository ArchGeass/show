package org.geass.jvm;

/**
 * @Description: 用于测试第归方法调用
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午10:22
 */
public class RecursiveClass {
    public static void main(String[] args) {
        System.out.println(factorial(5));
    }

    private static int factorial(int i) {
        if (i == 0) {
            return 1;
        } else {
            return i * factorial(i - 1);
        }
    }
}
