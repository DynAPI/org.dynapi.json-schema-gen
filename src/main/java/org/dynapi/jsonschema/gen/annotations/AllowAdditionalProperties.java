package org.dynapi.jsonschema.gen.annotations;

import java.lang.annotation.*;

/**
 * allows additional properties on an object
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AllowAdditionalProperties {
}
