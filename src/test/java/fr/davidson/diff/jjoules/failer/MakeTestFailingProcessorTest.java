package fr.davidson.diff.jjoules.failer;

import fr.davidson.diff.jjoules.AbstractProcessorTest;
import fr.davidson.diff.jjoules.failer.processor.MakeTestFailingProcessor;
import org.junit.jupiter.api.Assertions;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 24/09/2021
 */
public class MakeTestFailingProcessorTest extends AbstractProcessorTest {

    @Override
    protected void oracle(CtMethod<?> testMethod) {
        final String lastStatementAsString = testMethod
                .getBody()
                .getLastStatement()
                .toString();
        Assertions.assertEquals("junit.framework.Assert.fail()", lastStatementAsString);
    }

    @Override
    protected AbstractProcessor<?> getProcessor() {
        return new MakeTestFailingProcessor(this.getTestToBeProcessed(), ROOT_OUTPUT_DIR_PATH);
    }
}
