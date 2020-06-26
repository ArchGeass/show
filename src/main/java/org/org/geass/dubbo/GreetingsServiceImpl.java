package org.org.geass.dubbo;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/26,下午12:43
 */
public class GreetingsServiceImpl implements GreetingsService {
    @Override
    public String sayHi(String name) {
        return "hi, " + name;
    }
}
