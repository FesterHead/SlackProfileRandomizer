package com.festerhead.slack.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.festerhead.slack.profile.exception.ProfileException;

public class SettingsTest {

    @TempDir
    public File tempDir;

    @Test
    public void testFromJsonFile_ValidJson() throws IOException, ProfileException {
        String jsonContent = "{\n" +
                "  \"slackToken\": \"my-token\",\n" +
                "  \"updatePhoto\": true,\n" +
                "  \"photoSeed\": \"my-seed\",\n" +
                "  \"updateEmoji\": true,\n" +
                "  \"updateEmojiAlways\": true,\n" +
                "  \"updateStatus\": true,\n" +
                "  \"updateStatusAlways\": true\n" +
                "}";

        File settingsFile = new File(tempDir, "settings.json");
        writeToFile(settingsFile, jsonContent);

        Settings settings = Settings.fromJsonFile(settingsFile);

        assertNotNull(settings);
        assertEquals("my-token", settings.getSlackToken());
        assertTrue(settings.isUpdatePhoto());
        assertEquals("my-seed", settings.getPhotoSeed());
        assertTrue(settings.isUpdateEmoji());
        assertTrue(settings.isUpdateEmojiAlways());
        assertTrue(settings.isUpdateStatus());
        assertTrue(settings.isUpdateStatusAlways());
    }

    @Test
    public void testFromJsonFile_EmptySeed() throws IOException, ProfileException {
        String jsonContent = "{\n" +
                "  \"slackToken\": \"my-token\",\n" +
                "  \"updatePhoto\": true,\n" +
                "  \"photoSeed\": \"\",\n" +
                "  \"updateEmoji\": true,\n" +
                "  \"updateEmojiAlways\": true,\n" +
                "  \"updateStatus\": true,\n" +
                "  \"updateStatusAlways\": true\n" +
                "}";

        File settingsFile = new File(tempDir, "settings.json");
        writeToFile(settingsFile, jsonContent);

        Settings settings = Settings.fromJsonFile(settingsFile);

        assertEquals("", settings.getPhotoSeed());
    }

    @Test
    public void testFromJsonFile_InvalidJson() throws IOException {
        String invalidJson = "{invalid json}";
        File settingsFile = new File(tempDir, "settings.json");
        writeToFile(settingsFile, invalidJson);

        assertThrows(ProfileException.class, () -> Settings.fromJsonFile(settingsFile));
    }

    @Test
    public void testFromJsonFile_MissingFile() throws ProfileException {
        File missingFile = new File(tempDir, "missing.json");

        assertThrows(ProfileException.class, () -> Settings.fromJsonFile(missingFile));
    }

    private void writeToFile(File file, String content) throws IOException {
        FileUtils.writeStringToFile(file, content, "UTF-8");
    }
}