package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * container to use {@link RequiredIf} multiple times
 * @see RequiredIf
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequiredIfAnyOf {
    RequiredIf[] value();
}
