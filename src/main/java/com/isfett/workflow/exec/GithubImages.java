package com.isfett.workflow.exec;

import com.isfett.workflow.Definition;
import com.isfett.workflow.DefinitionBuilder;
import com.isfett.workflow.Marking;
import com.isfett.workflow.Transition;
import com.isfett.workflow.TransitionBlocker;
import com.isfett.workflow.Workflow;
import com.isfett.workflow.WorkflowInterface;
import com.isfett.workflow.dumper.DumperInterface;
import com.isfett.workflow.dumper.GraphvizDumper;
import com.isfett.workflow.dumper.PlantUmlDumper;
import com.isfett.workflow.event.EnterEvent;
import com.isfett.workflow.event.EventDispatcher;
import com.isfett.workflow.event.EventListenerInterface;
import com.isfett.workflow.event.GuardEvent;
import com.isfett.workflow.event.LeaveEvent;
import com.isfett.workflow.markingstore.MarkingStoreInterface;
import com.isfett.workflow.markingstore.MethodMarkingStore;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubImages {
    private static int fileCounter = 0;

    @SuppressWarnings({"ReassignedVariable"})
    public static void main(String[] args) throws IOException {
        DumperInterface dumper = new GraphvizDumper();

        DefinitionBuilder definitionBuilder = new DefinitionBuilder();
        Definition definition = definitionBuilder
            .clear()
            .addPlace("draft")
            .addPlace("reviewed")
            .addPlace("rejected")
            .addPlace("published")
            .addTransition(new Transition("to_review", List.of("draft"), List.of("reviewed")))
            .addTransition(new Transition("publish", List.of("reviewed"), List.of("published")))
            .addTransition(new Transition("reject", List.of("reviewed"), List.of("rejected")))
            .build();

        dumper.generateImage(createNewFile(), definition, null, Map.of());

        definition = definitionBuilder
            .clear()
            .addPlace("place_A")
            .addPlace("place_B")
            .addTransition(new Transition("transition_1", List.of("place_A"), List.of("place_B")))
            .build();

        dumper.generateImage(createNewFile(), definition, null, Map.of());

        definition = definitionBuilder
            .clear()
            .addPlace("start")
            .addPlace("hr_call")
            .addPlace("personality_test")
            .addPlace("logic_test")
            .addPlace("formal_requirements")
            .addPlace("review")
            .addPlace("hired")
            .addPlace("declined")
            .addTransition(new Transition("start_process", List.of("start"), List.of("hr_call")))
            .addTransition(new Transition("to_review", List.of("hr_call", "personality_test", "formal_requirements", "logic_test"), List.of("review")))
            .addTransition(new Transition("to_personality", List.of("hr_call"), List.of("personality_test")))
            .addTransition(new Transition("to_logic_test", List.of("hr_call", "personality_test"), List.of("logic_test")))
            .addTransition(new Transition("to_formal_requirements", List.of("hr_call", "logic_test", "personality_test"), List.of("formal_requirements")))
            .addTransition(new Transition("to_hire", List.of("review"), List.of("hired")))
            .addTransition(new Transition("to_decline", List.of("review"), List.of("declined")))
            .build();

        dumper.generateImage(createNewFile(), definition, null, Map.of());

        definition = definitionBuilder
            .clear()
            .addPlace("start")
            .addPlace("test")
            .addPlace("review")
            .addPlace("coding")
            .addPlace("merged")
            .addPlace("closed")
            .addTransition(new Transition("submit", List.of("start"), List.of("test")))
            .addTransition(new Transition("update", List.of("test", "review", "coding"), List.of("test")))
            .addTransition(new Transition("wait_for_review", List.of("test"), List.of("review")))
            .addTransition(new Transition("request_change", List.of("review"), List.of("coding")))
            .addTransition(new Transition("accept", List.of("review"), List.of("merged")))
            .addTransition(new Transition("reject", List.of("review"), List.of("closed")))
            .addTransition(new Transition("reopen", List.of("closed"), List.of("test")))
            .build();

        dumper.generateImage(createNewFile(), DumperInterface.modes.WITHOUT_TRANSITION_BOX, definition, null, Map.of());

        definition = definitionBuilder
            .clear()
            .addPlaces(List.of("draft", "reviewed", "rejected", "published"))
            .addTransition(new Transition("to_review", List.of("draft"), List.of("reviewed")))
            .addTransition(new Transition("publish", List.of("reviewed"), List.of("published")))
            .addTransition(new Transition("reject", List.of("reviewed"), List.of("rejected")))
            .build();

        new PlantUmlDumper().generateImage(createNewFile(), definition, null, Map.of("default", Map.of("title", "blog_publishing")));

        dumper.generateImage(createNewFile(), DumperInterface.modes.WITHOUT_TRANSITION_BOX, definition, null, Map.of());

        MarkingStoreInterface markingStore = new MethodMarkingStore(true, "status");
        WorkflowInterface workflow = new Workflow(definition, markingStore);
        Post post = new Post();
        post.setStatus("reviewed");
        Marking marking = workflow.getMarking(post);
        dumper.generateImage(createNewFile(), definition, marking, Map.of());
        dumper.generateImage(createNewFile(), definition, marking, Map.of("default", Map.of("markedColor", "#0000FF")));

        WorkflowInterface postWorkflow = new Workflow(definition, markingStore, "blog_publishing", new EventDispatcher());
        postWorkflow.addEventListener(new LogPostLeaveEventListener(), List.of("workflow.blog_publishing.leave"));
        postWorkflow.addEventListener(new BlogPostReviewGuardEventListener(), List.of("workflow.blog_publishing.guard.to_review"));
        post = new Post();
        post.setId(1337);
        post.setStatus("draft");
        System.out.println(postWorkflow.can(post, "to_review"));
        System.out.println(postWorkflow.buildTransitionBlockerList(post, "to_review").stream().map(TransitionBlocker::getMessage).toList()); //[This post cannot be marked as reviewed because it has no title.]
        postWorkflow.apply(post, "to_review"); // throws TransitionNotEnabledException -> Transition to_review is not enabled for workflow blog_publishing
    }

    private static File createNewFile() {
        fileCounter++;
        return new File(".github/images/fig" + fileCounter +".png");
    }

    @SuppressWarnings("unused")
    public static class Post{
        private Integer id;
        private String status = "draft";
        private String title = "";

        public Post() {}

        public Post(String status) {
            this.status = status;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class LogPostLeaveEventListener implements EventListenerInterface {
        private static final Logger log = LoggerFactory.getLogger(LogPostLeaveEventListener.class);

        public void onLeave(LeaveEvent event, String eventName) {
            Post post = (Post) event.getSource();
            System.out.println(String.format(
                "Blog post (id: %s) performed transition %s from %s to %s",
                post.getId(),
                event.getTransition().getName(),
                String.join(", ", event.getMarking().getPlaces()),
                String.join(", ", event.getTransition().getTos())
            ));
        }
    }

    public static class BlogPostReviewGuardEventListener implements EventListenerInterface {
        public void onGuard(GuardEvent event, String eventName) {
            Post post = (Post) event.getSource();

            if(post.getTitle().isEmpty()) {
                event.setBlocked(true, "This post cannot be marked as reviewed because it has no title.");
            }
        }
    }
}
