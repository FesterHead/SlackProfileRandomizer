package com.festerhead.slack.profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersSetPhotoRequest;
import com.slack.api.methods.response.users.UsersSetPhotoResponse;

public class Photo {
    private static final Logger logger = LoggerFactory.getLogger(Photo.class);
    private static String dicebearRootUrl = "https://api.dicebear.com/9.x/";
    private static String dicebearImageFormat = "png";
    private static String dicebearDefaultStyle = "shapes";

    /**
     * Updates the user's Slack profile photo using a randomly generated image from the Dicebear API.
     *
     * @param settings The settings object containing the Slack token and photo seed.
     * @param dicebearStyles The file containing the list of Dicebear styles to use.
     * @return `true` if the profile photo was successfully updated, `false` otherwise.
     */
    public static boolean doUpdateProfilePhoto(Settings settings, File dicebearStyles) {
        if (settings == null) {
            logger.error("Settings cannot be null!");
            return false;
        }
        if (dicebearStyles == null) {
            logger.error("Dicebear file cannot be null!");
            return false;
        }
        String style;
        try {
            List<String> styles = Files.readAllLines(dicebearStyles.toPath());
            if (styles.isEmpty()) {
                logger.warn("Dicebear styles are empty!");
                logger.warn("Using default style {}.", dicebearDefaultStyle);
                style = dicebearDefaultStyle;
            }
            style = styles.get(new Random().nextInt(styles.size()));
            logger.info("Dicebear style: {}", style);
        } catch (IOException e) {
            logger.warn("Error reading dicebear.txt: {}", e.getMessage());
            logger.warn("Using default style {}.", dicebearDefaultStyle);
            style = dicebearDefaultStyle;
        }

        String dicebearSeed = settings.getPhotoSeed();
        if (ObjectUtils.isEmpty(dicebearSeed)) {
            dicebearSeed = UUID.randomUUID().toString();
        }

        String dicebearImageUrl = String.format("%s%s/%s?seed=%s", dicebearRootUrl, style, dicebearImageFormat,
                dicebearSeed);
        logger.info("Dicebear url: {}", dicebearImageUrl);
        RestTemplate restTemplate = new RestTemplate();
        byte[] imageBytes = restTemplate.getForObject(dicebearImageUrl, byte[].class);
        if (imageBytes == null || imageBytes.length == 0) {
            logger.error("Failed to fetch image from Dicebear API.  Did not update Slack profile photo.");
            return false;
        }

        try {
            UsersSetPhotoResponse photoResponse = Slack.getInstance().methods(settings.getSlackToken())
                    .usersSetPhoto(UsersSetPhotoRequest.builder()
                            .imageData(imageBytes)
                            .build());
            if (!photoResponse.isOk()) {
                logger.error("Failed to update photo in Slack profile: ", photoResponse.getError());
                return false;
            }
            logger.debug("Profile photo updated: {}", photoResponse);
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to update photo in Slack profile: ", e.getMessage());
            return false;
        }

        return true;
    }

}
