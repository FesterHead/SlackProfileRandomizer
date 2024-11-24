package com.festerhead.slack.profile;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.festerhead.slack.profile.exception.ProfileException;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest;
import com.slack.api.methods.response.auth.AuthTestResponse;
import com.slack.api.methods.response.users.profile.UsersProfileGetResponse;
import com.slack.api.model.User;

/*
 * This class provides methods for retrieving and updating Slack user profiles.
 */
public class Profile {
    private static final Logger logger = LoggerFactory.getLogger(Profile.class);

    /**
     * Retrieves the Slack user ID for the specified Slack API token.
     *
     * @param slackToken the Slack API token
     * @return the Slack user ID, or throws a {@link ProfileException} if the user
     *         ID could not be retrieved
     * @throws ProfileException if there is an error retrieving the Slack user ID
     */
    public static String getUserId(String slackToken) throws ProfileException {
        if (slackToken == null) {
            String message = "Slack token cannot be null!";
            logger.error(message);
            throw new ProfileException(message);
        }
        String userId = null;
        AuthTestResponse response;
        try {
            response = Slack.getInstance().methods(slackToken).authTest(req -> req);
            if (!response.isOk()) {
                String message = "Unable to authenticate with Slack!";
                logger.error(message);
                throw new ProfileException(message);
            }
            userId = response.getUserId();
            logger.info("User ID is: {}", userId);
        } catch (IOException | SlackApiException e) {
            String errorMessage = "Unable to get Slack user id.";
            logger.error(errorMessage);
            throw new ProfileException(errorMessage);
        }

        return userId;
    }

    /**
     * Retrieves the Slack user profile for the specified user ID and Slack token.
     *
     * @param userId     the Slack user ID
     * @param slackToken the Slack API token
     * @return the Slack user profile, or null if the profile could not be retrieved
     * @throws ProfileException
     */
    public static User.Profile getUserProfile(String userId, String slackToken) throws ProfileException {
        if (userId == null) {
            String message = "User ID cannot be null!";
            logger.error(message);
            throw new ProfileException(message);
        }
        if (slackToken == null) {
            String message = "Slack token cannot be null!";
            logger.error(message);
            throw new ProfileException(message);
        }
        User.Profile profile = null;
        try {
            UsersProfileGetResponse profileResponse = Slack.getInstance().methods(slackToken).usersProfileGet(
                    UsersProfileGetRequest.builder().user(userId).build());
            if (!profileResponse.isOk() || Objects.isNull(profileResponse.getProfile())) {
                String message = profileResponse.getError();
                logger.error(message);
                throw new ProfileException(message);
            }
            profile = profileResponse.getProfile();
            logger.info("Profile retrieved.");
            logger.debug("Profile: {}", profileResponse);
        } catch (IOException | SlackApiException e) {
            logger.warn("Unable to get Slack user profile: ", e.getMessage());
            logger.info("Returning null.");
        }

        return profile;
    }
}
