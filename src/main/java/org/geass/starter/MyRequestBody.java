package org.geass.starter;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/7/4,下午3:12
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestBody {
}
