package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PrivateMessageType;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserFriendPrivateMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;

import java.util.List;

public class FriendReplySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.reply.usage" );
            return;
        }
        if ( !user.getStorage().hasData( UserStorageKey.FRIEND_MSG_LAST_USER ) )
        {
            user.sendLangMessage( "friends.reply.no-target" );
            return;
        }

        final String name = user.getStorage().getData( UserStorageKey.FRIEND_MSG_LAST_USER );
        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.reply.not-friend", "{user}", name );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final String message = String.join( " ", args );

            BuX.getInstance().getJobManager().executeJob( new UserFriendPrivateMessageJob(
                    user.getUuid(),
                    user.getName(),
                    name,
                    message,
                    PrivateMessageType.MSG
            ) );
        }
        else
        {
            user.sendLangMessage( "offline" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Allows you to reply to a friend that messaged you earlier.";
    }

    @Override
    public String getUsage()
    {
        return "/friend reply (message)";
    }
}
