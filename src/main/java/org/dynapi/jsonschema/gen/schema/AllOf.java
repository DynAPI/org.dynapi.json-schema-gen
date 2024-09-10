package org.dynapi.jsonschema.gen.schema;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * (AND) <br>
 * Must be valid against <b>all</b> of the subschemas
 */
public class AllOf extends Schema<AllOf, Void> {
    private final List<Schema<?, ?>> subSchemas = new ArrayList<>();

    public AllOf(Schema<?, ?>... schemas) {
        this(List.of(schemas));
    }

    public AllOf(List<? extends Schema<?, ?>> schemas) {
        subSchemas.addAll(schemas);
    }

    public AllOf addSchema(Schema<?, ?> schema) {
        subSchemas.add(schema);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("allOf", new JSONArray(subSchemas.stream().map(Schema::getJsonSchema).toList()));
    }
}
