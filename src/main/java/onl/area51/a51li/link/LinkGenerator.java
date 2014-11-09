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
import javax.json.JsonObject;
import javax.sql.DataSource;
import onl.area51.a51li.URLCodec;
import uk.trainwatch.util.JsonUtils;
import uk.trainwatch.util.sql.SQL;
import uk.trainwatch.util.sql.SQLFunction;

/**
 * SQLFunction to take a JsonObject and generate a url adding a uid field to the object.
 * <p>
 * @author Peter T Mount
 */
public class LinkGenerator
        implements SQLFunction<JsonObject, JsonObject>
{

    protected static final Logger LOG = Logger.getLogger( LinkGenerator.class.getName() );

    private final DataSource dataSource;

    public LinkGenerator( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Override
    public JsonObject apply( JsonObject t )
            throws SQLException
    {

        LOG.log( Level.FINE, "Received {0}", t );

        if( t == null )
        {
            return null;
        }

        String url = JsonUtils.getString( t, "url" );
        String userName = JsonUtils.getString( t, "user" );
        String hash = JsonUtils.getString( t, "hash" );

        if( url == null || userName == null || hash == null )
        {
            return null;
        }

        Long uid;
        try( Connection con = dataSource.getConnection() )
        {
            try( PreparedStatement s = con.prepareStatement(
                    "SELECT createlink(?,?,?,?)"
            ) )
            {
                s.setString( 1, url );
                s.setString( 2, userName );
                s.setString( 3, hash );
                s.setInt( 4, LinkType.URL.getDbType() );
                uid = SQL.stream( s, SQL.LONG_LOOKUP ).
                        findFirst().
                        orElse( null );
            }
        }

        LOG.log( Level.FINE, "Created uid {0}", uid );

        if( uid == null )
        {
            return null;
        }

        String shortUrl = "http://a51.li/" + URLCodec.getUID( uid );

        LOG.log( Level.INFO, "Short Url {0}", shortUrl );

        return JsonUtils.createObjectBuilder( t ).
                add( "uid", uid ).
                add( "url", shortUrl ).
                build();
    }

}
