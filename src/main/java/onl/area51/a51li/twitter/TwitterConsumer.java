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
package onl.area51.a51li.twitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import onl.area51.a51li.link.LinkManager;
import onl.area51.a51li.sql.User;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.trainwatch.util.JsonUtils;

/**
 *
 * @author Peter T Mount
 */
public class TwitterConsumer
        implements Consumer<JsonObject>
{

    private static final Logger LOG = Logger.getLogger( TwitterConsumer.class.getName() );

    /**
     * Cache of twitter api's per user
     */
    private final Map<String, Twitter> twits = new ConcurrentHashMap<>();

    @Override
    public void accept( JsonObject o )
    {
        LOG.log( Level.FINE, "Received {0}", o );

        if( o == null )
        {
            return;
        }

        String userName = JsonUtils.getString( o, "user" );
        String hash = JsonUtils.getString( o, "hash" );
        String tweetAs = JsonUtils.getString( o, "tweetAs", userName );
        String tweet = JsonUtils.getString( o, "tweet" );

        if( userName == null || hash == null || tweetAs == null || tweet == null )
        {
            return;
        }

        // Allow for comma separated twitter names
        if( tweetAs.contains( "," ) )
        {
            for( String as : tweetAs.split( "," ) )
            {
                tweet( userName, hash, as, tweet );
            }
        }
        else
        {
            tweet( userName, hash, tweetAs, tweet );
        }
    }

    private void tweet( String userName, String hash, String tweetAs, String tweet )
    {

        User user = LinkManager.INSTANCE.getUser( userName, hash );
        if( user == null )
        {
            return;
        }

        Twitter twitter = twits.computeIfAbsent( tweetAs, h -> TwitterManager.INSTANCE.getTwitter( user, tweetAs ) );
        if( twitter == null )
        {
            return;
        }

        // Now rate limit ourselves here as we have a twitter API available to us
        TwitterManager.INSTANCE.rateLimit();

        // Try to tweet
        try
        {
            LOG.log( Level.INFO, () -> "Tweeting @" + tweetAs + ": " + tweet );
            Status s = twitter.updateStatus( tweet );
            LOG.log( Level.INFO, () -> "Tweeted @" + tweetAs + ": " + s.getText() );
        }
        catch( TwitterException ex )
        {
            LOG.log( Level.SEVERE, ex, () -> "Failed to tweet " + tweetAs + ": " + tweet );
        }

    }

}
