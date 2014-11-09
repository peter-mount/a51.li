/*
 * Copyright 2014 Peter T Mount.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onl.area51.a51li.memo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import onl.area51.a51li.AbstractServlet;
import onl.area51.a51li.CacheControl;
import onl.area51.a51li.link.LinkManager;
import onl.area51.a51li.URLCodec;
import onl.area51.a51li.link.Url;

/**
 *
 * @author Peter T Mount
 */
@WebServlet( name = "viewmemo", urlPatterns = "/viewmemo/" )
public class ViewMemo
        extends AbstractServlet
{

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp, Url url )
            throws ServletException,
                   IOException
    {
        Memo memo = LinkManager.INSTANCE.getMemo( url.getId() );

        // If not removed from the db but it's expiry time has passed then treat as if it's no longer present
        if( memo != null && memo.getExpires() != null )
        {
            LocalDateTime expires = memo.getExpires().
                    toLocalDateTime();
            if( expires.isBefore( LocalDateTime.now() ) )
            {
                memo = null;
            }
        }

        if( memo == null )
        {
            // Show the removed page
            memo = Memo.REMOVED;

            // Change the status to gone
            resp.setStatus( HttpServletResponse.SC_GONE );
        }
        else
        {
            // Log the visit
            LinkManager.INSTANCE.recordVisit( url, req );

            // Cache it for a day
            CacheControl.DAY.addHeaders( resp );

            // Record when it was last modified, here the creation date
            resp.addDateHeader( "last-modified", url.getTimestamp().
                                getTime() );
        }

        // So the page can link back to it
        url.setUrl( "http://a51.li/" + URLCodec.getUID( url.getId() ) );

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();

        // The year, used in page footers
        req.setAttribute( "year", date.get( ChronoField.YEAR ) );

        // The page generated time, yyyy-mm-dd hh:mm:ss
        req.setAttribute( "pageGenerated",
                          now.truncatedTo( ChronoUnit.SECONDS ).
                          toString().
                          replace( 'T', ' ' ) );

        req.setAttribute( "memo", memo );
        req.setAttribute( "user", LinkManager.INSTANCE.getUser( url.getUserId() ) );
        req.setAttribute( "count", LinkManager.INSTANCE.getVisitCount( url.getId() ) );

        req.getRequestDispatcher( "/memo.jsp" ).
                forward( req, resp );
    }

}
