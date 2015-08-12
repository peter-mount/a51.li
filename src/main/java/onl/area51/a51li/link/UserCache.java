/*
 * Copyright 2015 peter.
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
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import static onl.area51.a51li.link.LinkManager.LOG;
import onl.area51.a51li.sql.User;
import uk.trainwatch.util.sql.SQL;

/**
 *
 * @author peter
 */
@ApplicationScoped
@CacheDefaults(cacheName = "a51UserCache")
public class UserCache
        extends AbstractCache
{

    @CacheResult
    public User getUser( @CacheKey long uid )
    {
        return get( uid, "users", User.fromSQL );
    }

    @CacheResult
    public User getUser( @CacheKey String name )
    {
        try( Connection con = dataSource.getConnection() ) {
            try( PreparedStatement s = SQL.prepare( con, "SELECT * FROM users WHERE username=?", name ) ) {
                s.setString( 1, name );
                return SQL.stream( s, User.fromSQL ).
                        findFirst().
                        orElse( null );
            }
        }
        catch( SQLException ex ) {
            LOG.log( Level.SEVERE, null, ex );
            return null;
        }
    }

}
