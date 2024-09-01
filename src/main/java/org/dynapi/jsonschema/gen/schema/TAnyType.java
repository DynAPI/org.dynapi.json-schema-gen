package org.dynapi.jsonschema.gen.schema;

import lombok.*;
import org.json.JSONObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TAnyType extends Schema<TAnyType, Object> {
    @Override
    protected JSONObject extraSchemaData() {
        // empty object should do
        return new JSONObject();
    }
}
