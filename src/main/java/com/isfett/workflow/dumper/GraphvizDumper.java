package com.isfett.workflow.dumper;

import com.isfett.workflow.Definition;
import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphvizDumper implements DumperInterface {
    protected final Map<String, Map<String, String>> defaultOptions = Map.of(
        "graph", Map.of("ratio", "compress", "rankdir", "LR"),
        "node", Map.of("fontsize", "9", "fontname", "Arial", "color", "#333333", "fillcolor", "lightblue", "fixedsize", "false", "width", "1"),
        "edge", Map.of("fontsize", "9", "fontname", "Arial", "color", "#333333", "arrowhead", "normal", "arrowsize", "0.5"),
        "default", Map.of("markedColor", "#FF0000", "markedShape", "doublecircle", "initialPlaceStyle", "filled")
    );

    private modes mode = modes.WITH_TRANSITION_BOX;

    @Override
    public void generateImage(File outputFile, Definition definition, Marking marking, Map<String, Map<String, String>> options) throws IOException {
        Graphviz.fromString(this.dump(definition, marking, options)).render(Format.PNG).toFile(outputFile);
    }

    @Override
    public void generateImage(File outputFile, modes mode, Definition definition, Marking marking, Map<String, Map<String, String>> options) throws IOException {
        this.mode = mode;
        this.generateImage(outputFile, definition, marking, options);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String dump(Definition definition, Marking marking, Map<String, Map<String, String>> options) {
        options = DumperInterface.deepMerge(this.defaultOptions, options);

        Map<String, Map<String, String>> places = this.findPlaces(definition, marking, options);
        Map<String, Map<String, String>> transitions = this.findTransitions(definition);
        List<Map<String, String>> edges = this.findEdges(definition);

        return this.startDot(options) + this.addPlaces(places) + this.addTransitions(transitions) + this.addEdges(edges) + this.endDot();
    }

    @Override
    public String dump(Definition definition, modes mode, Marking marking, Map<String, Map<String, String>> options) {
        this.mode = mode;
        return this.dump(definition, marking, options);
    }

    protected Map<String, Map<String, String>> findPlaces(Definition definition, Marking marking, Map<String, Map<String, String>> options) {
        Map<String, Map<String, String>> places = new HashMap<>();

        for (String place : definition.getPlaces().values()) {
            Map<String, String> attributes = new HashMap<>();
            if (definition.getInitialPlaces().containsValue(place)) {
                attributes.put("style", options.get("default").get("initialPlaceStyle"));
            }
            if (null != marking && marking.has(place)) {
                attributes.put("color", options.get("default").get("markedColor"));
                attributes.put("shape", options.get("default").get("markedShape"));
            }

            places.put(place, attributes);
        }

        return places;
    }

    protected Map<String, Map<String, String>> findTransitions(Definition definition) {
        Map<String, Map<String, String>> transitions = new HashMap<>();

        for (Transition transition : definition.getTransitions()) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("shape", "box");
            attributes.put("regular", "1");

            transitions.put(transition.getName(), attributes);
        }

        return transitions;
    }

    protected List<Map<String, String>> findEdges(Definition definition) {
        List<Map<String, String>> dotEdges = new ArrayList<>();

        for (int i = 0; i < definition.getTransitions().size(); i++) {
            Transition transition = definition.getTransitions().get(i);

            if(this.mode.equals(modes.WITHOUT_TRANSITION_BOX)) {
                for (String from : transition.getFroms()) {
                    for(String to: transition.getTos()) {
                        dotEdges.add(Map.of("from", from, "to", to, "direction", "from", "transition_number", String.valueOf(i), "label", transition.getName()));
                    }
                }
            } else {
                for (String from : transition.getFroms()) {
                    dotEdges.add(Map.of("from", from, "to", transition.getName(), "direction", "from", "transition_number", String.valueOf(i)));
                }

                for (String to : transition.getTos()) {
                    dotEdges.add(Map.of("from", transition.getName(), "to", to, "direction", "to", "transition_number", String.valueOf(i)));
                }
            }
        }

        return dotEdges;
    }

    protected String addOptions(Map<String, String> options) {
        List<String> code = new ArrayList<>();

        options.forEach((String k, String v) -> code.add(String.format("%s=\"%s\"", k, v)));

        return String.join(" ", code.stream().sorted().toList());
    }

    protected String addAttributes(Map<String, String> attributes) {
        List<String> code = new ArrayList<>();

        attributes.forEach((String k, String v) -> code.add(String.format("%s=\"%s\"", k, DumperInterface.escape(v))));

        return String.join(" ", code.stream().sorted().toList());
    }

    protected String dotize(String toHash) {
        int hashCode = toHash.hashCode() < 0 ? toHash.hashCode() * -1 : toHash.hashCode();
        return String.valueOf(hashCode);
    }

    protected String startDot(Map<String, Map<String, String>> options) {
        return String.format(
            "digraph workflow {\n  %s\n  node [%s];\n  edge [%s];\n\n",
            this.addOptions(options.get("graph")),
            this.addOptions(options.get("node")),
            this.addOptions(options.get("edge"))
        );
    }

    protected String endDot() {
        return "}\n";
    }

    protected String addPlaces(Map<String, Map<String, String>> places) {
        StringBuilder code = new StringBuilder();

        for (Map.Entry<String, Map<String, String>> entry : places.entrySet()) {
            String name = entry.getKey();
            Map<String, String> attributes = entry.getValue();
            if (!attributes.containsKey("shape")) {
                attributes.put("shape", "circle");
            }
            code.append(String.format("  place_%s [label=\"%s\" %s];\n", this.dotize(name), DumperInterface.escape(name), this.addAttributes(attributes)));
        }

        return code.toString();
    }

    protected String addTransitions(Map<String, Map<String, String>> transitions) {
        if (this.mode.equals(modes.WITHOUT_TRANSITION_BOX)) {
            return "";
        }

        StringBuilder code = new StringBuilder();

        for (Map.Entry<String, Map<String, String>> entry : transitions.entrySet()) {
            String name = entry.getKey();
            Map<String, String> attributes = entry.getValue();
            code.append(String.format("  transition_%s [label=\"%s\" %s];\n", this.dotize(name), DumperInterface.escape(name), this.addAttributes(attributes)));
        }

        return code.toString();
    }

    protected String addEdges(List<Map<String, String>> edges) {
        StringBuilder code = new StringBuilder();

        for (Map<String, String> edge : edges) {
            if (this.mode.equals(modes.WITHOUT_TRANSITION_BOX)) {
                code.append(String.format("  place_%s -> place_%s [label=\"%s\" style=\"solid\"];\n", this.dotize(edge.get("from")), this.dotize(edge.get("to")), DumperInterface.escape(edge.get("label"))));
            } else {
                if ("from".equals(edge.get("direction"))) {
                    code.append(String.format("  place_%s -> transition_%s [style=\"solid\"];\n", this.dotize(edge.get("from")), this.dotize(edge.get("to"))));
                } else {
                    code.append(String.format("  transition_%s -> place_%s [style=\"solid\"];\n", this.dotize(edge.get("from")), this.dotize(edge.get("to"))));
                }
            }
        }

        return code.toString();
    }
}
