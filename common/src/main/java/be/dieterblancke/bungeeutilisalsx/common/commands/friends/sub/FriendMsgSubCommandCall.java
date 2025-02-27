package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PrivateMessageType;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserFriendPrivateMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;

import java.util.List;

public class FriendMsgSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "friends.msg.usage" );
            return;
        }
        final String name = args.get( 0 );

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.msg.not-friend", "{user}", name );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final String message = String.join( " ", args.subList( 1, args.size() ) );

            user.getStorage().setData( UserStorageKey.FRIEND_MSG_LAST_USER, name );

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
        return "Allows you to privately message a friend.";
    }

    @Override
    public String getUsage()
    {
        return "/friend msg (user)";
    }
}
