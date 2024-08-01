package org.dynapi.jsonschemagen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class JsonSchemaGenerator {
    /**
     * converts a class to json-schema
     *
     * @param clazz class marked with {@code @JsonSchemaAble}
     * @return json-schema
     */
    public static JSONObject generateJsonSchema(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(JsonSchemaAble.class))
            throw new RuntimeException(String.format("%s is not marked as @JsonSchemaAble", clazz.getCanonicalName()));

        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        jsonSchema.put("title", clazz.getSimpleName());

        JSONObject objectSchema = generateJsonSchemaForObject(clazz);
        for (String key : objectSchema.keySet())
            jsonSchema.put(key, objectSchema.get(key));

        return jsonSchema;
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

    private static JSONObject generateJsonSchemaForObject(Class<?> clazz) {
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("type", "object");
        jsonSchema.put("additionalProperties", false);
        applyDescription(clazz.getAnnotation(Description.class), jsonSchema);

        JSONArray required = new JSONArray();
        JSONObject dependentRequired = new JSONObject();

        JSONObject properties = new JSONObject();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Hidden.class)) continue;

            JSONObject fieldSchema = generateJsonSchemaForField(field.getType());

            applyDescription(field.getAnnotation(Description.class), fieldSchema);
            applyExamples(field.getAnnotation(Examples.class), fieldSchema);
            applyConstraints(field.getAnnotation(Constraints.class), fieldSchema);
            if (field.isAnnotationPresent(Deprecated.class))
                fieldSchema.put("deprecated", true);
            if (field.isAnnotationPresent(Required.class))
                required.put(field.getName());
            RequiredIf requiredIf = field.getAnnotation(RequiredIf.class);
            if (requiredIf != null) {
                String ifField = requiredIf.value();
                if (dependentRequired.has(ifField))
                    dependentRequired.getJSONArray(ifField).put(field.getName());
                else
                    dependentRequired.put(ifField, new JSONArray().put(field.getName()));
            }

            properties.put(field.getName(), fieldSchema);
        }
        jsonSchema.put("properties", properties);

        if (!required.isEmpty())
            jsonSchema.put("required", required);

        return jsonSchema;
    }

    private static JSONObject generateJsonSchemaForField(Class<?> clazz) {
        JSONObject jsonSchema = new JSONObject();

        if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            jsonSchema.put("type", SchemaTypes.INTEGER);
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            jsonSchema.put("type", SchemaTypes.INTEGER);
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            jsonSchema.put("type", SchemaTypes.NUMBER);
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            jsonSchema.put("type", SchemaTypes.NUMBER);
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            jsonSchema.put("type", SchemaTypes.BOOLEAN);
        } else if (clazz.equals(String.class)) {
            jsonSchema.put("type", SchemaTypes.STRING);
        } else if (clazz.equals(Map.class)) {
            jsonSchema.put("type", SchemaTypes.OBJECT);
        } else if (clazz.equals(Object.class)) {
            // todo: verify if not type is any
//            jsonSchema.put("type", SchemaTypes.OBJECT);
        } else if (clazz.isArray()) {  // todo: support for List?
            JSONObject objectSchema = generateJsonSchemaForArrayField(clazz);
            for (String key : objectSchema.keySet())
                jsonSchema.put(key, objectSchema.get(key));
        } else {
            JSONObject objectSchema = generateJsonSchemaForObject(clazz);
            for (String key : objectSchema.keySet())
                jsonSchema.put(key, objectSchema.get(key));
        }
        return jsonSchema;
    }

    private static JSONObject generateJsonSchemaForArrayField(Class<?> clazz) {
        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put("type", "array");
        JSONObject items = new JSONObject();
        JSONObject itemType = generateJsonSchemaForField(clazz.getComponentType());
        for (String key : itemType.keySet())
            items.put(key, itemType.get(key));
        jsonSchema.put("items", items);
        return jsonSchema;
    }

    private static void applyDescription(Description meta, JSONObject object) {
        if (meta == null) return;
        object.put("description", meta.value());
    }

    private static void applyExamples(Examples examples, JSONObject object) {
        if (examples == null) return;
        object.put("examples", Arrays.stream(examples.value()).map(JsonSchemaGenerator::parseStringToJsonX).toList());
    }

    private static void applyConstraints(Constraints constraints, JSONObject object) {
        if (constraints == null) return;

        // common constraints

        if (!constraints.option().isEmpty())
            object.put("const", new JSONObject(constraints.option()));
        if (constraints.options().length > 0)
            object.put("enum", Arrays.stream(constraints.options()).map(JsonSchemaGenerator::parseStringToJsonX).toList());

        // string constrains

        if (constraints.minLength() != -1)
            object.put("minLength", constraints.minLength());
        if (constraints.maxLength() != -1)
            object.put("maxLength", constraints.maxLength());

        if (!constraints.pattern().isEmpty())
            object.put("pattern", constraints.pattern());

        if (!constraints.format().isEmpty())
            object.put("format", constraints.format());

        // numeric constraints

        if (constraints.multipleOf() != -1)
            object.put("multipleOf", constraints.multipleOf());

        if (constraints.ge() != -1)
            object.put("minimum", constraints.ge());
        if (constraints.gt() != -1)
            object.put("exclusiveMinimum", constraints.gt());
        if (constraints.le() != -1)
            object.put("maximum", constraints.le());
        if (constraints.lt() != -1)
            object.put("exclusiveMaximum", constraints.lt());

        // object constraints

        if (constraints.minProperties() != -1)
            object.put("minProperties", constraints.minProperties());
        if (constraints.maxProperties() != -1)
            object.put("maxProperties", constraints.maxProperties());

        // array constraints

        if (constraints.minItems() != -1)
            object.put("minItems", constraints.minItems());
        if (constraints.maxItems() != -1)
            object.put("maxItems", constraints.maxItems());

        if (constraints.uniqueItems())
            object.put("uniqueItems", true);
    }

    private static Object parseStringToJsonX(String string) {
        if (string.startsWith("{") && string.endsWith("}")) return new JSONObject(string);
        if (string.startsWith("[") && string.endsWith("]")) return new JSONArray(string);
        if (string.startsWith("\"") && string.endsWith("\"")) return string.substring(1, string.length() - 1);
        if (string.matches("\\d+")) return Integer.parseInt(string);
        if (string.matches("\\d*\\.\\d+")) return Double.parseDouble(string);
        return string;
    }
}
