package org.dynapi.jsonschemagen;

import org.json.JSONObject;

public class JsonSchemaGenerator {
    public static JSONObject generateJsonSchema(Class<?> clazz) {
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        jsonSchema.put("title", clazz.getSimpleName());
        jsonSchema.put("description", clazz.getCanonicalName());
        jsonSchema.put("type", "object");
        return jsonSchema;
    }

    public static String generateJsonSchemaAsString(Class<?> clazz) {
        JSONObject jsonSchema = generateJsonSchema(clazz);
        return jsonSchema.toString(2);
    }
}
