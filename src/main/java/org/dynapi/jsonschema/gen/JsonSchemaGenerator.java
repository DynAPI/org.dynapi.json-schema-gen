package org.dynapi.jsonschema.gen;

import org.json.JSONObject;

public class JsonSchemaGenerator {
    public static JsonSchemaBuilder builder() {
        return new JsonSchemaBuilder();
    }

    /**
     * converts a class to json-schema
     *
     * @param clazz class marked with {@code @JsonSchemaAble}
     * @return json-schema
     */
    public static JSONObject generateJsonSchema(Class<?> clazz) {
        AnnotationParser.StateData state = new AnnotationParser.StateData();

        JsonSchemaBuilder builder = new JsonSchemaBuilder()
                .title(clazz.getCanonicalName())
                .schema(AnnotationParser.generateJsonSchemaForObject(state, clazz));

        for (var ref : state.references.entrySet())
            builder.addDefinition(ref.getKey(), ref.getValue());

        return builder.build();
    }

    /**
     * converts a class to json-schema and returns it as json-string
     *
     * @param clazz class marked with {@code @JsonSchemaAble}
     * @return json-schema as string
     */
    public static String generateJsonSchemaAsString(Class<?> clazz) {
        JSONObject jsonSchema = generateJsonSchema(clazz);
        return jsonSchema.toString(2);
    }
}
