package be.dieterblancke.bungeeutilisalsx.common.commands.party.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;

import java.util.List;
import java.util.Optional;

public class PartySetOwnerSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "party.setowner.usage" );
            return;
        }
        final Optional<Party> optionalParty = BuX.getInstance().getPartyManager().getCurrentPartyFor( user.getName() );

        if ( !optionalParty.isPresent() )
        {
            user.sendLangMessage( "party.not-in-party" );
            return;
        }
        final Party party = optionalParty.get();

        if ( !party.isOwner( user.getUuid() ) )
        {
            user.sendLangMessage( "party.setowner.not-allowed" );
            return;
        }

        final String newOwner = args.get( 0 );
        final Optional<UserStorage> userStorage = UserUtils.getUserStorage( newOwner, user::sendLangMessage );

        userStorage.ifPresent( target -> party.getPartyMembers()
                .stream()
                .filter( m -> m.getUuid().equals( target.getUuid() ) )
                .findFirst()
                .ifPresentOrElse( ( member ) ->
                {
                    BuX.getInstance().getPartyManager().setPartyOwner( party, party.getOwner(), false );
                    BuX.getInstance().getPartyManager().setPartyOwner( party, member, true );


                    user.sendLangMessage(
                            "party.setowner.changed",
                            "{new-owner}", target.getUserName()
                    );
                    BuX.getInstance().getPartyManager().languageBroadcastToParty(
                            party,
                            "party.setowner.broadcast",
                            "{old-owner}", user.getName(),
                            "{new-owner}", target.getUserName()
                    );
                }, () ->
                {
                    user.sendLangMessage(
                            "party.setowner.not-in-party",
                            "{user}", target.getUserName()
                    );
                } ) );
    }

    @Override
    public String getDescription()
    {
        return "Changes the owner of the party.";
    }

    @Override
    public String getUsage()
    {
        return "/party setowner (user)";
    }
}
