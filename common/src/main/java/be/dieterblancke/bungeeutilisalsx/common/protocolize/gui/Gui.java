package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;
import com.google.common.collect.Lists;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.data.inventory.InventoryType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class Gui
{

    private final UUID uuid = UUID.randomUUID();
    private final String title;
    private final int rows;
    private final PageableItemProvider pageableItemProvider;
    private final List<User> users;
    private long lastActivity = System.currentTimeMillis();
    private Inventory inventory;
    private boolean opened;
    private int page = 1;

    public static Builder builder()
    {
        return new Builder();
    }

    public void handleInventoryClick( final InventoryClick event )
    {
        lastActivity = System.currentTimeMillis();
        final Optional<GuiItem> item = pageableItemProvider.getItemAtSlot( event.slot() );

        BuX.getApi().getUser( event.player().uniqueId() )
                .ifPresent( user -> item.ifPresent( i -> i.onClick( this, user, event ) ) );
    }

    public void open()
    {
        lastActivity = System.currentTimeMillis();
        this.buildInventory();

        for ( User user : users )
        {
            BuX.getInstance().getProtocolizeManager().openInventory( user, inventory );
        }
        opened = true;
    }

    public void close( final boolean remove )
    {
        if ( remove )
        {
            BuX.getInstance().getProtocolizeManager().getGuiManager().remove( this );
        }

        for ( User user : users )
        {
            BuX.getInstance().getProtocolizeManager().closeInventory( user );
        }
        opened = false;

        if ( remove )
        {
            users.clear();
        }
    }

    public void close()
    {
        this.close( true );
    }

    public void buildInventory()
    {
        if ( inventory == null )
        {
            this.refill();
        }
    }

    public void setPage( final int page )
    {
        if ( page < 1 )
        {
            this.page = 1;
        }
        else
        {
            this.page = Math.min( page, pageableItemProvider.getPageAmount() );
        }
    }

    public void refill()
    {
        inventory = new Inventory( InventoryType.valueOf( "GENERIC_9X" + rows ) );
        inventory.title( BuX.getInstance().proxyOperations().getMessageComponent( Utils.format( Utils.replacePlaceHolders(
                title,
                "{page}", page,
                "{max}", pageableItemProvider.getPageAmount()
        ) ) ) );
        inventory.onClick( this::handleInventoryClick );

        final ItemPage itemPage = pageableItemProvider.getItemContents( page );
        itemPage.populateTo( inventory );
    }

    public void addPlayer( final User user )
    {
        if ( opened )
        {
            BuX.getInstance().getProtocolizeManager().openInventory( user, inventory );
        }
    }

    public void removePlayer( final User user )
    {
        if ( opened )
        {
            BuX.getInstance().getProtocolizeManager().closeInventory( user );
        }
    }

    public static final class Builder
    {
        private String title;
        private int rows = 6;
        private List<User> users = new ArrayList<>();
        private PageableItemProvider pageableItemProvider;

        private Builder()
        {
            // keep constructor private
        }

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder rows( final int rows )
        {
            this.rows = rows;
            return this;
        }

        public Builder users( final Iterable<User> users )
        {
            this.users = Lists.newArrayList( users );
            return this;
        }

        public Builder users( final User... users )
        {
            this.users = Lists.newArrayList( users );
            return this;
        }

        public Builder itemProvider( final PageableItemProvider pageableItemProvider )
        {
            this.pageableItemProvider = pageableItemProvider;
            return this;
        }

        public Gui build()
        {
            final Gui gui = new Gui( title, rows, pageableItemProvider, users );
            BuX.getInstance().getProtocolizeManager().getGuiManager().add( gui );
            return gui;
        }
    }
}
