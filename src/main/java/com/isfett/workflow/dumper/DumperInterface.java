package com.isfett.workflow.dumper;

import com.isfett.workflow.Definition;
import com.isfett.workflow.Marking;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface DumperInterface {
    public enum modes {
        WITH_TRANSITION_BOX,
        WITHOUT_TRANSITION_BOX
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static Map deepMerge(Map original, Map newMap) {
        original = new HashMap<>(original);
        for (Object key : newMap.keySet()) {
            if (newMap.get(key) instanceof Map) {
                Map<String, String> originalChild = (Map) original.get(key);
                Map<String, String> newChild = (Map) newMap.get(key);
                original.put(key, deepMerge(originalChild, newChild));
            } else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

    static String escape(String toEscape) {
        toEscape = toEscape.replaceAll("\\\\", "\\\\\\\\");
        toEscape = toEscape.replaceAll("\\n", "\\\\n");
        toEscape = toEscape.replaceAll("\\r", "\\\\r");
        toEscape = toEscape.replaceAll("\\00", "\\\\0");
        toEscape = toEscape.replaceAll("'", "\\\\'");
        return toEscape;
    }

    String dump(Definition definition, Marking marking, Map<String, Map<String, String>> options);

    String dump(Definition definition, modes mode, Marking marking, Map<String, Map<String, String>> options);

    void generateImage(File outputFile, Definition definition, Marking marking, Map<String, Map<String, String>> options) throws IOException;

    void generateImage(File outputFile, modes mode, Definition definition, Marking marking, Map<String, Map<String, String>> options) throws IOException;
}
