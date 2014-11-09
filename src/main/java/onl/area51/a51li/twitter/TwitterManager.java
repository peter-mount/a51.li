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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.sql.DataSource;
import onl.area51.a51li.sql.User;
import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import twitter4j.Twitter;
import uk.trainwatch.util.sql.SQL;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public enum TwitterManager
{

    INSTANCE;
    
    /**
     * Maximum number of tweets per window to allow
     */
    private static final int MAX_RATE = 10;

    /**
     * Token bucket used to ensure we only make this number of tweets.
     * <p>
     * Now we have this as global - rather than per user so should be lower but could still hit limits on individual
     * users
     * <p>
     * https://dev.twitter.com/rest/public/rate-limiting
     */
    private final TokenBucket bucket = TokenBuckets.newFixedIntervalRefill( MAX_RATE, MAX_RATE, 15, TimeUnit.MINUTES );

    private DataSource dataSource;

    private final Function<Key, TwitterAccount> factory = SQLFunction.guard( key ->
    {
        try( Connection con = dataSource.getConnection() )
        {

            try( PreparedStatement s = SQL.prepare( con, TwitterAccount.SELECT_SQL, key.getId(), key.getName() ) )
            {
                return SQL.stream( s, TwitterAccount.fromSQL ).
                        findFirst().
                        orElse( null );
            }
        }
    } );

    private final Map<Key, TwitterAccount> accounts = new ConcurrentHashMap<>();

    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    public void rateLimit()
    {
        bucket.tryConsume();
    }
    
    public Twitter getTwitter( User user, String name )
    {
        TwitterAccount ac = getAccount( user, name );
        return ac == null ? null : ac.getTwitter();
    }

    public TwitterAccount getAccount( User user, String name )
    {
        return accounts.computeIfAbsent( new Key( user.getId(), name.toLowerCase() ), factory );
    }

    private static class Key
    {

        private final long id;
        private final String name;

        public Key( long id, String name )
        {
            this.id = id;
            this.name = name;
        }

        public long getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }

        @Override
        public boolean equals( Object obj )
        {
            if( obj == null )
            {
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                return false;
            }
            final Key other = (Key) obj;
            if( this.id != other.id )
            {
                return false;
            }
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
            hash = 29 * hash + Objects.hashCode( this.name );
            return hash;
        }

    }
}
