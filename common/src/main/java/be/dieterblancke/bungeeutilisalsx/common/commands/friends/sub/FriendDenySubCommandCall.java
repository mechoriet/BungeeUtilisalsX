package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.FriendsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.Optional;

public class FriendDenySubCommandCall implements CommandCall
{

    public static void removeFriendRequest( final UserStorage storage, final User user, final User target )
    {
        BuX.getApi().getStorageManager().getDao().getFriendsDao().removeFriendRequest( user.getUuid(), storage.getUuid() );
        user.sendLangMessage( "friends.deny.denied", "{user}", storage.getUserName() );

        if ( target != null )
        {
            target.sendLangMessage( "friends.deny.request-denied", "{name}", user.getName() );
        }
        else if ( BuX.getApi().getPlayerUtils().isOnline( storage.getUserName() ) )
        {
            BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                    storage.getUserName(),
                    "friends.deny.request-denied",
                    "{name}", user.getName()
            ) );
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.deny.usage" );
            return;
        }
        final String name = args.get( 0 );
        final FriendsDao dao = BuX.getApi().getStorageManager().getDao().getFriendsDao();
        final Optional<User> optionalTarget = BuX.getApi().getUser( name );
        final UserStorage storage = Utils.getUserStorageIfUserExists( optionalTarget.orElse( null ), name );

        if ( storage == null )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        if ( !dao.hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ).join() )
        {
            user.sendLangMessage( "friends.deny.no-request", "{user}", name );
            return;
        }

        removeFriendRequest( storage, user, optionalTarget.orElse( null ) );
    }

    @Override
    public String getDescription()
    {
        return "Denies an incoming friend request.";
    }

    @Override
    public String getUsage()
    {
        return "/friend deny (user)";
    }
}
