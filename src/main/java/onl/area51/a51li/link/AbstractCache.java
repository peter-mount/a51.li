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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.sql.DataSource;
import static onl.area51.a51li.link.LinkManager.LOG;
import uk.trainwatch.util.sql.SQL;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author peter
 */
public class AbstractCache
{

    @Resource(name = "jdbc/links")
    protected DataSource dataSource;

    protected <T> T get( long id, String table, SQLFunction<ResultSet, T> factory )
    {
        System.out.printf( "get(%d,%s)", id, table );
        try( Connection con = dataSource.getConnection() ) {
            try( PreparedStatement s = con.prepareStatement( "SELECT * FROM " + table + " WHERE id=?" ) ) {
                s.setLong( 1, id );
                return SQL.stream( s, factory ).
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
