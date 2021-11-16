package fr.davidson.diff.jjoules.util.maven;

import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
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
        if (mavenHome == null) {
            setMavenHome();
        }
        invoker.setMavenHome(new File(mavenHome));
        try {
            final InvocationResult invocationResult = invoker.execute(invocationRequest);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String mavenHome;

    private static void setMavenHome() {
        LOGGER.warn("Trying to lookup for maven home.");
        LOGGER.warn("This can fail, and thus lead to a crash of the application.");
        LOGGER.warn("You can set this value using the field mavenHome or defining the following property: MAVEN_HOME or M2_HOME");
        mavenHome = getMavenHome(envVariable -> System.getenv().get(envVariable) != null,
                envVariable -> System.getenv().get(envVariable),
                "MAVEN_HOME", "M2_HOME");
        if (mavenHome == null) {
            mavenHome = getMavenHome(path -> new File(path).exists(),
                    Function.identity(),
                    "/usr/share/maven/", "/usr/local/maven-3.3.9/", "/usr/share/maven3/", "/usr/share/apache-maven-3.8.1");
            if (mavenHome == null) {
                throw new RuntimeException("Maven home not found, please set properly MAVEN_HOME or M2_HOME.");
            }
        }
    }

    private static String getMavenHome(Predicate<String> conditional,
                                       Function<String, String> getFunction,
                                       String... possibleValues) {
        String mavenHome = null;
        final Optional<String> potentialMavenHome = Arrays.stream(possibleValues).filter(conditional).findFirst();
        if (potentialMavenHome.isPresent()) {
            mavenHome = getFunction.apply(potentialMavenHome.get());
        }
        return mavenHome;
    }

}
