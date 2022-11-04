package com.isfett.workflow.dumper;

import com.isfett.workflow.Definition;
import com.isfett.workflow.Marking;
import com.isfett.workflow.WorkflowTestHelper;
import com.isfett.workflow.dumper.DumperInterface.modes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PlantUmlDumperTest {
    private DumperInterface dumper;

    private static Stream<Arguments> provideWorkflowDefinition() {
        return Stream.of(
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), null, "SimpleDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideSimpleWorkflowDumpWithoutMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), null, "ComplexDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideComplexWorkflowDumpWithoutMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), new Marking(List.of("b")), "SimpleDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideSimpleWorkflowDumpWithMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), new Marking(List.of("b", "c")), "ComplexDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideComplexWorkflowDumpWithMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), null, "SimpleDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideSimpleWorkflowDumpWithoutMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), null, "ComplexDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideComplexWorkflowDumpWithoutMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), new Marking(List.of("b")), "SimpleDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideSimpleWorkflowDumpWithMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), new Marking(List.of("b", "c")), "ComplexDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideComplexWorkflowDumpWithMarkingWithoutTransitionBoxes())
        );
    }

    private static Stream<Arguments> provideStateMachineDefinition() {
        return Stream.of(
            Arguments.arguments(WorkflowTestHelper.createComplexStateMachineDefinition(), null, "SimpleDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideComplexStateMachineDumpWithoutMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexStateMachineDefinition(), new Marking(List.of("c", "e")), "SimpleDiagram", modes.WITH_TRANSITION_BOX, PlantUmlDumperTest.provideComplexStateMachineDumpWithMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexStateMachineDefinition(), null, "SimpleDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideComplexStateMachineDumpWithoutMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexStateMachineDefinition(), new Marking(List.of("c", "e")), "SimpleDiagram", modes.WITHOUT_TRANSITION_BOX, PlantUmlDumperTest.provideComplexStateMachineDumpWithMarkingWithoutTransitionBoxes())
        );
    }

    private static String provideComplexWorkflowDumpWithMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title ComplexDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b <<marked>>
            state c <<marked>>
            state d
            state e
            state f
            state g
            agent t1
            agent t2
            agent t3
            agent t4
            agent t5
            agent t6
            a --> t1
            t1 --> b
            t1 --> c
            b --> t2
            t2 --> d
            c --> t2
            d --> t3
            t3 --> e
            d --> t4
            t4 --> f
            e --> t5
            t5 --> g
            f --> t6
            t6 --> g
            @enduml""";
    }

    private static String provideComplexWorkflowDumpWithMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title ComplexDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b <<marked>>
            state c <<marked>>
            state d
            state e
            state f
            state g
            a --> b : t1
            a --> c : t1
            b --> d : t2
            c --> d : t2
            d --> e : t3
            d --> f : t4
            e --> g : t5
            f --> g : t6
            @enduml""";
    }

    private static String provideComplexWorkflowDumpWithoutMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title ComplexDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b
            state c
            state d
            state e
            state f
            state g
            agent t1
            agent t2
            agent t3
            agent t4
            agent t5
            agent t6
            a --> t1
            t1 --> b
            t1 --> c
            b --> t2
            t2 --> d
            c --> t2
            d --> t3
            t3 --> e
            d --> t4
            t4 --> f
            e --> t5
            t5 --> g
            f --> t6
            t6 --> g
            @enduml""";
    }

    private static String provideComplexWorkflowDumpWithoutMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title ComplexDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b
            state c
            state d
            state e
            state f
            state g
            a --> b : t1
            a --> c : t1
            b --> d : t2
            c --> d : t2
            d --> e : t3
            d --> f : t4
            e --> g : t5
            f --> g : t6
            @enduml""";
    }

    private static String provideSimpleWorkflowDumpWithMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b <<marked>>
            state c
            agent t1
            agent t2
            a --> t1
            t1 --> b
            b --> t2
            t2 --> c
            @enduml""";
    }

    private static String provideSimpleWorkflowDumpWithMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b <<marked>>
            state c
            a --> b : t1
            b --> c : t2
            @enduml""";
    }

    private static String provideSimpleWorkflowDumpWithoutMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b
            state c
            agent t1
            agent t2
            a --> t1
            t1 --> b
            b --> t2
            t2 --> c
            @enduml""";
    }

    private static String provideSimpleWorkflowDumpWithoutMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b
            state c
            a --> b : t1
            b --> c : t2
            @enduml""";
    }

    private static String provideComplexStateMachineDumpWithoutMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b
            state c
            state d
            agent t1
            agent t2
            agent t3
            a --> t1
            t1 --> b
            d --> t1
            b --> t2
            t2 --> c
            b --> t3
            t3 --> d
            @enduml""";
    }

    private static String provideComplexStateMachineDumpWithoutMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b
            state c
            state d
            a --> b : t1
            d --> b : t1
            b --> c : t2
            b --> d : t3
            @enduml""";
    }

    private static String provideComplexStateMachineDumpWithMarkingWithTransitionBoxes() {
        return """
            @startuml
            allow_mixing
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            skinparam agent {
                BackgroundColor #ffffff
                BorderColor #3887C6
            }
            state a <<initial>>
            state b
            state c <<marked>>
            state d
            agent t1
            agent t2
            agent t3
            a --> t1
            t1 --> b
            d --> t1
            b --> t2
            t2 --> c
            b --> t3
            t3 --> d
            @enduml""";
    }

    private static String provideComplexStateMachineDumpWithMarkingWithoutTransitionBoxes() {
        return """
            @startuml
            hide empty description
            title SimpleDiagram
            skinparam titleBorderRoundCorner 15
            skinparam titleBorderThickness 2
            skinparam state {
                BackgroundColor<<initial>> #87b741
                BackgroundColor<<marked>> #3887C6
                BorderColor #3887C6
                BorderColor<<marked>> Black
                FontColor<<marked>> White
            }
            state a <<initial>>
            state b
            state c <<marked>>
            state d
            a --> b : t1
            d --> b : t1
            b --> c : t2
            b --> d : t3
            @enduml""";
    }

    @BeforeEach
    void setUp() {
        this.dumper = new PlantUmlDumper();
    }

    @ParameterizedTest
    @MethodSource("provideWorkflowDefinition")
    void testDumpWorkflows(Definition definition, Marking marking, String title, modes mode, String expectedResult) {
        String dumpedOutput = this.dumper.dump(definition, mode, marking, Map.of("default", Map.of("title", title)));
        assertEquals(expectedResult, dumpedOutput);
    }

    @ParameterizedTest
    @MethodSource("provideStateMachineDefinition")
    void testDumpStatemachines(Definition definition, Marking marking, String title, modes mode, String expectedResult) {
        String dumpedOutput = this.dumper.dump(definition, mode, marking, Map.of("default", Map.of("name", title)));
        assertEquals(expectedResult, dumpedOutput);
    }

    @Test
    void exportImage() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_plantuml_", new Date().toString()).toFile();
        this.dumper.generateImage(file, modes.WITHOUT_TRANSITION_BOX, definition, marking, Map.of("default", Map.of("name", title)));

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }

    @Test
    void exportImageWithDefaultOptions() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_plantuml_", new Date().toString()).toFile();
        this.dumper.generateImage(file, modes.WITH_TRANSITION_BOX, definition, marking, Map.of());

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }
}
