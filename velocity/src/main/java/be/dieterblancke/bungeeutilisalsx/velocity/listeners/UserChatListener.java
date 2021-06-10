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

package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;

import java.util.Optional;

public class UserChatListener
{

    @Subscribe
    public void onChat( final PlayerChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();

        if ( event.getMessage().startsWith( "/" ) )
        {
            final UserCommandEvent commandEvent = new UserCommandEvent( user, event.getMessage() );
            BuX.getApi().getEventLoader().launchEvent( commandEvent );

            if ( commandEvent.isCancelled() )
            {
                event.setResult( PlayerChatEvent.ChatResult.denied() );
                return;
            }
            event.setResult( PlayerChatEvent.ChatResult.message( commandEvent.getCommand() ) );
        }
        else
        {
            final UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
            BuX.getApi().getEventLoader().launchEvent( chatEvent );

            if ( chatEvent.isCancelled() )
            {
                event.setResult( PlayerChatEvent.ChatResult.denied() );
                return;
            }

            event.setResult( PlayerChatEvent.ChatResult.message( chatEvent.getMessage() ) );
        }
    }
}