# Self Hosting FireFlow

This guide will walk you through the process of setting up and running a FireFlow server on your own device.

## Prerequisites

- A device to run the server on. (Your computer or another device that you are willing to keep running as long as you want the FireFlow server to be online.)
- Java 21 or newer installed on said device.
- A working Internet connection.

## Getting the FireFlow Jar

If you have a Github Account:
- Visit the [Build Actions](https://github.com/BlazeMCworld/FireFlow/actions/workflows/build.yml).
- Choose a successful build. (Any with a green checkmark.)
- Scroll down to the `Artifacts` section and download the `FireFlow` zip.
- Extract the zip and place the jar in a directory of your choice.

If you do not have a Github Account, and are not willing to make one:
- You can get the latest version from [nightly.link](https://nightly.link/BlazeMCworld/FireFlow/workflows/build/main/FireFlow.zip) (Third Party)
- Extract the zip and place the jar in a directory of your choice.

Building from source is also possible, but mainly used for testing and development.
- Download the source code
- Build the jar with `./gradlew shadowJar`
- The jar will be located in the `build/libs` directory and should end with `-all.jar`

If none of these options are possible, you can ask someone to share their jar file with you.

## Running the FireFlow Server for the first time

Begin by creating a new directory somewhere you want the server to be and move the FireFlow.jar to that directory if you haven't already.
Then open a terminal in that directory and run `java -jar server.jar` (Changing `server.jar` to match the name of your jar file).
After the server has started, which should only take a few seconds, you can stop it again, usually by pressing `ctrl + c`.

## Changing the Configuration

After you started the server the first time, there will be a `config.json` in the same directory.

- `motd` can be any text, supporting [MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags.
- `port` specifies the port the server should listen on, defaulting to 25565 (the default Minecraft port).
- `translations` specifies the language to use for the server. Available languages can be found [here](https://github.com/BlazeMCworld/FireFlow/tree/main/src/main/resources/languages)
- `limits.cpuUsage` is the maximum amount of CPU time a single space can use in nanoseconds.
- `limits.cpuHistory` is the amount of Minecraft ticks to record CPU usage for.
- `limits.spacesPerPlayer` defines how many spaces a player can own.
- `limits.totalSpaces` limits the total amount of spaces the server can have.
- `limits.spaceChunkDistance` decides how large the playable area of a space is. 5 for example means 5 chunks in every direction, so 10x10 chunks (10 because it goes both ways). Each chunk is 16x16 blocks, so the total playable area would be 160x160 blocks.

After changing the config, simply restart the server to make the changes take effect.