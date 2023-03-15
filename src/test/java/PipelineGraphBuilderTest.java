import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.pdg4j.api.PipelineContext;
import ru.hse.pdg4j.api.PipelineTask;
import ru.hse.pdg4j.api.PipelineTaskContext;
import ru.hse.pdg4j.api.PipelineTaskResult;
import ru.hse.pdg4j.impl.builder.PipelineGraphBuilder;
import ru.hse.pdg4j.impl.util.PrintUtils;

import java.util.Collection;
import java.util.Collections;

import static ru.hse.pdg4j.impl.SimplePipelineTaskResult.success;

public class PipelineGraphBuilderTest {
    static class EmptyContext implements PipelineTaskContext {

    }

    static class StubTask implements PipelineTask<EmptyContext> {
        private String name;

        public StubTask(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public EmptyContext getContext() {
            return new EmptyContext();
        }

        @Override
        public PipelineTaskResult run(PipelineContext context) {
            return success();
        }

        @Override
        public Collection<Class<? extends PipelineTask<?>>> getRequirements() {
            return Collections.emptyList();
        }
    }

    @Test
    public void buildGraph_Check_Success() {
        PipelineGraphBuilder builder = new PipelineGraphBuilder();
        PipelineTask<EmptyContext> first = new StubTask("first");
        PipelineTask<EmptyContext> second = new StubTask("second");
        PipelineTask<EmptyContext> third = new StubTask("third");
        PipelineTask<EmptyContext> fourth = new StubTask("fourth");
        PipelineTask<EmptyContext> fifth = new StubTask("fifth");
        PipelineTask<EmptyContext> sixth = new StubTask("sixth");
        PipelineTask<EmptyContext> top = new StubTask("top");

        builder.task(first);
        builder.task(second);
        builder.task(third, first.getName(), second.getName());

        builder.task(fourth);
        builder.task(fifth);
        builder.task(sixth, fourth.getName(), fifth.getName());

        builder.task(top, sixth.getName(), third.getName());

        Assertions.assertEquals(
                """
                        graph {
                        second -- first
                        first -- fourth
                        fourth -- fifth
                        fifth -- third
                        third -- sixth
                        sixth -- top
                        }""",
                PrintUtils.pipelineGraphToDot(builder.build()));
    }

    @Test
    public void samplePipeline_Success() {

    }
}
