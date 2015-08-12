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

import java.io.Serializable;
import java.sql.ResultSet;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public class User
        implements Serializable
{

    private static final long serialVersionUID = 1L;

    public static final SQLFunction<ResultSet, User> fromSQL = rs -> new User(
            rs.getLong( 1 ),
            rs.getString( 2 ),
            rs.getString( 3 ),
            rs.getString( 4 ),
            rs.getBoolean( 5 ),
            rs.getString( 6 ),
            rs.getString( 7 )
    );

    private final long id;
    private final String username;
    private final String userkey;
    private final String homepage;
    private final boolean enabled;
    private final String logo;
    private final String password;

    public User( long id, String username, String userkey, String homepage, boolean enabled,
                 String logo,
                 String password )
    {
        this.id = id;
        this.username = username;
        this.userkey = userkey;
        this.homepage = homepage;
        this.enabled = enabled;

        // Logo defaults to the alien badge if not defined
        this.logo = (logo == null || logo.isEmpty()) ? "//area51.onl/images/area51logo.png" : logo;

        this.password = password;
    }

    public long getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getUserkey()
    {
        return userkey;
    }

    public String getHomepage()
    {
        return homepage;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getLogo()
    {
        return logo;
    }

    public String getPassword()
    {
        return password;
    }

}
