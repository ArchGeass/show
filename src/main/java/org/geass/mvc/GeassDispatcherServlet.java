package org.geass.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geass.mvc.annotation.GeassController;
import org.geass.mvc.annotation.GeassGetMapping;
import org.geass.mvc.annotation.GeassRequestParam;
import org.geass.mvc.annotation.GeassResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,上午9:59
 */
public class GeassDispatcherServlet extends HttpServlet {
    Map<Class, Object> container;

    public GeassDispatcherServlet(Map<Class, Object> container) {
        this.container = container;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*String id = req.getParameter("id");
        resp.setHeader("Content-Type", "application/json");
        resp.getOutputStream().print("{\"id\":" + id + "}");
        resp.getOutputStream().flush();*/

        String uri = req.getRequestURI();
        for (Object bean : container.values()) {
            if (bean.getClass().getAnnotation(GeassController.class) != null) {
                for (Method method : bean.getClass().getMethods()) {
                    GeassGetMapping annotation = method.getAnnotation(GeassGetMapping.class);
                    if (annotation != null && annotation.value().equals(uri)) {
                        try {
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            Object[] params = new Object[parameterTypes.length];
                            //springMVC params autowired
                            for (int i = 0; i < parameterTypes.length; i++) {
                                Class<?> parameterType = parameterTypes[i];
                                if (parameterType == HttpServletRequest.class) {
                                    params[i] = req;
                                } else if (parameterType == HttpServletResponse.class) {
                                    params[i] = resp;
                                } else {
                                    Annotation[] annotationOnParams = method.getParameterAnnotations()[i];
                                    for (Annotation annotationOnParam : annotationOnParams) {
                                        if (annotationOnParam.annotationType() == GeassRequestParam.class) {
                                            params[i] = req.getParameter(((GeassRequestParam) annotationOnParam).value());
                                            break;
                                        }
                                    }
                                }
                            }
                            /**
                             * {@link DispatcherServlet#doDispatch()}
                             * mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                             */
                            Object modelAndView = method.invoke(bean, params);
                            if (modelAndView instanceof ModelAndView) {
                                File template = new File(getClass().getResource("/view/" + ((ModelAndView) modelAndView).getViewName() + ".template").toURI());
                                String content = new String(Files.readAllBytes(template.toPath()));
                                for (Map.Entry<String, Object> entry : ((ModelAndView) modelAndView).getModelMap().entrySet()) {
                                    String k = entry.getKey();
                                    Object v = entry.getValue();
                                    content = content.replace("{" + k + "}", v.toString());
                                }
                                resp.setHeader("Content-type", "text/html");
                                resp.getOutputStream().print(content);
                                resp.getOutputStream().flush();
                            } else if (method.getAnnotation(GeassResponseBody.class) != null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                String json = objectMapper.writeValueAsString(modelAndView);
                                resp.setHeader("Content-type", "application/json");
                                resp.getOutputStream().print(json);
                                resp.getOutputStream().flush();
                            }
                        } catch (IllegalAccessException | InvocationTargetException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
