package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import net.kyori.adventure.text.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBuXApi
{

    /**
     * Gets the console user instance
     *
     * @return the console user.
     */
    User getConsoleUser();

    /**
     * @return The language chat of BungeeUtilisals.
     */
    ILanguageManager getLanguageManager();

    /**
     * @return The BungeeUtilisalsX EventLoader allowing you to register EventHandlers.
     */
    IEventLoader getEventLoader();

    /**
     * @param name The username you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser( String name );

    /**
     * @param uuid The user uuid you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser( UUID uuid );

    /**
     * @return A list containing all online Users.
     */
    List<User> getUsers();

    /**
     * Adds a user to memory
     *
     * @param user the user to add
     */
    void addUser( User user );

    /**
     * Removes a user to memory
     *
     * @param user the user to remove
     */
    void removeUser( User user );

    /**
     * @param permission The permission the users must have.
     * @return A list containing all online users WITH the given permission.
     */
    List<User> getUsers( String permission );

    /**
     * @return A new ProxyConnection instance.
     * @throws SQLException When an error occurs trying to setup the connection.
     */
    Connection getConnection() throws SQLException;

    /**
     * @return The BungeeUtilisals punishment API.
     */
    IPunishmentHelper getPunishmentExecutor();

    /**
     * @return An either the active HubBalancer or null in case it's disabled.
     */
    IHubBalancer getHubBalancer();

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param message The message to be broadcasted.
     */
    void broadcast( String message );

    /**
     * Broadcasts a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param message    The message to be broadcasted.
     * @param permission The permission the user must have to receive the message.
     */
    void broadcast( String message, String permission );

    /**
     * Broadcasts a message with a given prefix to the people with the given permission.
     *
     * @param prefix  The prefix you want.
     * @param message The message to be broadcasted.
     */
    void announce( String prefix, String message );

    /**
     * Broadcasts a message with a given prefix to the people with the given permission.
     *
     * @param prefix     The prefix you want.
     * @param message    The message to be broadcasted.
     * @param permission The permission the user must have to receive the message.
     */
    void announce( String prefix, String message, String permission );

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param placeholders PlaceHolders + their replacements
     */
    void langBroadcast( String message, Object... placeholders );

    /**
     * Broadcasts a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param permission   The permission the user must have to receive the message.
     * @param placeholders PlaceHolders + their replacements
     */
    void langPermissionBroadcast( String message, String permission, Object... placeholders );

    /**
     * @return a list of all announcers.
     */
    Collection<Announcer> getAnnouncers();

    /**
     * @return an IPlayerUtils instance (BungeePlayerUtils or RedisPlayerUtils)
     */
    IPlayerUtils getPlayerUtils();

    /**
     * @return the storage chat used.
     */
    AbstractStorageManager getStorageManager();

    /**
     * @return a new BossBar instance.
     */
    IBossBar createBossBar();

    /**
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar
     * @return a new BossBar instance.
     */
    IBossBar createBossBar( BarColor color, BarStyle style, float progress, Component message );

    /**
     * @param uuid     UUID for the BossBar, should be unique!
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar
     * @return a new BossBar instance.
     */
    IBossBar createBossBar( UUID uuid, BarColor color, BarStyle style, float progress, Component message );

    /**
     * @return a list of online staff members
     */
    List<StaffUser> getStaffMembers();
}
