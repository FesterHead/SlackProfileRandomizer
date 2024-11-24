package com.festerhead.slack.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.festerhead.slack.profile.exception.ProfileException;

@SpringBootApplication
public class Randomizer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Randomizer.class);
	private static final String CONFIGURATION_FOLDER = "configuration";

	public static void main(String[] args) {
		SpringApplication.run(Randomizer.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("APPLICATION STARTED");

		File configurationFolder = new File(CONFIGURATION_FOLDER);
		if (configurationFolder.isDirectory()) {
			logger.info("Configuration folder exists.");
			File[] configFiles = configurationFolder.listFiles();
			if (configFiles != null) {
				for (File file : configFiles) {
					if (file.isDirectory()) {
						if (!checkDirectory(file)) {
							logger.warn("Missing files!  Skipping directory: {}", file.getName());
						} else {
							logger.info("====================================================");
							try {
								logger.info("Processing {}", file.getName());
								Settings settings = Settings.fromJsonFile(
										new File(file + File.separator + "settings.json"));
								Photo.doUpdateProfilePhoto(settings,
										new File(file + File.separator + "dicebear.txt"));
								EmojiAndStatus.doUpdateEmojiAndStatus(settings,
										new File(file + File.separator + "emoji.txt"),
										new File(file + File.separator + "status.txt"));
							} catch (ProfileException e) {
								logger.error("Profile exception caught: {}.", e.getMessage());
							}
							logger.info("====================================================");
						}
					}
				}
			}
		} else {
			logger.error("Configuration folder does not exist.  Exiting.");
			System.exit(1);
		}

		logger.info("APPLICATION FINISHED");
		System.exit(0);

	}

	/**
	 * Checks the specified directory to ensure it contains the required
	 * configuration files.
	 *
	 * @param directory the directory to check
	 * @return true if all required files are present, false otherwise
	 * @throws IllegalArgumentException if the directory is null
	 */
	public static boolean checkDirectory(File directory) throws IllegalArgumentException {
		if (directory == null) {
			String message = "Directory cannot be null!";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		logger.info("Checking directory: {}", directory.getName());
		File[] files = directory.listFiles();
		if (files != null) {
			Set<String> expectedFiles = new HashSet<>(
					Arrays.asList("settings.json", "dicebear.txt", "emoji.txt", "status.txt"));
			List<String> missingFiles = new ArrayList<>();
			for (File file : files) {
				logger.info("  Looking at file: {}", file.getName());
				expectedFiles.remove(file.getName());
			}
			if (!expectedFiles.isEmpty()) {
				missingFiles.addAll(expectedFiles);
				logger.error("  Missing required files: {}", String.join(", ", missingFiles));
				return false;
			} else {
				logger.info("  All required files found!");
			}
		}
		return true;
	}

}
