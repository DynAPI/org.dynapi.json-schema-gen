package org.dynapi.jsonschema.gen.schema;

import org.json.JSONObject;

/**
 * (NOT) <br>
 * Must <b>not</b> be valid against the given schema
 */
public class Not extends Schema<Not, Void> {
    private final Schema<?, ?> schema;

    public Not(Schema<?, ?> schema) {
        this.schema = schema;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("not", schema.getJsonSchema());
    }
}
