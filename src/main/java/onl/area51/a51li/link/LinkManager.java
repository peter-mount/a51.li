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
import java.sql.ResultSet;
import java.sql.SQLException;
import onl.area51.a51li.sql.VisitConsumer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import onl.area51.a51li.memo.Memo;
import onl.area51.a51li.memo.MemoGenerator;
import onl.area51.a51li.rabbit.ResponseConsumer;
import onl.area51.a51li.sql.User;
import onl.area51.a51li.sql.VisitCount;
import onl.area51.a51li.twitter.TwitterConsumer;
import uk.trainwatch.rabbitmq.RabbitConnection;
import uk.trainwatch.rabbitmq.RabbitMQ;
import uk.trainwatch.util.Consumers;
import uk.trainwatch.util.JsonUtils;
import uk.trainwatch.util.counter.RateMonitor;
import uk.trainwatch.util.sql.SQL;
import uk.trainwatch.util.sql.SQLConsumer;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public enum LinkManager
{

    INSTANCE;

    protected static final Logger LOG = Logger.getLogger( LinkManager.class.getName() );

    private DataSource dataSource;

    private RabbitConnection rabbitConnection;

    private Consumer<? super JsonStructure> visitRecorder;

    public void contextInitialized( DataSource dataSource, String rabbitUser, String rabbitPassword, String rabbitHost )
    {
        rabbitConnection = new RabbitConnection( rabbitUser, rabbitPassword, rabbitHost );

        // Twitter bot - handle outbound tweets
        Consumer<JsonObject> twitterMonitor = RateMonitor.log( LOG, "record twitter.tweet" );
        Consumer<JsonObject> twitterConsumer = new TwitterConsumer();
        RabbitMQ.queueDurableStream( rabbitConnection, "twitter.tweet", "twitter.tweet",
                                     s -> s.map( RabbitMQ.toString.andThen( JsonUtils.parseJsonObject ) ).
                                     filter( Objects::nonNull ).
                                     peek( twitterMonitor ).
                                     forEach( Consumers.guard( LOG, twitterConsumer ) )
        );
        Consumer<? super JsonStructure> twitterTweetConsumer = RabbitMQ.
                jsonConsumer( rabbitConnection, "twitter.tweet" );

        // Background recording of visits
        Consumer<JsonObject> visitMonitor = RateMonitor.log( LOG, "record a51.li.visit" );
        Consumer<JsonObject> visitConsumer = new VisitConsumer( dataSource );
        RabbitMQ.queueDurableStream( rabbitConnection, "a51.li.visit", "a51.li.visit",
                                     s -> s.map( RabbitMQ.toString.andThen( JsonUtils.parseJsonObject ) ).
                                     filter( Objects::nonNull ).
                                     peek( visitMonitor ).
                                     forEach( Consumers.guard( LOG, visitConsumer ) )
        );

        // Create our connection to the above queue
        visitRecorder = RabbitMQ.jsonConsumer( rabbitConnection, "a51.li.visit" );

        // Consumer that receives link create requests
        Consumer<JsonObject> linkMonitor = RateMonitor.log( LOG, "record a51.li.link" );
        Consumer<JsonObject> linkConsumer = Consumers.consume(
                SQLFunction.guard( new LinkGenerator( dataSource ) ),
                new ResponseConsumer()
        );
        RabbitMQ.queueDurableStream( rabbitConnection, "a51.li.link", "a51.li.link",
                                     s -> s.map( RabbitMQ.toString.andThen( JsonUtils.parseJsonObject ) ).
                                     filter( Objects::nonNull ).
                                     peek( linkMonitor ).
                                     forEach( Consumers.guard( LOG, linkConsumer ) )
        );

        // Consumer that receives memo create requests.
        // If json contains tweet details then we also tweet
        Consumer<JsonObject> memoMonitor = RateMonitor.log( LOG, "record a51.li.memo" );
        Consumer<JsonObject> memoConsumer = SQLConsumer.guard( new MemoGenerator( dataSource, twitterTweetConsumer ) );
        RabbitMQ.queueDurableStream( rabbitConnection, "a51.li.memo", "a51.li.memo",
                                     s -> s.map( RabbitMQ.toString.andThen( JsonUtils.parseJsonObject ) ).
                                     filter( Objects::nonNull ).
                                     peek( memoMonitor ).
                                     forEach( Consumers.guard( LOG, memoConsumer ) )
        );
    }

    public void contextDestroyed()
    {
        if( rabbitConnection != null )
        {
            rabbitConnection.close();
            rabbitConnection = null;
        }
    }

    public void recordVisit( Url url, HttpServletRequest req )
    {
        // Figure out the connection address
        String ip = null;

        String xf = req.getHeader( "X-Forwarded-For" );
        if( xf != null && !xf.isEmpty() )
        {
            for( String s : xf.split( "," ) )
            {
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
                                    || s.startsWith( "0:0" )) )
                {
                    ip = s;
                }
            }
        }

        visitRecorder.accept( Json.createObjectBuilder().
                add( "uid", url.getId() ).
                // On production if ip is still nulll then this will always be ::1 (localhost) as it's behind apache
                add( "remote", Objects.toString( ip, req.getRemoteHost() ) ).
                add( "useragent", Objects.toString( req.getHeader( "User-Agent" ), "" ) ).
                build() );
    }

    private <T> T get( long id, String table, SQLFunction<ResultSet, T> factory )
    {
        try( Connection con = dataSource.getConnection() )
        {
            try( PreparedStatement s = con.prepareStatement( "SELECT * FROM " + table + " WHERE id=?" ) )
            {
                s.setLong( 1, id );
                return SQL.stream( s, factory ).
                        findFirst().
                        orElse( null );
            }
        }
        catch( SQLException ex )
        {
            LOG.log( Level.SEVERE, null, ex );
            return null;
        }
    }

    public Url getUrl( long uid )
    {
        return get( Math.abs( uid ), "url", Url.fromSQL );
    }

    public Memo getMemo( long uid )
    {
        return get( Math.abs( uid ), "memo", Memo.fromSQL );
    }

    public User getUser( long uid )
    {
        return get( Math.abs( uid ), "users", User.fromSQL );
    }

    public User getUser( String name, String hash )
    {
        try( Connection con = dataSource.getConnection() )
        {
            try( PreparedStatement s = con.prepareStatement( "SELECT * FROM users WHERE username=? AND userkey=?" ) )
            {
                s.setString( 1, name );
                s.setString( 2, hash );
                return SQL.stream( s, User.fromSQL ).
                        findFirst().
                        orElse( null );
            }
        }
        catch( SQLException ex )
        {
            LOG.log( Level.SEVERE, null, ex );
            return null;
        }
    }

    public VisitCount getVisitCount( long uid )
    {
        try( Connection con = dataSource.getConnection() )
        {
            VisitCount count = new VisitCount();
            count.setTotal( count( con, uid, "" ) );
            count.setLastWeek( count( con, uid, " AND tm BETWEEN (now() - '1 week'::INTERVAL) AND now()" ) );
            count.setLastMonth( count( con, uid, " AND tm BETWEEN (now() - '1 month'::INTERVAL) AND now()" ) );
            return count;
        }
        catch( SQLException ex )
        {
            LOG.log( Level.SEVERE, null, ex );
            return null;
        }
    }

    private int count( Connection con, long id, String where )
            throws SQLException
    {
        try( PreparedStatement s = con.prepareStatement( "SELECT count(id) FROM visit WHERE url=? " + where ) )
        {
            s.setLong( 1, id );
            return SQL.stream( s, SQL.INT_LOOKUP ).
                    findFirst().
                    orElse( 0 );
        }
    }
}
