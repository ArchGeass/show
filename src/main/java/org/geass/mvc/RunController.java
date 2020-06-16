package org.geass.mvc;

import org.geass.mvc.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,下午6:21
 */
@GeassController
public class RunController {

    @GeassAutowired
    private RunService runService;

    @GeassGetMapping("/hello")
    public ModelAndView hello(@GeassRequestParam("name") String name,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().put("greeting", runService.sayHello(name));
        modelAndView.getModelMap().put("name", name);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @GeassGetMapping("/json")
    @GeassResponseBody
    public Object json(@GeassRequestParam("name") String name,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        return Arrays.asList(runService.sayHello(name));
    }
}
