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
package onl.area51.a51li.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.sql.DataSource;
import uk.trainwatch.util.JsonUtils;

/**
 * Log's a visit as recorded by /redirector
 * <p>
 * @author Peter T Mount
 */
@ApplicationScoped
public class VisitConsumer
        implements Consumer<JsonObject>
{

    @Resource(name = "jdbc/links")
    private DataSource dataSource;

    @Override
    public void accept( JsonObject t )
    {
        // Not a valid uid then abort
        long uid = JsonUtils.getLong( t, "uid" );
        if( uid > 0 ) {
            try {
                record( uid, JsonUtils.getString( t, "remote", "" ), JsonUtils.getString( t, "useragent", "" ) );
            }
            catch( SQLException ex ) {
                Logger.getLogger( getClass().getName() ).log( Level.SEVERE, null, ex );
            }
        }
    }

    private void record( long uid, String remote, String userAgent )
            throws SQLException
    {
        try( Connection con = dataSource.getConnection() ) {
            try( PreparedStatement s = con.prepareStatement(
                    "INSERT INTO visit (url,remote,useragent,tm) VALUES (?,remotehost(?),useragent(?),now())" ) ) {
                s.setLong( 1, uid );
                s.setString( 2, remote );
                s.setString( 3, userAgent );
                s.executeUpdate();
            }
        }
    }

}
