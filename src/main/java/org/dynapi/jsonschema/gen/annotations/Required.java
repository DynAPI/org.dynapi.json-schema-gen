package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * marks a field as required
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
}
