package org.geass.controller;

import org.geass.starter.MyRequestBody;
import org.geass.starter.MyResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class MyController {
    @GetMapping("/users")
    @MyResponseBody
    public List<User> users() {
        return Arrays.asList(new User(1, "A"), new User(2, "B"));
    }

    @PostMapping("/get/mymap")
//    @MyResponseBody
    public String myMap(@MyRequestBody Map<String, String> map) {
        System.out.println(map);
        return map.toString();
    }

    @PostMapping("/get/debug")
    @ResponseBody
    public String debug(@RequestBody Map<String, String> map) {
        System.out.println(map);
        return map.toString();
    }

    public static class User {
        Integer id;
        String name;

        public User(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
