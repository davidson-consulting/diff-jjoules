package fr.davidson.diff.jjoules;

import fr.davidson.diff.jjoules.report.ReportEnum;
import fr.davidson.diff.jjoules.util.wrapper.WrapperEnum;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powerapi.jjoules.EnergySample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 01/12/2021
 */
public class DiffJJoulesStepTest {

    private File tempDir;

    private File fdV1;

    private File fdV2;

    private Git remote;

    @BeforeEach
    void setUp() throws GitAPIException, IOException {
        this.tempDir = new File("target");
        //extractLibPerf();
        //addDir(this.tempDir.getAbsolutePath());
        //System.loadLibrary("perf");
        this.fdV1 = new File(tempDir, "v1");
        this.fdV2 = new File(tempDir, "v2");
        this.remote = Git.cloneRepository()
                .setDirectory(fdV1)
                .setURI("https://github.com/davidson-consulting/diff-jjoules-demo.git")
                .call();
        Git.cloneRepository()
                .setDirectory(fdV2)
                .setURI("https://github.com/davidson-consulting/diff-jjoules-demo.git")
                .setBranch("pure-inc-instr")
                .call()
                .close();
    }



    @Test
    void test() {
        final Configuration configuration = new Configuration(
                this.fdV1.getAbsolutePath() + "/",
                this.fdV2.getAbsolutePath() + "/",
                5,
                this.fdV1.getAbsolutePath() + "/diff-jjoules/",
                "",
                "",
                "report",
                true,
                true,
                ReportEnum.NONE,
                WrapperEnum.MAVEN,
                true
        );
        new DiffJJoulesStep().run(configuration);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(this.fdV1);
        FileUtils.deleteDirectory(this.fdV2);
        this.remote.close();
    }

    private void extractLibPerf() throws IOException {
        final String libperfDotSO = "/libperf.so";
        final String extractFilePath = this.tempDir.getAbsolutePath() + libperfDotSO;
        try (final InputStream resourceAsStream = EnergySample.class.getResourceAsStream(libperfDotSO)) {
            try (final FileOutputStream writer = new FileOutputStream(extractFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
                    writer.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private void addDir(String s) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            if (Arrays.asList(paths).contains(s)) {
                return;
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }
}
