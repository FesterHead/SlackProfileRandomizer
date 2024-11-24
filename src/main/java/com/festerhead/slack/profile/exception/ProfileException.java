/**
 * Represents an exception that occurs when working with a user's Slack profile.
 */
package com.festerhead.slack.profile.exception;

public class ProfileException extends Exception {
    /**
     * Constructs a new {@link ProfileException} with the specified error message.
     *
     * @param errorMessage the error message that describes the exception
     */
    public ProfileException(String errorMessage) {
        super(errorMessage);
    }
}