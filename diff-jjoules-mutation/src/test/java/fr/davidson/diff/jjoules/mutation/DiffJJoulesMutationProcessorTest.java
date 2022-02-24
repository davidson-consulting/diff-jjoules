package fr.davidson.diff.jjoules.mutation;

import fr.davidson.diff.jjoules.mutation.processor.DiffJJoulesMutationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 18/02/2022
 */
public class DiffJJoulesMutationProcessorTest {

    @BeforeEach
    void setUp() {
        final File directory = new File("target/trash");
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @Test
    void test() {
        final CtClass<?> ctClass = Launcher.parseClass("public class SrcClass { public void method() {} }");
        ctClass.getFactory().getModel().processWith(
                new DiffJJoulesMutationProcessor(
                        new HashMap<String, Set<String>>() {
                            {
                                put("SrcClass", new HashSet<>());
                                get("SrcClass").addAll(Collections.singletonList("method"));
                            }
                        },
                        "target/trash",
                        "",
                        100000L
                )
        );
        System.out.println(ctClass);
    }
}
