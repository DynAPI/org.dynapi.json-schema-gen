package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * marks a field as required if another one is given
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequiredIf {
    String value();
}
