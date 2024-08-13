package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * hides properties from a class from being put in the json-schema
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HiddenProperties {
    String[] value();
}
