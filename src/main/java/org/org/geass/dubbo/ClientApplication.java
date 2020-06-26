package org.org.geass.dubbo;

import java.io.IOException;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/26,下午12:42
 */
public class ClientApplication {
    public static void main(String[] args) throws IOException {
        GreetingsService service = new MyDubboClient<>(GreetingsService.class).getRef();
        System.out.println(service.sayHi("GreetingsService"));
    }
}
