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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.sql.DataSource;
import onl.area51.a51li.URLCodec;
import uk.trainwatch.util.JsonUtils;
import uk.trainwatch.util.sql.SQL;
import uk.trainwatch.util.sql.SQLConsumer;

/**
 * SQLFunction to take a JsonObject and generate a Memo adding a uid field to the object.
 * <p>
 * @author Peter T Mount
 */
public class MemoGenerator
        implements SQLConsumer<JsonObject>
{

    protected static final Logger LOG = Logger.getLogger( MemoGenerator.class.getName() );

    private final DataSource dataSource;
    private final Consumer<? super JsonStructure> twitterConsumer;

    public MemoGenerator( DataSource dataSource, Consumer<? super JsonStructure> twitterConsumer )
    {
        this.dataSource = dataSource;
        this.twitterConsumer = twitterConsumer;
    }

    @Override
    public void accept( JsonObject t )
            throws SQLException
    {
        LOG.log( Level.FINE, "Received {0}", t );

        if( t == null )
        {
            return;
        }

        String userName = JsonUtils.getString( t, "user" );
        String hash = JsonUtils.getString( t, "hash" );
        String title = JsonUtils.getString( t, "title", "Untitled" );
        String text = JsonUtils.getString( t, "text" );

        if( userName == null || hash == null || text == null || text.isEmpty() )
        {
            return;
        }

        MemoType memoType = MemoType.valueOf( JsonUtils.getString( t, "type", "TEXT" ).
                toUpperCase() );

        LocalDateTime expires = JsonUtils.getLocalDateTime( t, "expires" );

        Long uid;
        try( Connection con = dataSource.getConnection() )
        {
            try( PreparedStatement s = con.prepareStatement(
                    "SELECT creatememo(?,?,?,?,?,?)"
            ) )
            {
                s.setString( 1, userName );
                s.setString( 2, hash );
                s.setString( 3, title );
                s.setString( 4, text );
                s.setInt( 5, memoType.getDbType() );

                if( expires == null )
                {
                    s.setNull( 6, Types.TIMESTAMP );
                }
                else
                {
                    s.setTimestamp( 6, Timestamp.from( expires.toInstant( ZoneOffset.UTC ) ) );
                }

                uid = SQL.stream( s, SQL.LONG_LOOKUP ).
                        findFirst().
                        orElse( null );
            }
        }

        LOG.log( Level.FINE, "Created uid {0}", uid );

        if( uid == null )
        {
            return;
        }

        String shortUrl = "http://a51.li/" + URLCodec.getUID( uid );

        LOG.log( Level.INFO, "Short Url {0}", shortUrl );

        // Also require tweeting?
        if( t.containsKey( "tweet" ) && t.containsKey( "tweetAs" ) )
        {
            // Form the tweet
            String tweet = JsonUtils.getString( t, "tweet", title );
            if( "Untitled".equals( tweet ) )
            {
                tweet = text;
            }

            // Twitter uses 21 chars for url's (as of now), so remove 30 from 140 limit to allow for room
            if( tweet.length() > 110 )
            {
                tweet = tweet.substring( 0, 110 ) + "… ";
            }

            tweet += " " + shortUrl;

            // Send the tweet
            twitterConsumer.accept( Json.createObjectBuilder().
                    add( "user", userName ).
                    add( "hash", hash ).
                    add( "tweetAs", t.getString( "tweetAs" ) ).
                    add( "tweet", tweet ).
                    build() );
        }
    }

}
