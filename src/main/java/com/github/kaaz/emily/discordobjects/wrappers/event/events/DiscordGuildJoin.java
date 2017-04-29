package com.github.kaaz.emily.discordobjects.wrappers.event.events;

import com.github.kaaz.emily.discordobjects.wrappers.Guild;
import com.github.kaaz.emily.discordobjects.wrappers.event.BotEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;

/**
 * Made by nija123098 on 3/31/2017.
 */
public class DiscordGuildJoin implements BotEvent {
    private GuildCreateEvent event;
    public DiscordGuildJoin(GuildCreateEvent event) {
        this.event = event;
    }
    public Guild getGuild(){
        return Guild.getGuild(this.event.getGuild());
    }
}