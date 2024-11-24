package com.festerhead.slack.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.festerhead.slack.profile.exception.ProfileException;

public class ProfileTest {

    @Test
    public void testGetUserId_ValidToken() throws ProfileException {
        String userId = Profile.getUserId(System.getenv("SLACK_TOKEN_FOR_TESTING"));
        assertEquals(System.getenv("SLACK_USER_ID_FOR_TESTING"), userId);
    }

    @Test
    public void testGetUserId_InvalidToken() {
        assertThrows(ProfileException.class, () -> Profile.getUserId("invalid-token"));
    }

    @Test
    public void testGetUserProfile_Success() throws ProfileException {
        assertNotNull(Profile.getUserProfile(System.getenv("SLACK_USER_ID_FOR_TESTING"), System.getenv("SLACK_TOKEN_FOR_TESTING")));
    }

    @Test
    public void testGetUserProfile_Error() throws ProfileException {
        assertThrows(ProfileException.class, () -> Profile.getUserProfile(System.getenv("SLACK_USER_ID_FOR_TESTING"), "invalid-token"));
        assertThrows(ProfileException.class, () -> Profile.getUserProfile("invalid-user-id", "invalid-token"));
    }
}