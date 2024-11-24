package com.festerhead.slack.profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.festerhead.slack.profile.exception.ProfileException;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.profile.UsersProfileSetRequest;
import com.slack.api.methods.response.users.profile.UsersProfileSetResponse;
import com.slack.api.model.User;

public class EmojiAndStatus {
    private static final Logger logger = LoggerFactory.getLogger(EmojiAndStatus.class);

    /**
     * Updates the user's Slack profile with a random emoji and status message.
     * <p>
     * The emoji and status are updated only if the corresponding update flag in the
     * settings is enabled. If the `always` flag is set for a given field, that
     * field will be updated regardless of its current value.
     * If the current status is already one of those listed in the status file, both
     * emoji and status will be updated, assuming the update flags are set.
     *
     * @param settings   The settings object containing the Slack token and
     *                   configuration options.
     * @param emojiFile  The file containing a list of emojis to choose from.
     * @param statusFile The file containing a list of status messages to choose
     *                   from.
     * @return {@code true} if the profile was successfully updated, {@code false}
     *         otherwise.
     */
    public static boolean doUpdateEmojiAndStatus(Settings settings, File emojiFile, File statusFile) {

        String emoji = "";
        String status = "";

        try {
            String userId = Profile.getUserId(settings.getSlackToken());
            User.Profile userProfile = Profile.getUserProfile(userId, settings.getSlackToken());
            if (Objects.nonNull(userProfile)) {
                emoji = userProfile.getStatusEmoji();
                logger.info("Current emoji is: {}", emoji);
                status = userProfile.getStatusText();
                logger.info("Current status is: {}", status);

                if (settings.isUpdateEmoji() && (emoji.isEmpty() || (!emoji.isEmpty() && settings.isUpdateEmojiAlways()) || isStatusOneOfOurs(status, statusFile))) {
                    emoji = getRandomLine(emojiFile);
                }

                if (settings.isUpdateStatus()  && (status.isEmpty() || (!status.isEmpty() && settings.isUpdateStatusAlways()) || isStatusOneOfOurs(status, statusFile))) {
                    status = getRandomLine(statusFile);
                }

                logger.info("Emoji to set is: {}", emoji);
                logger.info("Status to set is: {}", status);

                userProfile.setStatusEmoji(emoji);
                userProfile.setStatusText(status);

                UsersProfileSetRequest profileRequest = UsersProfileSetRequest.builder()
                        .profile(userProfile)
                        .build();

                try {
                    UsersProfileSetResponse profileResponse = Slack.getInstance().methods(settings.getSlackToken())
                            .usersProfileSet(profileRequest);
                    if (!profileResponse.isOk()) {
                        throw new IOException(profileResponse.getError());
                    }
                    logger.info("Profile updated.");
                    logger.debug("Profile response: {}", profileResponse);
                } catch (IOException | SlackApiException e) {
                    logger.error("Failed to update Slack profile: ", e.getMessage());
                    return false;
                }

            } else {
                logger.warn("Could not update profile.  Profile is non-null.");
                return false;
            }
        } catch (ProfileException e) {
            logger.error("Error getting Slack user ID: {}", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Retrieves a random line from the specified file.
     *
     * @param file The file to read.
     * @return A random line from the file, or an empty string if the file is empty
     *         or an error occurs.
     */
    private static String getRandomLine(File file) {
        String randomLine = "";

        try {
            List<String> entryList = Files.readAllLines(file.toPath());
            if (entryList.isEmpty()) {
                throw new IOException("Could not read file.");
            }
            randomLine = entryList.get(new Random().nextInt(entryList.size()));
        } catch (IOException e) {
            logger.error("Error reading {}: {}", file.getName(), e.getMessage());
        }

        return randomLine;
    }

    /**
     * Checks if the given status string is present in the specified status file.
     *
     * @param status     The status string to search for.
     * @param statusFile The file containing a list of status strings, one per line.
     * @return {@code true} if the status is found in the file, {@code false}
     *         otherwise.
     */
    private static boolean isStatusOneOfOurs(String status, File statusFile) {
        List<String> statusList;
        try {
            statusList = Files.readAllLines(statusFile.toPath());
            if (statusList.contains(status)) {
                logger.info("Status \"{}\" is one of ours!", status);
                return true;
            } else {
                logger.info("Status \"{}\" is NOT one of ours!", status);
                return false;
            }
        } catch (IOException e) {
            logger.error("Error reading status file: {}", e.getMessage());
            return false;
        }
    }
}
