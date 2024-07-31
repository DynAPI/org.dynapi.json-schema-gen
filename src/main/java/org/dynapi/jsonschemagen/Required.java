package org.dynapi.jsonschemagen;

import java.lang.annotation.*;

/**
 * marks a field as required
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
}
