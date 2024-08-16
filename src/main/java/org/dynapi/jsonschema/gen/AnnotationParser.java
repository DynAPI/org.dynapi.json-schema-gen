package org.dynapi.jsonschema.gen;

import org.dynapi.jsonschema.gen.annotations.*;
import org.dynapi.jsonschema.gen.schema.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

class AnnotationParser {
    protected static Schema<?, ?> generateJsonSchemaForObject(StateData state, Class<?> clazz) {
        String refName = clazz.getCanonicalName();
        JRef objectRef = new JRef(refName);
        // we always return a ref for an object for recursive references
        if (state.references.containsKey(clazz.getCanonicalName())) {
            return objectRef;
        }

        JObject jsonSchema = new JObject()
                .allowAdditionalProperties(clazz.isAnnotationPresent(AllowAdditionalProperties.class));
        state.references.put(refName, jsonSchema);
        applyDescription(clazz.getAnnotation(Description.class), jsonSchema);

        List<String> fieldNames = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toList();

        HiddenProperties hiddenProperties = clazz.getAnnotation(HiddenProperties.class);
        List<String> hiddenPropertiesNames = List.of(hiddenProperties != null ? hiddenProperties.value() : new String[0]);

        List<String> diff = new ArrayList<>(hiddenPropertiesNames);
        diff.removeAll(fieldNames);
        if (!diff.isEmpty()) {
            throw new RuntimeException("Unknown properties to hide: " + String.join(", ", diff));
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (hiddenPropertiesNames.contains(field.getName()) || field.isAnnotationPresent(Hidden.class)) continue;

            Schema<?, ?> fieldSchema = generateJsonSchemaForField(state, field.getType());

            applyDescription(field.getAnnotation(Description.class), fieldSchema);
            applyExamples(field.getAnnotation(Examples.class), fieldSchema);
            applyConstraints(field.getAnnotation(Constraints.class), fieldSchema);

            if (field.isAnnotationPresent(Deprecated.class))
                fieldSchema.deprecated();

            if (field.isAnnotationPresent(Required.class))
                jsonSchema.required(field.getName());

            RequiredIf[] requiredIfAnyOf = field.getAnnotationsByType(RequiredIf.class);
            for (RequiredIf requiredIf : requiredIfAnyOf) {
                String ifField = field.getName();
                if (!fieldNames.contains(ifField)) {
                    String errorMessage = String.format("Bad @RequiredIf(\"%s\") of property %s of class %s", ifField, field.getName(), clazz.getCanonicalName());
                    throw new RuntimeException(errorMessage);
                }
                jsonSchema.requiredIf(ifField, requiredIf.value());
            }

            jsonSchema.addProperty(field.getName(), fieldSchema);
        }

        return jsonSchema;
    }

    protected static Schema<?, ?> generateJsonSchemaForField(StateData state, Class<?> clazz) {
        if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            return new JInteger();
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            return new JInteger();
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            return new JNumber();
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            return new JNumber();
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            return new JBoolean();
        } else if (clazz.equals(String.class)) {
            return new JString();
        } else if (clazz.equals(Map.class)) {
            return new JObject();
        } else if (clazz.equals(Object.class)) {
            return new JAnyType();
        } else if (clazz.isArray()) {  // todo: support for List?
            return generateJsonSchemaForArrayField(state, clazz);
        } else if (clazz.isEnum()) {
            return generateJsonSchemaForEnum(clazz);
        } else {
            return generateJsonSchemaForObject(state, clazz);
        }
    }

    protected static Schema<?, ?> generateJsonSchemaForArrayField(StateData state, Class<?> clazz) {
        return new JArray()
                .addType(generateJsonSchemaForField(state, clazz.getComponentType()));
    }

    protected static JString generateJsonSchemaForEnum(Class<?> clazz) {
        return new JString()
                .options(Stream.of(clazz.getEnumConstants()).map(v -> ((Enum<?>) v).name()).toList());
    }

    protected static void applyDescription(Description meta, Schema<?, ?> object) {
        if (meta == null) return;
        object.description(meta.value());
    }

    protected static <T> void applyExamples(Examples examples, Schema<?, T> object) {
        if (examples == null) return;
        //noinspection unchecked
        object.examples((List<T>) Arrays.stream(examples.value()).map(Util::parseStringToJsonX).toList());
    }

    protected static <T> void applyConstraints(Constraints constraints, Schema<?, T> schema) {
        if (constraints == null) return;

        // common constraints

        if (!constraints.option().isEmpty())
            //noinspection unchecked
            schema.constValue((T) constraints.option());
        if (constraints.options().length > 0)
            //noinspection unchecked
            schema.options((List<T>) Arrays.stream(constraints.options()).map(Util::parseStringToJsonX).toList());

        // string constrains

        if (schema instanceof JString jString) {
            if (constraints.minLength() != -1)
                jString.minLength(constraints.minLength());
            if (constraints.maxLength() != -1)
                jString.maxLength(constraints.maxLength());

            if (!constraints.pattern().isEmpty())
                jString.pattern(constraints.pattern());

            if (!constraints.format().isEmpty())
                jString.format(constraints.format());
        }

        // numeric constraints

        if (schema instanceof JNumber jNumber) {
            if (constraints.multipleOf() != -1)
                jNumber.multipleOf(constraints.multipleOf());

            if (constraints.ge() != -1)
                jNumber.gte(constraints.ge());
            if (constraints.gt() != -1)
                jNumber.gt(constraints.gt());
            if (constraints.le() != -1)
                jNumber.lte(constraints.le());
            if (constraints.lt() != -1)
                jNumber.lt(constraints.lt());
        }

        // object constraints

        if (schema instanceof JObject jObject) {
            if (constraints.minProperties() != -1)
                jObject.minProperties(constraints.minProperties());
            if (constraints.maxProperties() != -1)
                jObject.maxProperties(constraints.maxProperties());
        }

        // array constraints

        if (schema instanceof JArray jArray) {
            if (constraints.minItems() != -1)
                jArray.minSize(constraints.minItems());
            if (constraints.maxItems() != -1)
                jArray.maxSize(constraints.maxItems());

            if (constraints.uniqueItems())
                jArray.uniqueItems();
        }
    }

    protected static class StateData {
        protected Map<String, Schema<?, ?>> references = new HashMap<>();
    }
}
