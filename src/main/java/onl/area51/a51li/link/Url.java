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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import uk.trainwatch.util.sql.SQLFunction;

/**
 *
 * @author Peter T Mount
 */
public class Url
        implements Serializable
{

    private static final long serialVersionUID = 1L;

    public static final SQLFunction<ResultSet, Url> fromSQL = rs -> new Url(
            rs.getLong( 1 ),
            rs.getString( 2 ),
            rs.getTimestamp( 3 ),
            LinkType.get( rs.getInt( 4 ) ),
            rs.getLong( 5 )
    );

    private final long id;
    private String url;
    private final Timestamp timestamp;
    private final LinkType linkType;
    private final long userId;

    public Url( long id, String url, Timestamp timestamp, LinkType linkType, long userId )
    {
        this.id = id;
        this.url = url;
        this.timestamp = timestamp;
        this.linkType = linkType;
        this.userId = userId;
    }

    public long getId()
    {
        return id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public LinkType getLinkType()
    {
        return linkType;
    }

    public long getUserId()
    {
        return userId;
    }

}
