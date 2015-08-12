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
package onl.area51.a51li.link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import onl.area51.a51li.memo.Memo;
import onl.area51.a51li.sql.User;
import onl.area51.a51li.sql.VisitCount;
import uk.trainwatch.util.sql.SQL;

/**
 *
 * @author Peter T Mount
 */
@ApplicationScoped
@CacheDefaults(cacheName = "A51LI")
public class LinkManager
{

    protected static final Logger LOG = Logger.getLogger( LinkManager.class.getName() );

    @Resource(name = "jdbc/links")
    private DataSource dataSource;

    @Inject
    private UrlCache urlCache;

    @Inject
    private MemoCache memoCache;

    @Inject
    private UserCache userCache;

    public void recordVisit( Url url, HttpServletRequest req )
    {
        // Figure out the connection address
        String ip = null;

        String xf = req.getHeader( "X-Forwarded-For" );
        if( xf != null && !xf.isEmpty() ) {
            for( String s: xf.split( "," ) ) {
                s = s.trim();
                if( ip == null && !( // IPV4 private networks
                                    s.startsWith( "10." )
                                    || s.startsWith( "172.16." )
                                    || s.startsWith( "172.17." )
                                    || s.startsWith( "172.18." )
                                    || s.startsWith( "172.19." )
                                    // 172.20-172.29
                                    || s.startsWith( "172.2" )
                                    || s.startsWith( "172.30." )
                                    || s.startsWith( "172.31." )
                                    || s.startsWith( "192.168." )
                                    // IANA Carrier grade nat since April 2012 so not on private or public internet
                                    || s.startsWith( "100.64." )
                                    // IPV6 private networks
                                    || s.startsWith( "fe80:" )
                                    || // Localhost
                                    s.startsWith( "127." )
                                    || s.startsWith( "0:0" )) ) {
                    ip = s;
                }
            }
        }

//        visitRecorder.accept( Json.createObjectBuilder().
//                add( "uid", url.getId() ).
//                // On production if ip is still nulll then this will always be ::1 (localhost) as it's behind apache
//                add( "remote", Objects.toString( ip, req.getRemoteHost() ) ).
//                add( "useragent", Objects.toString( req.getHeader( "User-Agent" ), "" ) ).
//                build() );
    }

    public Url getUrl( long uid )
    {
        return urlCache.getUrl( uid );
    }

    public Memo getMemo( @CacheKey long uid )
    {
        return memoCache.getMemo( uid );
    }

    public User getUser( long uid )
    {
        return userCache.getUser( uid );
    }

    public User getUser( String name, String hash )
    {
        User user = userCache.getUser( name );
        return user != null && user.getUserkey().equals( hash ) ? user : null;
    }

    public VisitCount getVisitCount( long uid )
    {
        try( Connection con = dataSource.getConnection() ) {
            VisitCount count = new VisitCount();
            count.setTotal( count( con, uid, "" ) );
            count.setLastWeek( count( con, uid, " AND tm BETWEEN (now() - '1 week'::INTERVAL) AND now()" ) );
            count.setLastMonth( count( con, uid, " AND tm BETWEEN (now() - '1 month'::INTERVAL) AND now()" ) );
            return count;
        }
        catch( SQLException ex ) {
            LOG.log( Level.SEVERE, null, ex );
            return null;
        }
    }

    private int count( Connection con, long id, String where )
            throws SQLException
    {
        try( PreparedStatement s = con.prepareStatement( "SELECT count(id) FROM visit WHERE url=? " + where ) ) {
            s.setLong( 1, id );
            return SQL.stream( s, SQL.INT_LOOKUP ).
                    findFirst().
                    orElse( 0 );
        }
    }
}
