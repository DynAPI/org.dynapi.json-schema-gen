package org.dynapi.jsonschema.gen.schema;

import org.json.JSONObject;

/**
 * <b>for internal usage of {@code org.dynapi:json-schema-gen} only</b>
 */
public class BackdoorSchema extends Schema<BackdoorSchema, Void> {

    public BackdoorSchema setDirectly(String option, Object value) {
        this.options.put(option, value);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject();
    }

    public static void addExtraSchemaDataFromTo(Schema<?, ?> from, Schema<?, ?> to) {
        JSONObject extraData = from.extraSchemaData();
        for (String key : extraData.keySet())
            to.options.put(key, extraData.get(key));
    }
}
