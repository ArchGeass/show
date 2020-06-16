package org.geass.mvc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,下午6:16
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GeassGetMapping {
    String value();
}
