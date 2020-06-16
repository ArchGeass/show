package org.geass.mvc;

import com.google.common.reflect.ClassPath;
import org.apache.catalina.LifecycleException;
import org.geass.mvc.annotation.GeassAutowired;
import org.geass.mvc.annotation.GeassController;
import org.geass.mvc.annotation.GeassService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,下午6:18
 */
public class GeassSpringApplication {
    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, LifecycleException {
        Map<Class, Object> container = new HashMap<>();
        ClassPath classPath = ClassPath.from(GeassSpringApplication.class.getClassLoader());
        List<Class<?>> componentClasses = classPath.getTopLevelClassesRecursive(GeassSpringApplication.class.getPackage().getName())
                .stream().map(ClassPath.ClassInfo::getName).map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(klass -> klass.getAnnotation(GeassController.class) != null
                        || klass.getAnnotation(GeassService.class) != null)
                .collect(Collectors.toList());
        for (Class klass : componentClasses) {
            container.put(klass, klass.getConstructor().newInstance());
        }
        for (Object bean : container.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                //find @GeassAutowired field
                if (field.getAnnotation(GeassAutowired.class) != null) {
                    //set private
                    field.setAccessible(true);
                    //set field value
                    field.set(bean, container.get(field.getType()));
                }
            }
        }

        GeassServer.startServer(new GeassDispatcherServlet(container));
    }
}
