package org.dynapi.jsonschema.gen.schema;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.dynapi.jsonschema.gen.JsonSchemaAble;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
abstract public class Schema<THIS extends Schema<THIS, ?>, TYPE> implements JsonSchemaAble {
    protected THIS getThis() { return (THIS) this; }

    protected final Map<String, Object> options = new HashMap<>();

    public THIS description(String description) {
        options.put("description", description);
        return getThis();
    }

    @SafeVarargs
    public final THIS examples(TYPE... examples) {
        return examples(List.of(examples));
    }

    public final THIS examples(List<TYPE> examples) {
        options.put("examples", examples);
        return getThis();
    }

    public THIS constValue(TYPE constValue) {
        options.put("const", constValue);
        return getThis();
    }

    @SafeVarargs
    public final THIS options(TYPE... enumValues) {
        return options(List.of(enumValues));
    }

    public THIS options(List<TYPE> enumValues) {
        options.put("enum", enumValues);
        return getThis();
    }

    public THIS deprecated() {
        return deprecated(true);
    }

    public THIS deprecated(boolean deprecated) {
        options.put("deprecated", deprecated);
        return getThis();
    }

    public Not not() {
        return new Not(this);
    }

    @Override
    public JSONObject getJsonSchema() {
        JSONObject schema = new JSONObject(options);
        JSONObject extraData = extraSchemaData();
        for (String key : extraData.keySet())
            schema.put(key, extraData.get(key));
        return schema;
    }

    abstract protected JSONObject extraSchemaData();
}
