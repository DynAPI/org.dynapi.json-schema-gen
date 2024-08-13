package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * adds some limitations to a field
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Constraints {

    // common constraints

    String[] options() default {};
    String option() default "";

    // string constrains

    int minLength() default -1;
    int maxLength() default -1;

    String pattern() default "";
    String format() default "";

    // numeric constraints

    double multipleOf() default -1;

    double ge() default -1;
    double gt() default -1;
    double le() default -1;
    double lt() default -1;

    // object constraints

    int minProperties() default -1;
    int maxProperties() default -1;

    // array constraints

    int minItems() default -1;
    int maxItems() default -1;

    boolean uniqueItems() default false;
}
