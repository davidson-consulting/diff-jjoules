package fr.davidson.diff.jjoules.suspect;

import eu.stamp_project.testrunner.EntryPoint;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumFormula;

import java.util.Map;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class FlacocoRunner {

    public static void run(String classpath, String rootPathDirectory) {
        Flacoco flacoco = new Flacoco();
        FlacocoConfig config = FlacocoConfig.getInstance();
        config.setTestRunnerVerbose(true);
        config.setTestFramework(FlacocoConfig.TestFramework.JUNIT5);

        config.setProjectPath(rootPathDirectory);
        config.setClasspath(classpath);
        config.setFamily(FlacocoConfig.FaultLocalizationFamily.SPECTRUM_BASED);
        config.setSpectrumFormula(SpectrumFormula.OCHIAI);

        Map<String, Double> susp = flacoco.runDefault();
        for (String s : susp.keySet()) {
            System.out.println(s + " " + susp.get(s));
        }
    }

    public static void main(String[] args) {
        EntryPoint.verbose = true;
        run(
                classpath,
                "/home/benjamin/workspace/diff-jjoules-demo/"
        );
    }

    private static final String classpath = "/home/benjamin/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.3.2/junit-jupiter-api-5.3.2.jar:/home/benjamin/.m2/repository/org/apiguardian/apiguardian-api/1.0.0/apiguardian-api-1.0.0.jar:/home/benjamin/.m2/repository/org/opentest4j/opentest4j/1.1.1/opentest4j-1.1.1.jar:/home/benjamin/.m2/repository/org/junit/platform/junit-platform-commons/1.3.2/junit-platform-commons-1.3.2.jar:/home/benjamin/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.3.2/junit-jupiter-engine-5.3.2.jar:/home/benjamin/.m2/repository/org/junit/platform/junit-platform-engine/1.3.2/junit-platform-engine-1.3.2.jar:/home/benjamin/.m2/repository/org/junit/platform/junit-platform-launcher/1.3.2/junit-platform-launcher-1.3.2.jar:/home/benjamin/.m2/repository/org/powerapi/jjoules/junit-jjoules/1.0-SNAPSHOT/junit-jjoules-1.0-SNAPSHOT.jar:/home/benjamin/.m2/repository/junit/junit/4.13/junit-4.13.jar:/home/benjamin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/benjamin/.m2/repository/org/powerapi/jjoules/j-joules/1.0-SNAPSHOT/j-joules-1.0-SNAPSHOT.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-plugin-api/3.3.9/maven-plugin-api-3.3.9.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-model/3.3.9/maven-model-3.3.9.jar:/home/benjamin/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-artifact/3.3.9/maven-artifact-3.3.9.jar:/home/benjamin/.m2/repository/org/eclipse/sisu/org.eclipse.sisu.plexus/0.3.2/org.eclipse.sisu.plexus-0.3.2.jar:/home/benjamin/.m2/repository/javax/enterprise/cdi-api/1.0/cdi-api-1.0.jar:/home/benjamin/.m2/repository/javax/annotation/jsr250-api/1.0/jsr250-api-1.0.jar:/home/benjamin/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar:/home/benjamin/.m2/repository/org/eclipse/sisu/org.eclipse.sisu.inject/0.3.2/org.eclipse.sisu.inject-0.3.2.jar:/home/benjamin/.m2/repository/org/codehaus/plexus/plexus-component-annotations/1.5.5/plexus-component-annotations-1.5.5.jar:/home/benjamin/.m2/repository/org/codehaus/plexus/plexus-classworlds/2.5.2/plexus-classworlds-2.5.2.jar:/home/benjamin/.m2/repository/org/apache/maven/plugin-tools/maven-plugin-annotations/3.6.0/maven-plugin-annotations-3.6.0.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-project/2.2.1/maven-project-2.2.1.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-settings/2.2.1/maven-settings-2.2.1.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-profile/2.2.1/maven-profile-2.2.1.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-artifact-manager/2.2.1/maven-artifact-manager-2.2.1.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-repository-metadata/2.2.1/maven-repository-metadata-2.2.1.jar:/home/benjamin/.m2/repository/org/apache/maven/wagon/wagon-provider-api/1.0-beta-6/wagon-provider-api-1.0-beta-6.jar:/home/benjamin/.m2/repository/backport-util-concurrent/backport-util-concurrent/3.1/backport-util-concurrent-3.1.jar:/home/benjamin/.m2/repository/org/apache/maven/maven-plugin-registry/2.2.1/maven-plugin-registry-2.2.1.jar:/home/benjamin/.m2/repository/org/codehaus/plexus/plexus-interpolation/1.11/plexus-interpolation-1.11.jar:/home/benjamin/.m2/repository/org/codehaus/plexus/plexus-utils/1.5.15/plexus-utils-1.5.15.jar:/home/benjamin/.m2/repository/org/codehaus/plexus/plexus-container-default/1.0-alpha-9-stable-1/plexus-container-default-1.0-alpha-9-stable-1.jar:/home/benjamin/.m2/repository/classworlds/classworlds/1.1-alpha-2/classworlds-1.1-alpha-2.jar:/home/benjamin/.m2/repository/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar";

}
