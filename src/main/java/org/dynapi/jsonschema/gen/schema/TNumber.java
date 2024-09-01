package org.dynapi.jsonschema.gen.schema;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.json.JSONObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TNumber extends Schema<TNumber, Double> {
    public TNumber gte(double value) {
        options.put("minimum", value);
        options.put("exclusiveMinimum", false);
        return this;
    }

    public TNumber gt(double value) {
        options.put("minimum", value);
        options.put("exclusiveMinimum", true);
        return this;
    }

    public TNumber lte(double value) {
        options.put("maximum", value);
        options.put("exclusiveMaximum", false);
        return this;
    }

    public TNumber lt(double value) {
        options.put("maximum", value);
        options.put("exclusiveMaximum", true);
        return this;
    }

    public TNumber multipleOf(double value) {
        options.put("multipleOf", value);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("type", "number");
    }
}
