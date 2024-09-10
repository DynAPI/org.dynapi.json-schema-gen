package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * can be used to generate schema based on some implementations of an interface or a subclass
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Implementations {
    Class<?>[] value();
}
