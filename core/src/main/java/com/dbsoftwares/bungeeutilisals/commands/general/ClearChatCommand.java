package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.dbsoftwares.bungeeutilisals.utils.redis.RedisMessenger;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super(
                "clearchat",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("clearchat.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("clearchat.permission")
        );
    }

    public static void clearChat(String server) {
        if (server.equalsIgnoreCase("ALL")) {
            ProxyServer.getInstance().getPlayers().forEach(ClearChatCommand::clearChat);
        } else {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

            Validate.ifNotNull(info, serverInfo -> info.getPlayers().forEach(ClearChatCommand::clearChat));
        }
    }

    private static void clearChat(ProxiedPlayer player) {
        for (int i = 0; i < 250; i++) {
            player.sendMessage(Utils.format("&e"));
        }
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            user.sendLangMessage("general-commands.clearchat.usage");
            return;
        }
        String server = args[0].toLowerCase().contains("g") ? "ALL" : user.getServerName();

        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(Channels.CLEARCHAT, server);
        } else {
            clearChat(server);
        }
    }
}
