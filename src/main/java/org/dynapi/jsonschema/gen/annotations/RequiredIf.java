package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * marks a field as required if another one is given. <br>
 * note: can be used multiple times.
 * @see RequiredIfAnyOf
 */
@Documented
@Repeatable(RequiredIfAnyOf.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequiredIf {
    String value();
}

