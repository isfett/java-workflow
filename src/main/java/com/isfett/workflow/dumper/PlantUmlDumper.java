package com.isfett.workflow.dumper;

import com.isfett.workflow.Definition;
import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlantUmlDumper implements DumperInterface {
    private static final String INITIAL = "<<initial>>";
    private static final String MARKED = "<<marked>>";

    private modes mode = modes.WITH_TRANSITION_BOX;

    protected final Map<String, Map<String, String>> defaultOptions = Map.of(
        "skinparams", Map.of(
            "titleBorderRoundCorner", "15",
            "titleBorderThickness", "2",
            "state_BackgroundColor" + PlantUmlDumper.INITIAL, "#87b741",
            "state_BackgroundColor" + PlantUmlDumper.MARKED, "#3887C6",
            "state_BorderColor", "#3887C6",
            "state_BorderColor" + PlantUmlDumper.MARKED, "Black",
            "state_FontColor" + PlantUmlDumper.MARKED, "White",
            "agent_BackgroundColor", "#ffffff",
            "agent_BorderColor", "#3887C6"
        ),
        "default", Map.of()
    );

    @Override
    public void generateImage(File outputFile, Definition definition, Marking marking, Map<String, Map<String, String>> options) throws IOException {
        SourceStringReader reader = new SourceStringReader(String.join("\n", this.dump(definition, marking, options)));
        reader.outputImage(new FileOutputStream(outputFile));
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

        List<String> code = new ArrayList<>(List.of(
            "@startuml"
        ));

        if (this.mode.equals(modes.WITHOUT_TRANSITION_BOX)) {
            code.add("hide empty description");
        } else {
            code.add("allow_mixing");
        }

        code.addAll(this.initialize(options));

        for (String place : definition.getPlaces().values()) {
            code.add(this.getState(place, definition, marking));
        }

        if (this.mode.equals(modes.WITH_TRANSITION_BOX)) {
            for (Transition transition : definition.getTransitions()) {
                List<String> agents = new ArrayList<>();
                agents.add(String.format("agent %s", transition.getName()));
                for(String agent: agents) {
                    if(!code.contains(agent)) {
                        code.add(agent);
                    }
                }
            }
        }

        for (Transition transition : definition.getTransitions()) {
            for (String from : transition.getFroms()) {
                for (String to : transition.getTos()) {
                    if (this.mode.equals(modes.WITHOUT_TRANSITION_BOX)) {
                        code.add(String.format("%s --> %s : %s", DumperInterface.escape(from), DumperInterface.escape(to), DumperInterface.escape(transition.getName())));
                    } else {
                        List<String> lines = new ArrayList<>();
                        lines.add(String.format("%s --> %s", DumperInterface.escape(from), DumperInterface.escape(transition.getName())));
                        lines.add(String.format("%s --> %s", DumperInterface.escape(transition.getName()), DumperInterface.escape(to)));

                        for (String line: lines) {
                            if (!code.contains(line)) {
                                code.add(line);
                            }
                        }
                    }
                }
            }
        }

        code.add("@enduml");

        return String.join("\n", code);
    }

    @Override
    public String dump(Definition definition, modes mode, Marking marking, Map<String, Map<String, String>> options) {
        this.mode = mode;
        return this.dump(definition, marking, options);
    }

    private List<String> initialize(Map<String, Map<String, String>> options) {
        List<String> code = new ArrayList<>();

        if (options.get("default").containsKey("title")) {
            code.add("title " + options.get("default").get("title"));
        }
        if (options.get("default").containsKey("name")) {
            code.add("title " + options.get("default").get("name"));
        }

        List<String> codeSkinparam = new ArrayList<>();
        List<String> codeSkinparamState = new ArrayList<>();
        List<String> codeSkinparamAgent = new ArrayList<>();
        options.get("skinparams").forEach((String k, String v) -> {
            if (!k.contains("_")) {
                codeSkinparam.add(String.format("skinparam %s %s", k, v));
            }
            if (k.startsWith("state_")) {
                codeSkinparamState.add(String.format("    %s %s", k.replace("state_", ""), v));
            }
            if (k.startsWith("agent_")) {
                codeSkinparamAgent.add(String.format("    %s %s", k.replace("agent_", ""), v));
            }
        });

        code.addAll(codeSkinparam.stream().sorted().toList());

        code.add("skinparam state {");
        code.addAll(codeSkinparamState.stream().sorted().toList());
        code.add("}");

        if (this.mode.equals(modes.WITH_TRANSITION_BOX)) {
            code.add("skinparam agent {");
            code.addAll(codeSkinparamAgent.stream().sorted().toList());
            code.add("}");
        }

        return code;
    }

    private String getState(String place, Definition definition, Marking marking) {
        return String.format("state %s%s%s", DumperInterface.escape(place), definition.getInitialPlaces().containsValue(place) ? " " + PlantUmlDumper.INITIAL : "", marking != null && marking.has(place) ? " " + PlantUmlDumper.MARKED : "");
    }
}
