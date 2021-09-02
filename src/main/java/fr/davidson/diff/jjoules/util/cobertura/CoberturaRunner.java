package fr.davidson.diff.jjoules.util.cobertura;

import eu.stamp_project.diff_test_selection.coverage.Coverage;
import fr.davidson.diff.jjoules.util.maven.MavenRunner;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 01/09/2021
 */
public class CoberturaRunner {

    public static Map<String, Coverage> run(
            final String rootPathFolder,
            final Map<String, List<String>> consideredTestsNames
    ) {
        final CoberturaDataCollector coberturaDataCollector = new CoberturaDataCollector();
        for (String testClassName : consideredTestsNames.keySet()) {
            final Properties properties = new Properties();
            properties.setProperty("test", testClassName);
            MavenRunner.runGoals(
                    rootPathFolder,
                    properties,
                    "clean",
                    "compile",
                    "cobertura:instrument",
                    "test"
            );
            coberturaDataCollector.collectData(testClassName, consideredTestsNames.get(testClassName), rootPathFolder);
        }
        return coberturaDataCollector.getCoveragePerTestMethodName();
    }

}
