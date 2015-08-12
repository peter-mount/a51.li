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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter T Mount
 */
public enum LinkType
{

    /**
     * A normal external URL
     */
    URL( 1, "/redirector/" ),
    /**
     * A memo
     */
    MEMO( 2, "/viewmemo/" ),
    /**
     * An image - not yet supported, probably will use S3 for storage?
     */
    IMAGE( 3, "/viewimage/" );
    private static final Map<Integer, LinkType> TYPES = new ConcurrentHashMap<>();

    static
    {
        for( LinkType t : values() )
        {
            TYPES.put( t.dbType, t );
        }
    }

    public static LinkType get( int type )
    {
        return TYPES.get( type );
    }
    private final int dbType;
    private final String servlet;

    private LinkType( int dbType, String servlet )
    {
        this.dbType = dbType;
        this.servlet = servlet;
    }

    public int getDbType()
    {
        return dbType;
    }

    public String getServlet()
    {
        return servlet;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response )
            throws ServletException,
                   IOException
    {
        request.getRequestDispatcher( servlet ).
                forward( request, response );
    }
}
