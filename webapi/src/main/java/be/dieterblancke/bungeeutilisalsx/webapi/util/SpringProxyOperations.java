package be.dieterblancke.bungeeutilisalsx.webapi.util;

import be.dieterblancke.bungeeutilisalsx.common.ProxyOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public class SpringProxyOperations implements ProxyOperationsApi
{
    @Override
    public void registerCommand( Command command )
    {
        // do nothing
    }

    @Override
    public void unregisterCommand( Command command )
    {
        // do nothing
    }

    @Override
    public List<IProxyServer> getServers()
    {
        return null;
    }

    @Override
    public IProxyServer getServerInfo( String serverName )
    {
        return null;
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return null;
    }

    @Override
    public Optional<PluginInfo> getPlugin( String pluginName )
    {
        return Optional.empty();
    }

    @Override
    public long getMaxPlayers()
    {
        return 0;
    }

    @Override
    public Object getMessageComponent( Component components )
    {
        return null;
    }
}
