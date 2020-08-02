package org.geass.jvm;

/**
 * @Description: 用于测试if分支调转
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午10:21
 */
public class BranchClass {
    public static void main(String[] args) {
        System.out.println(foo(111));
    }

    private static int foo(int i) {
        if (i % 2 == 0) {
            return 100;
        } else {
            return 200;
        }
    }
}
