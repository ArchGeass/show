package org.geass.mvc;

import org.geass.mvc.annotation.GeassService;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,下午6:24
 */
@GeassService
public class RunService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
