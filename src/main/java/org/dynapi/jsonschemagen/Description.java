package org.dynapi.jsonschemagen;

import java.lang.annotation.*;

/**
 * adds a description to a class or a field
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String value();
}
