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

import java.sql.ResultSet;
import java.util.Objects;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public class TwitterAccount
{

    public static final String SELECT_SQL = "SELECT t.*, a.* FROM twitter t"
                                            + " INNER JOIN user_twitter ut ON t.id = ut.twitterid"
                                            + " INNER JOIN users u ON ut.userid = u.id"
                                            + " INNER JOIN twitapp a ON t.application = a.id"
                                            + " WHERE u.id=? AND lower(t.account)=?";

    public static final SQLFunction<ResultSet, TwitterAccount> fromSQL = rs -> new TwitterAccount(
            rs.getInt( "t.id" ),
            rs.getString( "t.account" ),
            rs.getString( "t.token" ),
            rs.getString( "t.secret" ),
            TwitterApplication.fromSQL.apply( rs )
    );

    private final int id;
    private final String account;
    private final String token;
    private final String secret;
    private final Twitter twitter;

    public TwitterAccount( int id, String account, String token, String secret, TwitterApplication application )
    {
        this.id = id;
        this.account = account;
        this.token = token;
        this.secret = secret;

        ConfigurationBuilder cb = new ConfigurationBuilder().
                setOAuthConsumerKey( application.getKey() ).
                setOAuthConsumerSecret( application.getSecret() ).
                setOAuthAccessToken( token ).
                setOAuthAccessTokenSecret( secret );

        TwitterFactory f = new TwitterFactory( cb.build() );
        twitter = f.getInstance();
    }

    public int getId()
    {
        return id;
    }

    public String getAccount()
    {
        return account;
    }

    public String getToken()
    {
        return token;
    }

    public String getSecret()
    {
        return secret;
    }

    public Twitter getTwitter()
    {
        return twitter;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode( this.account );
        return hash;
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
        final TwitterAccount other = (TwitterAccount) obj;
        if( this.id != other.id )
        {
            return false;
        }
        if( !Objects.equals( this.account, other.account ) )
        {
            return false;
        }
        return true;
    }

}
