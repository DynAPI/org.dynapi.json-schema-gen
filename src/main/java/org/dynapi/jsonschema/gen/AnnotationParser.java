package org.dynapi.jsonschema.gen;

import org.dynapi.jsonschema.gen.annotations.*;
import org.dynapi.jsonschema.gen.schema.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

class AnnotationParser {
    protected static Schema<?, ?> generateJsonSchemaForObject(StateData state, Class<?> clazz) {
        String refName = clazz.getCanonicalName();
        TRef objectRef = new TRef(refName);
        // we always return a ref for an object for recursive references
        if (state.references.containsKey(clazz.getCanonicalName())) {
            return objectRef;
        }

        TObject jsonSchema = new TObject()
                .allowAdditionalProperties(clazz.isAnnotationPresent(AllowAdditionalProperties.class));
        state.references.put(refName, jsonSchema);
        applyDescription(clazz.getAnnotation(Description.class), jsonSchema);

        List<String> fieldNames = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toList();

        HiddenProperties hiddenProperties = clazz.getAnnotation(HiddenProperties.class);
        List<String> hiddenPropertiesNames = Arrays.asList(hiddenProperties != null ? hiddenProperties.value() : new String[0]);

        Set<String> diff = diff(fieldNames, hiddenPropertiesNames);
        if (!diff.isEmpty())
            throw new RuntimeException("Unknown properties to hide of @HiddenProperties of " + clazz.getCanonicalName() + " (" + String.join(", ", diff) + ")");

        Map<String, Set<String>> mutuallyExclusivesGroups = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (hiddenPropertiesNames.contains(field.getName()) || field.isAnnotationPresent(Hidden.class)) continue;

            Schema<?, ?> fieldSchema;

            Implementations implementations = field.getAnnotation(Implementations.class);
            if (implementations != null) {
                fieldSchema = new OneOf(
                        Stream.of(implementations.value())
                                .map(implementation -> generateJsonSchemaForObject(state, implementation))
                                .toList()
                );
            } else {
                fieldSchema = generateJsonSchemaForField(state, field.getType());
            }

            applyDescription(field.getAnnotation(Description.class), fieldSchema);
            applyExamples(field.getAnnotation(Examples.class), fieldSchema);
            applyConstraints(field.getAnnotation(Constraints.class), fieldSchema);

            if (field.isAnnotationPresent(Deprecated.class))
                fieldSchema.deprecated();

            if (field.isAnnotationPresent(Required.class))
                jsonSchema.required(field.getName());

            RequiredIf[] requiredIfAnyOf = field.getAnnotationsByType(RequiredIf.class);
            for (RequiredIf requiredIf : requiredIfAnyOf) {
                String fieldName = field.getName();
                String ifField = requiredIf.value();
                if (!fieldNames.contains(ifField))
                    throw new RuntimeException("unknown field of @RequiredIf of " + clazz.getCanonicalName() + "#" + fieldName + " (" + ifField + ")");
                jsonSchema.requiredIf(fieldName, ifField);
            }

            // finds a mutually exclusive group and adds, or creates a new one
            MutuallyExclusiveWith mutuallyExclusiveWith = field.getAnnotation(MutuallyExclusiveWith.class);
            if (mutuallyExclusiveWith != null) {
                String fieldName = field.getName();
                Set<String> others = Set.of(mutuallyExclusiveWith.value());
                Set<String> unknownFields = diff(fieldNames, others);
                if (!unknownFields.isEmpty())
                    throw new RuntimeException("Unknown field(s) of @MutuallyExclusiveWith of " + clazz.getCanonicalName() + "#" + fieldName + " (" + String.join(", ", unknownFields) + ")");
                mutuallyExclusivesGroups.put(fieldName, others);
            }

            jsonSchema.addProperty(field.getName(), fieldSchema);
        }

        if (!mutuallyExclusivesGroups.isEmpty()) {
            // we precompute and remove duplicates before adding to the schema
            Set<Set<String>> badPairs = new HashSet<>();
            for (Map.Entry<String, Set<String>> entry : mutuallyExclusivesGroups.entrySet()) {
                String fieldName = entry.getKey();
                for (String otherField : entry.getValue()) {
                    badPairs.add(Set.of(fieldName, otherField));
                }
            }

            Not notAnyOfPair = new Not(new AnyOf(
                    badPairs
                            .stream()
                            .map(badPair ->
                                    (Schema<?, ?>) new BackdoorSchema()
                                            .setDirectly("required", badPair)
                            )
                            .toList()
            ));
            BackdoorSchema.addExtraSchemaDataFromTo(notAnyOfPair, jsonSchema);
        }

        return jsonSchema;
    }

    protected static Schema<?, ?> generateJsonSchemaForField(StateData state, Class<?> clazz) {
        if (clazz.equals(Number.class)) {
            return new TNumber();
        } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            return new TInteger();
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            return new TInteger();
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            return new TNumber();
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            return new TNumber();
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            return new TBoolean();
        } else if (clazz.equals(String.class)) {
            return new TString();
        } else if (clazz.equals(Map.class)) {
            return new TObject();
        } else if (clazz.equals(Object.class)) {
            return new TAnyType();
        } else if (clazz.isArray()) {  // todo: support for List?
            return generateJsonSchemaForArrayField(state, clazz);
        } else if (clazz.isEnum()) {
            return generateJsonSchemaForEnum(clazz);
        } else {
            return generateJsonSchemaForObject(state, clazz);
        }
    }

    protected static Schema<?, ?> generateJsonSchemaForArrayField(StateData state, Class<?> clazz) {
        return new TArray()
                .addType(generateJsonSchemaForField(state, clazz.getComponentType()));
    }

    protected static TString generateJsonSchemaForEnum(Class<?> clazz) {
        return new TString()
                .options(Stream.of(clazz.getEnumConstants()).map(v -> ((Enum<?>) v).name()).toList());
    }

    protected static void applyDescription(Description meta, Schema<?, ?> object) {
        if (meta == null) return;
        object.description(meta.value());
    }

    @SuppressWarnings("unchecked")
    protected static <T> void applyExamples(Examples examples, Schema<?, T> object) {
        if (examples == null) return;
        object.examples((List<T>) Arrays.stream(examples.value()).map(Util::parseStringToJsonX).toList());
    }

    @SuppressWarnings("unchecked")
    protected static <T> void applyConstraints(Constraints constraints, Schema<?, T> schema) {
        if (constraints == null) return;

        // common constraints

        if (!constraints.option().isEmpty())
            schema.constValue((T) Util.parseStringToJsonX(constraints.option()));
        if (constraints.options().length > 0)
            schema.options((List<T>) Arrays.stream(constraints.options()).map(Util::parseStringToJsonX).toList());

        // string constrains

        if (schema instanceof TString jString) {
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

        if (schema instanceof TNumber jNumber) {
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

        if (schema instanceof TObject jObject) {
            if (constraints.minProperties() != -1)
                jObject.minProperties(constraints.minProperties());
            if (constraints.maxProperties() != -1)
                jObject.maxProperties(constraints.maxProperties());
        }

        // array constraints

        if (schema instanceof TArray jArray) {
            if (constraints.minItems() != -1)
                jArray.minSize(constraints.minItems());
            if (constraints.maxItems() != -1)
                jArray.maxSize(constraints.maxItems());

            if (constraints.uniqueItems())
                jArray.uniqueItems();
        }
    }

    protected static <T> Set<T> diff(Collection<T> allowed, Collection<T> used) {
        Set<T> result = new HashSet<>(used);
        result.removeAll(allowed);
        return result;
    }

    protected static class StateData {
        protected Map<String, Schema<?, ?>> references = new HashMap<>();
    }
}
