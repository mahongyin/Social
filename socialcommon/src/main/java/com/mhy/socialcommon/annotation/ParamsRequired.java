package com.mhy.socialcommon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Function：
 * Desc：带有这个注解的参数表示必传
 * @author mahongyin
 */
@Target(ElementType.PARAMETER)
public @interface ParamsRequired {
}
