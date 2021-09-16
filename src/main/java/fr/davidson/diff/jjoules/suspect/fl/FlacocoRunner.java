package fr.davidson.diff.jjoules.suspect.fl;

import eu.stamp_project.testrunner.EntryPoint;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumFormula;

import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 30/06/2021
 */
public class FlacocoRunner {

    public static Map<Location, Suspiciousness> run(
            boolean junit4,
            String classpath,
            String rootPathDirectory,
            Set<String> testsToBeRun
    ) {
        FlacocoConfig config = new FlacocoConfig();
        config.setTestRunnerVerbose(true);
        config.setProjectPath(rootPathDirectory);
        config.setClasspath(classpath);
        config.setFamily(FlacocoConfig.FaultLocalizationFamily.SPECTRUM_BASED);
        config.setSpectrumFormula(SpectrumFormula.OCHIAI);
        if (junit4) {
            config.setjUnit4Tests(testsToBeRun);
        } else {
            config.setjUnit5Tests(testsToBeRun);
        }
        EntryPoint.verbose = false;

        Flacoco flacoco = new Flacoco(config);
        return flacoco.run().getDefaultSuspiciousnessMap();
    }
}
