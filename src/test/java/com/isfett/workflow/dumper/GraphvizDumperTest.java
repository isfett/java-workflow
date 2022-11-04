package com.isfett.workflow.dumper;

import com.isfett.workflow.Definition;
import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import com.isfett.workflow.WorkflowTestHelper;
import com.isfett.workflow.dumper.DumperInterface.modes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class GraphvizDumperTest {
    private DumperInterface dumper;

    private static Stream<Arguments> provideWorkflowDefinitionWithoutMarking() {
        return Stream.of(
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), modes.WITH_TRANSITION_BOX, GraphvizDumperTest.provideComplexWorkflowDumpWithoutMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), modes.WITHOUT_TRANSITION_BOX, GraphvizDumperTest.provideComplexWorkflowDumpWithoutMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), modes.WITH_TRANSITION_BOX, GraphvizDumperTest.provideSimpleWorkflowDumpWithoutMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), modes.WITHOUT_TRANSITION_BOX, GraphvizDumperTest.provideSimpleWorkflowDumpWithoutMarkingWithoutTransitionBoxes())
        );
    }

    private static Stream<Arguments> provideWorkflowDefinitionWithMarking() {
        return Stream.of(
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), new Marking(List.of("b")), modes.WITH_TRANSITION_BOX, GraphvizDumperTest.provideComplexWorkflowDumpWithMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createComplexWorkflowDefinition(), new Marking(List.of("b")), modes.WITHOUT_TRANSITION_BOX, GraphvizDumperTest.provideComplexWorkflowDumpWithMarkingWithoutTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), new Marking(List.of("c", "d")), modes.WITH_TRANSITION_BOX, GraphvizDumperTest.provideSimpleWorkflowDumpWithMarkingWithTransitionBoxes()),
            Arguments.arguments(WorkflowTestHelper.createSimpleWorkflowDefinition(), new Marking(List.of("c", "d")), modes.WITHOUT_TRANSITION_BOX, GraphvizDumperTest.provideSimpleWorkflowDumpWithMarkingWithoutTransitionBoxes())
        );
    }

    private static String provideComplexWorkflowDumpWithMarkingWithTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                            
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" color="#FF0000" shape="doublecircle"];
              place_99 [label="c" shape="circle"];
              place_100 [label="d" shape="circle"];
              place_101 [label="e" shape="circle"];
              place_102 [label="f" shape="circle"];
              place_103 [label="g" shape="circle"];
              transition_3648 [label="t4" regular="1" shape="box"];
              transition_3649 [label="t5" regular="1" shape="box"];
              transition_3650 [label="t6" regular="1" shape="box"];
              transition_3645 [label="t1" regular="1" shape="box"];
              transition_3646 [label="t2" regular="1" shape="box"];
              transition_3647 [label="t3" regular="1" shape="box"];
              place_97 -> transition_3645 [style="solid"];
              transition_3645 -> place_98 [style="solid"];
              transition_3645 -> place_99 [style="solid"];
              place_98 -> transition_3646 [style="solid"];
              place_99 -> transition_3646 [style="solid"];
              transition_3646 -> place_100 [style="solid"];
              place_100 -> transition_3647 [style="solid"];
              transition_3647 -> place_101 [style="solid"];
              place_100 -> transition_3648 [style="solid"];
              transition_3648 -> place_102 [style="solid"];
              place_101 -> transition_3649 [style="solid"];
              transition_3649 -> place_103 [style="solid"];
              place_102 -> transition_3650 [style="solid"];
              transition_3650 -> place_103 [style="solid"];
            }
            """;
    }

    private static String provideComplexWorkflowDumpWithMarkingWithoutTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
             
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" color="#FF0000" shape="doublecircle"];
              place_99 [label="c" shape="circle"];
              place_100 [label="d" shape="circle"];
              place_101 [label="e" shape="circle"];
              place_102 [label="f" shape="circle"];
              place_103 [label="g" shape="circle"];
              place_97 -> place_98 [label="t1" style="solid"];
              place_97 -> place_99 [label="t1" style="solid"];
              place_98 -> place_100 [label="t2" style="solid"];
              place_99 -> place_100 [label="t2" style="solid"];
              place_100 -> place_101 [label="t3" style="solid"];
              place_100 -> place_102 [label="t4" style="solid"];
              place_101 -> place_103 [label="t5" style="solid"];
              place_102 -> place_103 [label="t6" style="solid"];
            }
            """;
    }

    private static String provideComplexWorkflowDumpWithoutMarkingWithTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                            
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" shape="circle"];
              place_100 [label="d" shape="circle"];
              place_101 [label="e" shape="circle"];
              place_102 [label="f" shape="circle"];
              place_103 [label="g" shape="circle"];
              transition_3648 [label="t4" regular="1" shape="box"];
              transition_3649 [label="t5" regular="1" shape="box"];
              transition_3650 [label="t6" regular="1" shape="box"];
              transition_3645 [label="t1" regular="1" shape="box"];
              transition_3646 [label="t2" regular="1" shape="box"];
              transition_3647 [label="t3" regular="1" shape="box"];
              place_97 -> transition_3645 [style="solid"];
              transition_3645 -> place_98 [style="solid"];
              transition_3645 -> place_99 [style="solid"];
              place_98 -> transition_3646 [style="solid"];
              place_99 -> transition_3646 [style="solid"];
              transition_3646 -> place_100 [style="solid"];
              place_100 -> transition_3647 [style="solid"];
              transition_3647 -> place_101 [style="solid"];
              place_100 -> transition_3648 [style="solid"];
              transition_3648 -> place_102 [style="solid"];
              place_101 -> transition_3649 [style="solid"];
              transition_3649 -> place_103 [style="solid"];
              place_102 -> transition_3650 [style="solid"];
              transition_3650 -> place_103 [style="solid"];
            }
            """;
    }

    private static String provideComplexWorkflowDumpWithoutMarkingWithoutTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                        
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" shape="circle"];
              place_100 [label="d" shape="circle"];
              place_101 [label="e" shape="circle"];
              place_102 [label="f" shape="circle"];
              place_103 [label="g" shape="circle"];
              place_97 -> place_98 [label="t1" style="solid"];
              place_97 -> place_99 [label="t1" style="solid"];
              place_98 -> place_100 [label="t2" style="solid"];
              place_99 -> place_100 [label="t2" style="solid"];
              place_100 -> place_101 [label="t3" style="solid"];
              place_100 -> place_102 [label="t4" style="solid"];
              place_101 -> place_103 [label="t5" style="solid"];
              place_102 -> place_103 [label="t6" style="solid"];
            }
            """;
    }

    private static String provideSimpleWorkflowDumpWithMarkingWithTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                            
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" color="#FF0000" shape="doublecircle"];
              transition_3645 [label="t1" regular="1" shape="box"];
              transition_3646 [label="t2" regular="1" shape="box"];
              place_97 -> transition_3645 [style="solid"];
              transition_3645 -> place_98 [style="solid"];
              place_98 -> transition_3646 [style="solid"];
              transition_3646 -> place_99 [style="solid"];
            }
            """;
    }

    private static String provideSimpleWorkflowDumpWithMarkingWithoutTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                        
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" color="#FF0000" shape="doublecircle"];
              place_97 -> place_98 [label="t1" style="solid"];
              place_98 -> place_99 [label="t2" style="solid"];
            }
            """;
    }

    private static String provideSimpleWorkflowDumpWithoutMarkingWithTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
             
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" shape="circle"];
              transition_3645 [label="t1" regular="1" shape="box"];
              transition_3646 [label="t2" regular="1" shape="box"];
              place_97 -> transition_3645 [style="solid"];
              transition_3645 -> place_98 [style="solid"];
              place_98 -> transition_3646 [style="solid"];
              transition_3646 -> place_99 [style="solid"];
            }
            """;
    }

    private static String provideSimpleWorkflowDumpWithoutMarkingWithoutTransitionBoxes() {
        return """
            digraph workflow {
              rankdir="LR" ratio="compress"
              node [color="#333333" fillcolor="lightblue" fixedsize="false" fontname="Arial" fontsize="9" width="1"];
              edge [arrowhead="normal" arrowsize="0.5" color="#333333" fontname="Arial" fontsize="9"];
                        
              place_97 [label="a" shape="circle" style="filled"];
              place_98 [label="b" shape="circle"];
              place_99 [label="c" shape="circle"];
              place_97 -> place_98 [label="t1" style="solid"];
              place_98 -> place_99 [label="t2" style="solid"];
            }
            """;
    }

    @BeforeEach
    void setUp() {
        this.dumper = new GraphvizDumper();
    }

    @ParameterizedTest
    @MethodSource("provideWorkflowDefinitionWithoutMarking")
    void testDumpWithoutMarking(Definition definition, modes mode, String expectedResult) {
        String dumpedOutput = this.dumper.dump(definition, mode, null, Map.of());
        assertEquals(expectedResult, dumpedOutput);
    }

    @ParameterizedTest
    @MethodSource("provideWorkflowDefinitionWithMarking")
    void testDumpWithMarking(Definition definition, Marking marking, modes mode, String expectedResult) {
        String dumpedOutput = this.dumper.dump(definition, mode, marking, Map.of());
        assertEquals(expectedResult, dumpedOutput);
    }

    @Test
    void exportImage() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        definition.getTransitions().add(new Transition("fooooo", List.of("a"), List.of("b")));
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_graphviz_", ".png").toFile();
        this.dumper.generateImage(file, modes.WITH_TRANSITION_BOX, definition, marking, Map.of("default", Map.of("name", title)));

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }

    @Test
    void exportImageWithTitle() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        definition.getTransitions().add(new Transition("fooooo", List.of("a"), List.of("b")));
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_graphviz_", ".png").toFile();
        this.dumper.generateImage(file, modes.WITHOUT_TRANSITION_BOX, definition, marking, Map.of("default", Map.of("title", title)));

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }

    @Test
    void exportImageWithTitleAndCustomMarkedColor() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        definition.getTransitions().add(new Transition("fooooo", List.of("a"), List.of("b")));
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_graphviz_", ".png").toFile();
        this.dumper.generateImage(file, modes.WITHOUT_TRANSITION_BOX, definition, marking, Map.of("default", Map.of("title", title, "markedColor", "#0000FF")));

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }

    @Test
    void exportImageWithDefaultOptions() throws IOException {
        Definition definition = WorkflowTestHelper.createSimpleWorkflowDefinition();
        String title = "TestGen";
        Marking marking = new Marking(List.of("b"));

        File file = Files.createTempFile("workflow_graphviz_", ".png").toFile();
        this.dumper.generateImage(file, definition, marking, Map.of());

        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }
}
