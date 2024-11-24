# SlackProfileRandomizer

Tool to randomly update your Slack profile picture, emoji, and status.

# Setup

## Slack App

- Create a Slack app with `users.profile:read` and `users.profile:write` scopes and install into your workspace.
- Create an OAuth token

Read the Slack documentation or search for how to do this.

## Configuration

- Create a `configuration` folder.  Inside there, create another folder (e.g. the name of your Slack workspace) and inside there create the following files:
    - `settings.json`
    - `dicebear.txt`
    - `emoji.txt`
    - `status.txt`

### Example

```
configuration
    workspace1
        settings.json
        dicebear.txt
        emoji.txt
        status.txt
    workspace2
        settings.json
        dicebear.txt
        emoji.txt
        status.txt
```

### settings.json

JSON format:
```json
{
    "slackToken": "string",             // Slack application OAuth token
    "updatePhoto": true||false,         // Update photo
    "photoSeed": "string",              // Dicebear seed, leave blank to use random UUID seed
    "updateEmoji": true||false,         // Update emoji
    "updateEmojiAlways": true||false,   // Update emoji always, even if one is already set
    "updateStatus": true||false,        // Update status
    "updateStatusAlways": true||false   // Update status always, even if one is already set
}
```

I use the Outlook application for my work workspace which sets my emoji and status based on my work presence and I don't want to over-write it so I use `updateEmojiAlways` and `updateStatusAlways` to `false`.  However, if the existing status is one from the `status.txt` file it is OK to update.


#### Example I use for work

```json
{
    "slackToken": token",
    "updatePhoto": true,
    "photoSeed": "",
    "updateEmoji": true,
    "updateEmojiAlways": false,
    "updateStatus": true,
    "updateStatusAlways": false
}
```

#### Example I use for personal

```json
{
    "slackToken": "true",
    "updatePhoto": true,
    "photoSeed": "",
    "updateEmoji": true,
    "updateEmojiAlways": true,
    "updateStatus": true,
    "updateStatusAlways": true
}
```


### dicebear.txt

The program will select a random style from this file.

- A line separated list of styles from https://www.dicebear.com/styles/
- Must have at least one style
- Styles can be repeated and be put in any order

#### Example

```
shapes
adventurer
shapes
adventurer-neutral
shapes
```

### emoji.txt

The program will select a random emoji from this file.

- A line separated list of emojis
- Must have at least one emoji
- Emojis can be repeated and be put in any order

#### Example

```
:dolphin:
:door:
:doughnut:
:dragon:
:dragon_face:
:dress:
```

### status.txt

The program will select a random status from this file.

- A line separated list of statuses
- Must have at least one status
- Statuses can be repeated and be put in any order

#### Example

```
normalizing power
obfuscating quigley matrix
over constraining dirty industry calculations
partitioning city grid singularities
perturbing matrices
```

# How to build

Assumes Java (I use Corretto 17) is and Maven are available.

From the project directory, run `mvn -DskipTests clean compile package` which will create a zip file in the target folder.

# How to test

Add the following to your `.vscode/settings.json` file:
```json
    "java.test.config": {
        "name": "My Test Config",
        "env": {
            "SLACK_TOKEN_FOR_TESTING": "",
            "SLACK_USER_ID_FOR_TESTING": ""
        }
    }
```

Use your own token and id.  Then run the tests.  This doesn't worj with maven.

# How to run

Assumes Java (I use Corretto 17) is available.

- Move the zip file to where you want to run the program and unzip.
- Run the program with `java -jar slack-profile-randomizer-<version>.jar` where `<version>` is the version.
- The program will log to the `logs` folder.  Read `logback-spring.xml` for more details.

## Example crontab

```
5 * * * * cd /mnt/projects/SlackStatusChanger && java -jar slack-profile-randomizer-1.0.0.jar
```

[5 * * * *](https://crontab.guru/#5_*_*_*_*) means every minute 5, i.e. every `xx:05`.
