package org.dynapi.jsonschema.gen.schema;

import lombok.*;
import org.json.JSONObject;

import java.util.regex.Pattern;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JString extends Schema<JString, String> {
    /**
     *
     * @param minLength minimum length in characters
     */
    public JString minLength(int minLength) {
        options.put("minLength", minLength);
        return this;
    }

    /**
     * @param maxLength maximum length in characters
     */
    public JString maxLength(int maxLength) {
        options.put("maxLength", maxLength);
        return this;
    }

    /**
     * @param pattern regex pattern the value should match
     */
    public JString pattern(@NonNull String pattern) {
        options.put("pattern", pattern);
        return this;
    }

    /**
     * @param pattern regex pattern the value should match
     */
    public JString pattern(@NonNull Pattern pattern) {
        options.put("pattern", pattern.pattern());
        return this;
    }

    @Override
    public JSONObject extraSchemaData() {
        return new JSONObject()
                .put("type", "string");
    }
}
