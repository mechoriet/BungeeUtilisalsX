package be.dieterblancke.bungeeutilisalsx.common.api.utils.text;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PageUtils
{

    public static <T> List<T> getPageFromList( final int page, final List<T> list, final int pageSize ) throws PageNotFoundException
    {
        final int maxPages = (int) Math.ceil( list.size() / (double) pageSize );

        if ( page > maxPages )
        {
            throw new PageNotFoundException( page, maxPages );
        }

        final int begin = ( ( page - 1 ) * 10 );
        int end = begin + 10;

        if ( end > list.size() )
        {
            end = list.size();
        }

        return list.subList( begin, end );
    }

    public static <T> void sendPagedList( final User user,
                                          final List<T> list,
                                          final String pageStr,
                                          final int pageSize,
                                          final PageResponseHandler<T> responseHandler )
    {
        if ( list.isEmpty() )
        {
            responseHandler.getEmptyListMessage().sendMessage( user );
            return;
        }

        final int pages = (int) Math.ceil( (double) list.size() / pageSize );
        final int page;
        if ( MathUtils.isInteger( pageStr ) )
        {
            page = Math.min( Integer.parseInt( pageStr ), pages );
        }
        else
        {
            page = 1;
        }

        try
        {
            final List<T> listPage = PageUtils.getPageFromList( page, list, pageSize );
            final int previous = page > 1 ? page - 1 : 1;
            final int next = Math.min( page + 1, pages );

            responseHandler.getHeaderMessage().sendMessage(
                    user,
                    "{page}", page,
                    "{maxPages}", pages,
                    "{previousPage}", previous,
                    "{nextPage}", next
            );

            for ( T item : listPage )
            {
                responseHandler.getItemMessage( item ).sendMessage(
                        user,
                        "{page}", page,
                        "{maxPages}", pages,
                        "{previousPage}", previous,
                        "{nextPage}", next
                );
            }

            responseHandler.getFooterMessage().sendMessage(
                    user,
                    "{page}", page,
                    "{maxPages}", pages,
                    "{previousPage}", previous,
                    "{nextPage}", next
            );
        }
        catch ( PageUtils.PageNotFoundException e )
        {
            responseHandler.getInvalidPageMessage().sendMessage(
                    user,
                    "{page}", e.getPage(),
                    "{maxpages}", e.getMaxPages()
            );
        }
    }

    public interface PageResponseHandler<T>
    {

        PageMessageInfo getEmptyListMessage();

        PageMessageInfo getHeaderMessage();

        PageMessageInfo getItemMessage( T type );

        PageMessageInfo getFooterMessage();

        PageMessageInfo getInvalidPageMessage();

    }

    public static class PageMessageInfo
    {

        private final String path;
        private final Object[] parameters;

        public PageMessageInfo( final String path, final Object... parameters )
        {
            this.path = path;
            this.parameters = parameters;
        }

        public void sendMessage( User user, Object... parameters )
        {
            user.sendLangMessage(
                    path,
                    Stream.concat( Arrays.stream( this.parameters ), Arrays.stream( parameters ) ).toArray()
            );
        }
    }

    public static class PageNotFoundException extends Exception
    {

        @Getter
        private final int page;
        @Getter
        private final int maxPages;

        public PageNotFoundException( int page, int maxPages )
        {
            this.page = page;
            this.maxPages = maxPages;
        }
    }
}
