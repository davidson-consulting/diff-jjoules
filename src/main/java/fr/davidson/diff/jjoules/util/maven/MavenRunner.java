package fr.davidson.diff.jjoules.util.maven;

import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 31/08/2021
 */
public class MavenRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenRunner.class);

    public static void runCleanAndCompile(String pathToPom) {
        final Properties properties = new Properties();
        properties.setProperty("mdep.outputFile", "classpath");
        runGoals(pathToPom, properties, "clean", "test", "-DskipTests", "dependency:build-classpath"
                ,"--quiet"
        );
    }

    public static void runGoals(String pathToPom, String... goals) {
        runGoals(pathToPom, new Properties(), goals);
    }

    public static void runGoals(String pathToPom, Properties properties, String... goals) {
        final InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(pathToPom));
        invocationRequest.setGoals(Arrays.asList(goals));
        invocationRequest.setProperties(properties);
        LOGGER.info("mvn -f {} {} {}",
                pathToPom,
                String.join(" ", invocationRequest.getGoals()),
                invocationRequest.getProperties()
                        .keySet()
                        .stream()
                        .map(key -> "-D" + key + "=" + invocationRequest.getProperties().get(key))
                        .collect(Collectors.joining(""))
        );
        final Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File("/usr/share/maven"));
        try {
            final InvocationResult invocationResult = invoker.execute(invocationRequest);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
