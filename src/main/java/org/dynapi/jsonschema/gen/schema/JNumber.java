package org.dynapi.jsonschema.gen.schema;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.json.JSONObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JNumber extends Schema<JNumber, Double> {
    public JNumber gte(double value) {
        options.put("minimum", value);
        options.put("exclusiveMinimum", false);
        return this;
    }

    public JNumber gt(double value) {
        options.put("minimum", value);
        options.put("exclusiveMinimum", true);
        return this;
    }

    public JNumber lte(double value) {
        options.put("maximum", value);
        options.put("exclusiveMaximum", false);
        return this;
    }

    public JNumber lt(double value) {
        options.put("maximum", value);
        options.put("exclusiveMaximum", true);
        return this;
    }

    public JNumber multipleOf(double value) {
        options.put("multipleOf", value);
        return this;
    }

    @Override
    protected JSONObject extraSchemaData() {
        return new JSONObject()
                .put("type", "number");
    }
}
