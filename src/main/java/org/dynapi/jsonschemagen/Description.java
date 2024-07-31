package org.dynapi.jsonschemagen;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String value();
}
