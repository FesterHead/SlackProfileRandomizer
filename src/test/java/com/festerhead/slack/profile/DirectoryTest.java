package com.festerhead.slack.profile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DirectoryTest {
    @TempDir
    public File tempDir;

    @Test
    public void testValidDirectory() throws IOException {
        // Create a directory with all expected files
        File directory = new File(tempDir, "validDir");
        directory.mkdir();
        createFiles(directory, "settings.json", "dicebear.txt", "emoji.txt", "status.txt");
        assertTrue(Randomizer.checkDirectory(directory));
    }

    @Test
    public void testMissingFile() throws IOException {
        // Create a directory with some missing files
        File directory = new File(tempDir, "missingFileDir");
        directory.mkdir();
        createFiles(directory, "settings.json", "dicebear.txt");
        assertFalse(Randomizer.checkDirectory(directory));
    }

    @Test
    public void testEmptyDirectory() throws IOException {
        // Create an empty directory
        File directory = new File(tempDir, "emptyDir");
        directory.mkdir();
        assertFalse(Randomizer.checkDirectory(directory));
    }

    @Test
    public void testNullDirectory() {
        // Test with a null directory argument
        assertThrows(IllegalArgumentException.class, () -> Randomizer.checkDirectory(null));
    }

    private void createFiles(File directory, String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            new File(directory, fileName).createNewFile();
        }
    }

}