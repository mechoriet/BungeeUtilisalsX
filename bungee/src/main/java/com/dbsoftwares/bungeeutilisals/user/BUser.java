/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.user;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.Location;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Setter
public class BUser implements User {

    private ProxiedPlayer parent;

    private String name;
    private UUID uuid;
    private String IP;
    private Boolean socialspy;
    private UserCooldowns cooldowns;
    private UserStorage storage;
    private PunishmentInfo mute;
    private Location location;

    @Getter
    private List<FriendData> friends;

    @Getter
    private boolean inStaffChat;

    @Override
    public void load(ProxiedPlayer parent) {
        Dao dao = BungeeUtilisals.getInstance().getDatabaseManagement().getDao();

        this.parent = parent;
        this.name = parent.getName();
        this.uuid = parent.getUniqueId();
        this.IP = Utils.getIP(parent.getAddress());
        this.storage = new UserStorage();
        this.cooldowns = new UserCooldowns();

        if (dao.getUserDao().exists(uuid)) {
            storage = dao.getUserDao().getUserData(uuid);

            if (!storage.getUserName().equalsIgnoreCase(name)) {
                dao.getUserDao().setName(uuid, name);
                storage.setUserName(name);
            }

            storage.setLanguage(BUCore.getApi().getLanguageManager().getLanguageIntegration().getLanguage(uuid));
        } else {
            final Language defLanguage = BUCore.getApi().getLanguageManager().getDefaultLanguage();
            final Date date = new Date(System.currentTimeMillis());

            dao.getUserDao().createUser(
                    uuid,
                    name,
                    IP,
                    defLanguage
            );

            storage = new UserStorage(uuid, name, IP, defLanguage, date, date, Maps.newHashMap());
        }

        if (!storage.getUserName().equals(name)) { // Stored name != user current name | Name changed?
            storage.setUserName(name);
            dao.getUserDao().setName(uuid, name);
        }

        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean("enabled")) {
            if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.MUTE, uuid, null, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.MUTE, uuid, null);
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.TEMPMUTE, uuid, null, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.TEMPMUTE, uuid, null);
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPMUTE, null, IP, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.IPMUTE, null, IP);
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPTEMPMUTE, null, IP, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.IPTEMPMUTE, null, IP);
            }
        }

        if (FileLocation.FRIENDS_CONFIG.getConfiguration().getBoolean("enabled")) {
            friends = dao.getFriendsDao().getFriends(uuid);
        }

        UserLoadEvent userLoadEvent = new UserLoadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userLoadEvent);
    }

    @Override
    public void unload() {
        save();
        cooldowns.remove();

        UserUnloadEvent event = new UserUnloadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEventAsync(event);

        parent = null;
        storage.getData().clear();
    }

    @Override
    public void save() {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDao().getUserDao().updateUser(uuid, getName(), IP, getLanguage(), new Date(System.currentTimeMillis()));
    }

    @Override
    public UserStorage getStorage() {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public String getIP() {
        return IP;
    }

    @Override
    public Language getLanguage() {
        return storage.getLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        storage.setLanguage(language);
    }

    @Override
    public CommandSender sender() {
        return getParent();
    }

    @Override
    public void sendRawMessage(String message) {
        sendMessage(new TextComponent(PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendRawColorMessage(String message) {
        sendMessage(Utils.format(this, message));
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(getLanguageConfig().getString("prefix"), PlaceHolderAPI.formatMessage(this, message));
    }

    @Override
    public void sendLangMessage(String path) {
        sendLangMessage(true, path);
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        sendLangMessage(true, path, placeholders);
    }

    @Override
    public void sendLangMessage(boolean prefix, String path) {
        final String message = buildLangMessage(path);
        if (prefix) {
            sendMessage(message);
        } else {
            sendRawColorMessage(message);
        }
    }

    @Override
    public void sendLangMessage(boolean prefix, String path, Object... placeholders) {
        final String message = buildLangMessage(path, placeholders);
        if (prefix) {
            sendMessage(message);
        } else {
            sendRawColorMessage(message);
        }
    }

    @Override
    public void sendMessage(String prefix, String message) {
        sendMessage(Utils.format(prefix + PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendMessage(BaseComponent component) {
        getParent().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        getParent().sendMessage(components);
    }

    @Override
    public void kick(String reason) {
        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> getParent().disconnect(Utils.format(reason)));
    }

    @Override
    public void langKick(String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            StringBuilder builder = new StringBuilder();

            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                builder.append(message).append("\n");
            }
            kick(builder.toString());
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            kick(message);
        }
    }

    @Override
    public void forceKick(String reason) {
        getParent().disconnect(Utils.format(reason));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        this.socialspy = socialspy;
    }

    @Override
    public Boolean isSocialSpy() {
        return socialspy;
    }

    @Override
    public ProxiedPlayer getParent() {
        return parent;
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getLanguageConfiguration(BungeeUtilisals.getInstance().getDescription().getName(), this);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public String getServerName() {
        return getParent().getServer().getInfo().getName();
    }

    @Override
    public boolean isMuted() {
        return mute != null;
    }

    @Override
    public PunishmentInfo getMuteInfo() {
        return mute;
    }

    @Override
    public Version getVersion() {
        try {
            return Version.getVersion(parent.getPendingConnection().getVersion());
        } catch (Exception e) {
            return Version.MINECRAFT_1_8;
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String buildLangMessage(final String path, final Object... placeholders) {
        final StringBuilder builder = new StringBuilder();

        if (getLanguageConfig().isList(path)) {
            final List<String> messages = getLanguageConfig().getStringList(path);

            for (int i = 0; i < messages.size(); i++) {
                final String message = replacePlaceHolders(messages.get(i), placeholders);
                builder.append(message);

                if (i < messages.size() - 1) {
                    builder.append("\n");
                }
            }
        } else {
            final String message = replacePlaceHolders(getLanguageConfig().getString(path), placeholders);

            builder.append(message);
        }
        return builder.toString();
    }

    private String replacePlaceHolders(String message, Object... placeholders) {
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
        }
        message = PlaceHolderAPI.formatMessage(this, message);
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BUser user = (BUser) o;
        return user.name.equalsIgnoreCase(name) && user.uuid.equals(uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }

    @Override
    public void sendPacket(DefinedPacket packet) {
        if (parent == null || !parent.isConnected()) {
            return;
        }
        parent.unsafe().sendPacket(packet);
    }
}