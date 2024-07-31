package org.dynapi.jsonschemagen;

import java.lang.annotation.*;

/**
 * marks a class as usable for JsonSchemaGenerator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonSchemaAble {
}
