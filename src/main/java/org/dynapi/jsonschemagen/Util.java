package org.dynapi.jsonschemagen;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * utility functions only for jsonschemagen
 */
class Util {
    protected static Object parseStringToJsonX(String string) {
        if (string.startsWith("{") && string.endsWith("}")) return new JSONObject(string);
        if (string.startsWith("[") && string.endsWith("]")) return new JSONArray(string);
        if (string.startsWith("\"") && string.endsWith("\"")) return string.substring(1, string.length() - 1);
        if (string.matches("\\d+")) return Integer.parseInt(string);
        if (string.matches("\\d*\\.\\d+")) return Double.parseDouble(string);
        return string;
    }
}
