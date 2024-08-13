package org.dynapi.jsonschema.gen.schema;

import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JObject extends Schema<JObject, JSONObject> {
    private final Map<String, Schema<?, ?>> properties = new HashMap<>();

    /**
     * @param properties marks properties that are required
     */
    public JObject required(@NonNull String... properties) {
        if (!options.containsKey("required")) options.put("required", new JSONArray());
        ((JSONArray) options.get("required")).putAll(properties);
        return this;
    }

    public JObject requiredIf(@NonNull String requiredProperty, @NonNull String ifProperty) {
        if (!options.containsKey("dependentRequired")) options.put("dependentRequired", new JSONObject());
        JSONObject dependentRequired = (JSONObject) options.get("dependentRequired");
        if (!dependentRequired.has(ifProperty)) dependentRequired.put(ifProperty, new JSONArray());
        dependentRequired.getJSONArray(ifProperty).put(requiredProperty);
        return this;
    }

    /**
     * @param property property-name
     * @param value value schema
     */
    public JObject addProperty(@NonNull String property, @NonNull Schema<?, ?> value) {
        properties.put(property, value);
        return this;
    }

    /**
     * marks that this object accepts properties not specified via {@link JObject#addProperty}
     */
    public JObject allowAdditionalProperties() {
        return allowAdditionalProperties(true);
    }
    /**
     * marks that this object accepts properties not specified via {@link JObject#addProperty}
     */
    public JObject allowAdditionalProperties(boolean allowAdditionalProperties) {
        options.put("additionalProperties", allowAdditionalProperties);
        return this;
    }

    public JObject minProperties(int minProperties) {
        options.put("minProperties", minProperties);
        return this;
    }

    public JObject maxProperties(int maxProperties) {
        options.put("maxProperties", maxProperties);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        if (properties.isEmpty())
            return new JSONObject()
                    .put("type", "object");

        Map<String, JSONObject> finalizedProperties = properties.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getJsonSchema()));

        return new JSONObject()
                .put("type", "object")
                .put("properties", finalizedProperties);
    }
}
