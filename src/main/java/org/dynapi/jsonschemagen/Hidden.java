package org.dynapi.jsonschemagen;

import java.lang.annotation.*;

/**
 * hides a field from being put in the json-schema
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Hidden {
}
