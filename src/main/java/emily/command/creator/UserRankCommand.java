/*
 * Copyright 2017 github.com/kaaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package emily.command.creator;

import emily.command.CommandVisibility;
import emily.core.AbstractCommand;
import emily.db.controllers.CRank;
import emily.db.controllers.CUser;
import emily.db.controllers.CUserRank;
import emily.db.model.ORank;
import emily.db.model.OUser;
import emily.db.model.OUserRank;
import emily.handler.SecurityHandler;
import emily.handler.Template;
import emily.main.Config;
import emily.main.DiscordBot;
import emily.permission.SimpleRank;
import emily.util.DisUtil;
import emily.util.Misc;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * !userrank
 */
public class UserRankCommand extends AbstractCommand {

    public UserRankCommand() {
        super();
    }

    @Override
    public String getDescription() {
        return "This command is intended for bot admins";
    }

    @Override
    public String getCommand() {
        return "userrank";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "userrank <user>                   //check rank of user",
                "userrank <user> <rank>            //gives a rank to user",
                "userrank <user> perm <+/-> <node> //adds/removes permission from user",
                "userrank permlist                 //lists all permissions",
                "userrank ranks                    //lists all ranks",
        };
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "ur"
        };
    }

    @Override
    public CommandVisibility getVisibility() {
        return CommandVisibility.PUBLIC;
    }

    @Override
    public String execute(DiscordBot bot, String[] args, MessageChannel channel, User author) {
        SimpleRank authorRank = bot.security.getSimpleRank(author);
        if (!authorRank.isAtLeast(SimpleRank.BOT_ADMIN)) {
            return Template.get("no_permission");
        }
        if (args.length == 0) {
            return Template.get("command_invalid_use");
        }
        if (args[0].equals("permlist")) {
            return "Available permissions: " + Config.EOL +
                    tableFor(Arrays.asList(OUser.PermissionNode.values()));
        }
        if (args[0].equals("ranks")) {
            return "Available permissions: " + Config.EOL +
                    tableFor(SimpleRank.values());
        }
        User user;
        if (DisUtil.isUserMention(args[0])) {
            user = channel.getJDA().getUserById(DisUtil.mentionToId(args[0]));
        } else if (args[0].matches("^i\\d+$")) {
            user = channel.getJDA().getUserById(CUser.getCachedDiscordId(Integer.parseInt(args[0].substring(1))));
        } else {
            Member member = DisUtil.findUserIn((TextChannel) channel, args[0]);
            if (member != null) {
                user = member.getUser();
            } else {
                user = null;
            }
        }
        if (user == null) {
            return Template.get("cant_find_user", args[0]);
        }
        SimpleRank targetOldRank = bot.security.getSimpleRank(user);
        OUser dbUser = CUser.findBy(user.getId());
        if (args.length == 1) {
            OUserRank userRank = CUserRank.findBy(user.getId());
            if (userRank.rankId == 0 && !targetOldRank.isAtLeast(SimpleRank.CREATOR)) {
                return Template.get("command_userrank_no_rank", user.getName());
            } else if (targetOldRank.isAtLeast(SimpleRank.CREATOR)) {
                return Template.get("command_userrank_rank", user.getName(), "creator");
            } else {
                return Template.get("command_userrank_rank", user.getName(), CRank.findById(userRank.rankId).codeName);
            }
        } else if (args[1].equals("perm")) {

            if (args.length < 4) {
                if (dbUser.getPermission().isEmpty()) {
                    return "No permissions set for " + user.getName();
                }
                return "Permissions for " + user.getName() + Config.EOL +
                        tableFor(dbUser.getPermission());
            }
            boolean adding = true;
            switch (args[2].toLowerCase()) {
                case "-":
                case "del":
                case "rem":
                case "min":
                case "remove":
                case "delete":
                    adding = false;
                    break;
            }
            try {
                OUser.PermissionNode node = OUser.PermissionNode.valueOf(args[3].toUpperCase());
                if (adding) {
                    dbUser.addPermission(node);
                    CUser.update(dbUser);
                    return String.format(":+1: adding `%s` to %s", node.toString(), user.getName());
                }
                dbUser.removePermission(node);
                CUser.update(dbUser);
                return String.format(":+1: removed `%s` from %s", node.toString(), user.getName());
            } catch (Exception e) {
                return "Invalid permission node";
            }
        } else if (args.length == 2) {
            SimpleRank targetNewRank = args[1].equals("none") ? SimpleRank.USER : SimpleRank.findRank(args[1]);
            if (targetNewRank == null) {
                return Template.get("command_userrank_rank_not_exists", args[1]);
            }
            if (!authorRank.isHigherThan(targetNewRank) || !authorRank.isHigherThan(targetOldRank)) {
                return Template.get("no_permission");
            }
            ORank targetDbRank = CRank.findBy(args[1]);
            if (targetDbRank.id == 0) {
                targetDbRank.codeName = targetNewRank.name();
                targetDbRank.fullName = targetNewRank.name().toLowerCase();
                CRank.insert(targetDbRank);
            }
            OUserRank userRank = CUserRank.findBy(CUser.getCachedId(user.getId(), user.getName()));
            userRank.rankId = targetDbRank.id;
            CUserRank.insertOrUpdate(userRank);
            SecurityHandler.initialize();
            return Template.get("command_userrank_rank", user.getName(), targetDbRank.codeName);
        }
        return Template.get("command_invalid_use");
    }

    private String tableFor(Collection<OUser.PermissionNode> nodes) {
        List<List<String>> tbl = new ArrayList<>();
        for (OUser.PermissionNode node : nodes) {
            tbl.add(Arrays.asList(node.toString(), node.getDescription()));
        }
        return Misc.makeAsciiTable(Arrays.asList("code", "description"), tbl, null);
    }

    private String tableFor(SimpleRank[] ranks) {
        List<List<String>> tbl = new ArrayList<>();
        for (int i = ranks.length - 1; i >= 0; i--) {
            SimpleRank rank = ranks[i];
            tbl.add(Arrays.asList(rank.toString(), rank.getDescription()));
        }
        return Misc.makeAsciiTable(Arrays.asList("code", "description"), tbl, null);
    }
}