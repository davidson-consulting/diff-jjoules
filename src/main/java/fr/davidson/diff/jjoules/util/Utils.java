package fr.davidson.diff.jjoules.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static void gitResetHard(String pathToFolder) {
        try (Git git = Git.open(new File(pathToFolder))) {
            git.reset().setMode(ResetCommand.ResetType.HARD).call();
            // must delete module-info.java TODO checkout this
            try (Stream<Path> walk = Files.walk(Paths.get(pathToFolder))) {
                walk.filter(path -> path.endsWith("module-info.java"))
                        .forEach(path -> path.toFile().delete());
            }
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readClasspathFile(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.lines().collect(Collectors.joining(Constants.PATH_SEPARATOR));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String toFullQualifiedName(String className, String methodName) {
        return className + "#" + methodName;
    }

    public static <T> void addToGivenMap(final String key, T value, Map<String, List<T>> givenMap) {
        if (!givenMap.containsKey(key)) {
            givenMap.put(key, new ArrayList<>());
        }
        givenMap.get(key).add(value);
    }
}
