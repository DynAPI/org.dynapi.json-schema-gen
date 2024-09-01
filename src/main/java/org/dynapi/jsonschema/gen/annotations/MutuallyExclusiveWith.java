package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * marks a field as mutually exclusive with another one.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MutuallyExclusiveWith {
    String[] value() default {};
}
