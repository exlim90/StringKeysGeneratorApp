package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vladimir.angeleski on 23/05/2017.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface StringKeysGenerator {
    String className() default "TranslationKeys";
    String packageName() default "com.vladimir.generated";
    String stringsPath() default "app/src/main/res/values/strings.xml";
}
