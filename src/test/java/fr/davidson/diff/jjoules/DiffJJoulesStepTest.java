package fr.davidson.diff.jjoules;

import org.apache.maven.surefire.shared.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DiffJJoulesStepTest {

    @TempDir
    File tempDir;

    private File remoteDir;

    private Git remote;

    @BeforeEach
    public void setUp() throws GitAPIException, IOException {
        remoteDir = new File(tempDir, "remote");
        remote = Git.init().setDirectory(remoteDir).call();
        remote.commit().setMessage("Initial commit").call();
    }

    @AfterEach
    public void tearDown() throws IOException {
        remote.close();
        FileUtils.deleteDirectory(remoteDir);
    }

    @Test
    void testGitResetHard() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DiffJJoulesStep diffJJoulesStep = new DiffJJoulesStep();
        Method method = DiffJJoulesStep.class.getDeclaredMethod("gitResetHard", String.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(diffJJoulesStep, remoteDir.getAbsolutePath()));
    }
}
