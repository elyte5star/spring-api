package com.elyte.utils;

import java.lang.reflect.Method;
import org.springframework.util.StringUtils;

public class CustomKeyGenerator {

    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "_"
          + method.getName() + "_"
          + StringUtils.arrayToDelimitedString(params, "_");
    }
    
}
