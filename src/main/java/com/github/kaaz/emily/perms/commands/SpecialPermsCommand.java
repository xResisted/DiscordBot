package com.github.kaaz.emily.perms.commands;

import com.github.kaaz.emily.command.AbstractCommand;
import com.github.kaaz.emily.command.ModuleLevel;
import com.github.kaaz.emily.command.anotations.Argument;
import com.github.kaaz.emily.command.anotations.Command;
import com.github.kaaz.emily.config.ConfigHandler;
import com.github.kaaz.emily.discordobjects.helpers.MessageMaker;
import com.github.kaaz.emily.discordobjects.wrappers.Guild;
import com.github.kaaz.emily.perms.BotRole;
import com.github.kaaz.emily.perms.configs.specialperms.GuildSpecialPermsConfig;
import com.github.kaaz.emily.perms.configs.specialperms.SpecialPermsContainer;

/**
 * Made by nija123098 on 6/13/2017.
 */
public class SpecialPermsCommand extends AbstractCommand {
    public SpecialPermsCommand() {
        super("commandadmin", BotRole.GUILD_TRUSTEE, ModuleLevel.ADMINISTRATIVE, "ca", null, "Helps set command approval and restrictions on Emily commands based on channel and ranks");
    }
    @Command
    public void command(Guild guild, MessageMaker maker, @Argument Boolean enable){
        ConfigHandler.setSetting(GuildSpecialPermsConfig.class, guild, enable ? new SpecialPermsContainer(guild) : null);
    }
}