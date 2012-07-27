package org.phw.eop.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface EopParam {
    String name() default "";

    boolean optinal() default false;

    boolean encrypted() default false;

    int minlen() default 0;

    int maxlen() default 0;

    String range() default "";

    String regex() default "";

    String clazz() default "";
}
