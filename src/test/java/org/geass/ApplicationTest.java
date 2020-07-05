package org.geass;


import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import org.geass.controller.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @Autowired
    Environment environment;

    @Test
    public void test() throws Exception {
        String json = HttpRequest.get("http://localhost:" + environment.getProperty("local.server.port") + "/users")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        System.out.println(json);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String status = jsonObject.getString("status");
        List<?> data = jsonObject.getJSONArray("data");

        Assertions.assertEquals("ok", status);
        Assertions.assertEquals(2, data.size());
    }
}
