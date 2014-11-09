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
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public class TwitterApplication
{

    public static final SQLFunction<ResultSet, TwitterApplication> fromSQL = rs -> new TwitterApplication(
            rs.getLong( "a.id" ),
            rs.getString( "a.appname" ),
            rs.getString( "a.key" ),
            rs.getString( "a.secret" ),
            rs.getBoolean( "a.enabled" )
    );

    private final long id;
    private final String applicationName;
    private final String key;
    private final String secret;
    private final boolean enabled;

    public TwitterApplication( long id, String applicationName, String key, String secret, boolean enabled )
    {
        this.id = id;
        this.applicationName = applicationName;
        this.key = key;
        this.secret = secret;
        this.enabled = enabled;
    }

    public long getId()
    {
        return id;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public String getKey()
    {
        return key;
    }

    public String getSecret()
    {
        return secret;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode( this.applicationName );
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
        final TwitterApplication other = (TwitterApplication) obj;
        if( this.id != other.id )
        {
            return false;
        }
        if( !Objects.equals( this.applicationName, other.applicationName ) )
        {
            return false;
        }
        return true;
    }

}
