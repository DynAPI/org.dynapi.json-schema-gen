package org.dynapi.jsonschema.gen.schema;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * (XOR) <br>
 * Must be valid against <b>exactly one</b> of the subschemas
 */
public class OneOf extends Schema<OneOf, Void> {
    private final List<Schema<?, ?>> subSchemas = new ArrayList<>();

    public OneOf(Schema<?, ?>... schemas) {
        subSchemas.addAll(List.of(schemas));
    }

    public OneOf addSchema(Schema<?, ?> schema) {
        subSchemas.add(schema);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("oneOf", new JSONArray(subSchemas.stream().map(Schema::getJsonSchema).toList()));
    }
}
