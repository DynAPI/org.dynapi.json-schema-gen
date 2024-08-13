package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * adds one or more examples to a field <br>
 * note: each field is attempted to be parsed as json
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Examples {
    String[] value();
}
