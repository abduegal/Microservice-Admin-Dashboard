package com.aegal.framework.core.auth.provider.srv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject authenticated principal objects into protected JAX-RS resource
 * methods when expression check is evaluated to true.
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Auth {

    String check() default "";

    boolean required() default true;
}
