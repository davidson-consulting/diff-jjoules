package fr.davidson.diff.jjoules;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    void testDeleteOutputFdFailed() {
        Exception e = assertThrows(RuntimeException.class, () -> {
            new Configuration(null, null, 0, "src/test/resources/json", null, null, null, false, false, null, null, false);
        });

        String expectedMessage = String.format("Something went wrong when trying to delete the folder %s, please check your configuration", new File("src/test/resources/json").getPath());
        assertEquals(expectedMessage, e.getMessage());
        assertEquals(DirectoryNotEmptyException.class, e.getCause().getClass());
    }
}
