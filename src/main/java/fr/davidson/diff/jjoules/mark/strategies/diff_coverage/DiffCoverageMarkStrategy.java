package fr.davidson.diff.jjoules.mark.strategies.diff_coverage;

import fr.davidson.diff.jjoules.Configuration;
import fr.davidson.diff.jjoules.delta.data.Data;
import fr.davidson.diff.jjoules.delta.data.Datas;
import fr.davidson.diff.jjoules.delta.data.Delta;
import fr.davidson.diff.jjoules.delta.data.Deltas;
import fr.davidson.diff.jjoules.mark.strategies.AbstractCoverageMarkStrategy;
import fr.davidson.diff.jjoules.selection.NewCoverage;
import fr.davidson.diff.jjoules.util.Constants;
import fr.davidson.diff.jjoules.util.FullQualifiedName;
import fr.davidson.diff.jjoules.util.JSONUtils;
import fr.davidson.diff.jjoules.util.MethodNamesPerClassNames;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 06/05/2022
 */
public class DiffCoverageMarkStrategy extends AbstractCoverageMarkStrategy {

    private String computeDiff(String pathSrcV1, String pathSrcV2) {
        final String command = String.join(" ", new String[]{
                "diff",
                "-r",
                pathSrcV1,
                pathSrcV2
        });
        System.out.println("Running: " + command);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Process process;
        try {
            process = Runtime.getRuntime().exec(command, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Future<?> submit = executor.submit(() -> {
            try {
                process.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            submit.get(5, TimeUnit.SECONDS);
            if (process != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                int current;
                StringBuilder output = new StringBuilder();
                while (true) {
                    try {
                        if ((current = inputStreamReader.read()) == -1) {
                            break;
                        }
                        output.append((char) current);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return output.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
            submit.cancel(true);
            executor.shutdownNow();
        }
    }

    @Override
    public boolean applyStrategy(
            Configuration configuration,
            Datas dataV1,
            Datas dataV2,
            Deltas deltaPerTestMethodName,
            MethodNamesPerClassNames consideredTest) {
        this.initCoverages(configuration);
        Data deltaOmega = new Data();
        final String diff = this.computeDiff(configuration.getPathToFirstVersion() + "/src/main/java/", configuration.getPathToSecondVersion() + "/src/main/java/");
        final String[] diffLines = diff.split(Constants.NEW_LINE);
        final HashMap<String, Long> nbDiffLinePerTestName = new HashMap<>();
        int nbDiffLine = 0;
        final Pattern p = Pattern.compile("\\d+(,\\d+)?[adc]\\d+(,\\d+)?");
        for (int i = 0; i < diffLines.length; i++) {
            final String diffLine = diffLines[i];
            if (diffLine.startsWith("diff -r") && diffLine.endsWith(".java")) {
                final String[] splitLine = diffLine.split(" ");
                final String fullPathnameV1 = splitLine[2];
                final String fullPathnameV2 = splitLine[3];
                String current;
                do {
                    current = diffLines[++i];
                    if (p.matcher(current).matches()) {
                        if (current.contains("a")) {
                            nbDiffLine += matchDiffAndCoverage(nbDiffLinePerTestName, coverageV2, fullPathnameV2, current, "a", 1);
                        } else if (current.contains("d")) {
                            nbDiffLine += matchDiffAndCoverage(nbDiffLinePerTestName, coverageV1, fullPathnameV1, current, "d", 0);
                        } else if (current.contains("c")) {
                            int currentNbDiffLine = matchDiffAndCoverage(nbDiffLinePerTestName, coverageV1, fullPathnameV1, current, "c", 0);
                            currentNbDiffLine += matchDiffAndCoverage(nbDiffLinePerTestName, coverageV2, fullPathnameV2, current, "c", 1);
                            nbDiffLine += currentNbDiffLine > 1 ? currentNbDiffLine / 2 : currentNbDiffLine;
                        } else {
                            throw new RuntimeException("Action changes is not recognized! " + current);
                        }
                    }
                } while (!current.startsWith("diff -r") && i+1 < diffLines.length);
            }
        }
        for (String testClassName : consideredTest.keySet()) {
            for (String testMethodName : consideredTest.get(testClassName)) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testMethodName);
                final Delta delta = deltaPerTestMethodName.get(fullQualifiedName.toString());
                final Long nbDiffLineCovered = nbDiffLinePerTestName.get(fullQualifiedName.toString());
                final double factor = ((double) (nbDiffLineCovered == null ? 0.0D : (double) nbDiffLineCovered) / (double) nbDiffLine);
                deltaOmega = deltaOmega.add(delta, factor);
            }
        }
        return deltaOmega.cycles <= 0;
    }

    private int matchDiffAndCoverage(
            Map<String, Long> nbDiffLinePerTestName,
            NewCoverage coverage,
            String fullPathname,
            String changes,
            String action,
            int index) {
        // 176c176
        // 224,225c224,225
        final String changedLines = changes.split(action)[index];
        final String[] splittedChangedLines = changedLines.split(",");
        final int startingLine = Integer.parseInt(splittedChangedLines[0]);
        final int endingLine = splittedChangedLines.length > 1 ? Integer.parseInt(splittedChangedLines[1]) : startingLine;
        final int nbDiffLine = endingLine == startingLine ? 1 : endingLine - startingLine;
        for (String testClassName : coverage.keySet()) {
            for (String testName : coverage.get(testClassName).keySet()) {
                final FullQualifiedName fullQualifiedName = new FullQualifiedName(testClassName, testName);
                final String fullQualifiedNameFromFullPathname = fullPathname.split("\\.java")[0].split("/java/")[1].replace("/", ".");
                if (coverage.get(testClassName).get(testName).containsKey(fullQualifiedNameFromFullPathname)) {
                    if (!nbDiffLinePerTestName.containsKey(fullQualifiedName.toString())) {
                        nbDiffLinePerTestName.put(fullQualifiedName.toString(), 0L);
                    }
                    nbDiffLinePerTestName.put(fullQualifiedName.toString(), nbDiffLinePerTestName.get(fullQualifiedName.toString()) +
                            coverage.get(testClassName)
                                    .get(testName)
                                    .get(fullQualifiedNameFromFullPathname)
                                    .stream()
                                    .filter(coveredLine -> startingLine <= coveredLine && coveredLine <= endingLine)
                                    .count()
                    );
                }
            }
        }
        return nbDiffLine;
    }
}
