package com.festerhead.slack.profile;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festerhead.slack.profile.exception.ProfileException;

import lombok.Data;

@Data
public class Settings {
    private String slackToken;
    private boolean updatePhoto;
    private String photoSeed;
    private boolean updateEmoji;
    private boolean updateEmojiAlways;
    private boolean updateStatus;
    private boolean updateStatusAlways;

    public static Settings fromJsonFile(File jsonFile) throws ProfileException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonFile, Settings.class);
        } catch (IOException e) {
            String message = "Error reading from settings file!";
            throw new ProfileException(message);
        }
    }
}
