package com.github.kaaz.discordbot.launcher;

import com.github.kaaz.discordbot.botconfig.BotConfig;
import com.wezinkhof.configuration.ConfigurationBuilder;

import java.io.File;

/**
 * Made by nija123098 on 2/20/2017.
 */
public class Launcher {
    public static void main(String[] args) {
        new ConfigurationBuilder(BotConfig.class, new File("C:\\Users\\Jack\\IdeaWorkspace\\DiscordBot\\target\\application.cfg"));
    }
}
