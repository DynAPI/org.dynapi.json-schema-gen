package org.dynapi.jsonschema.gen;

import lombok.NonNull;
import org.dynapi.jsonschema.gen.schema.Schema;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonSchemaBuilder {
    public final static String SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";

    protected String title = null;
    protected Schema<?, ?> schema = null;
    protected Map<String, Schema<?, ?>> definitions = new HashMap<>();

    public JsonSchemaBuilder title(@NonNull String title) {
        this.title = title;
        return this;
    }

    public JsonSchemaBuilder schema(@NonNull Schema<?, ?> schema) {
        this.schema = schema;
        return this;
    }

    public JsonSchemaBuilder addDefinition(@NonNull String name, @NonNull Schema<?, ?> definition) {
        definitions.put(name, definition);
        return this;
    }

    public JSONObject build() {
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("$schema", SCHEMA_VERSION);
        jsonSchema.put("title", title);

        JSONObject rootSchema = schema.getJsonSchema();
        System.out.println("Root-Schema: " + rootSchema);
        for (String key : rootSchema.keySet())
            jsonSchema.put(key, rootSchema.get(key));

        JSONObject definitions = new JSONObject();
        jsonSchema.put("$defs", definitions);
        for (var ref : this.definitions.entrySet())
            definitions.put(ref.getKey(), ref.getValue().getJsonSchema());

        return jsonSchema;
    }

    public String toString() {
        return build().toString();
    }

    public String toString(int indentFactor) {
        return build().toString(indentFactor);
    }
}
