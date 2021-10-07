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

    public static final String POM_XML = "/pom.xml";

    public static void runCleanAndCompile(String pathToRootDir) {
        final Properties properties = new Properties();
        properties.setProperty("mdep.outputFile", "classpath");
        runGoals(pathToRootDir, properties, "clean", "test", "-DskipTests", "dependency:build-classpath", "--quiet");
    }

    public static void runGoals(String pathToRootDir, Properties properties, String... goals) {
        final InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(pathToRootDir + POM_XML));
        invocationRequest.setGoals(Arrays.asList(goals));

        properties.setProperty("enforcer.skip", "true");
        properties.setProperty("checkstyle.skip", "true");
        properties.setProperty("cobertura.skip", "true");
        properties.setProperty("skipITs", "true");
        properties.setProperty("rat.skip", "true");
        properties.setProperty("license.skip", "true");
        properties.setProperty("findbugs.skip", "true");
        properties.setProperty("gpg.skip", "true");
        properties.setProperty("jacoco.skip", "true");
        properties.setProperty("animal.sniffer.skip", "true");
        properties.setProperty("proguard.skip", "true");
        properties.setProperty("java.locale.providers", "COMPAT,CLDR,SPI");

        invocationRequest.setProperties(properties);
        LOGGER.info("mvn -f {} {} {}",
                pathToRootDir,
                String.join(" ", invocationRequest.getGoals()),
                invocationRequest.getProperties()
                        .keySet()
                        .stream()
                        .map(key -> "-D" + key + "=" + invocationRequest.getProperties().get(key))
                        .collect(Collectors.joining(" "))
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
