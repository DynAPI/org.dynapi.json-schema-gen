package org.dynapi.jsonschema.gen.schema;

import lombok.*;
import org.json.JSONObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JRef extends Schema<JRef, Void> {
    private final String ref;

    public JRef(@NonNull String ref) {
        this.ref = ref;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("$ref", "#/$defs/" + ref);
    }
}
