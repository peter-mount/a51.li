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
package onl.area51.a51li.memo;

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
public enum MemoType
{

    /**
     * Content is in html
     */
    HTML( 1, "viewhtml" ),
    /**
     * Content is plain text
     */
    TEXT( 2, "viewtext" ),
    /**
     * Content is plain text but is log output
     */
    LOG( 3, "viewlog" );
    private static final Map<Integer, MemoType> TYPES = new ConcurrentHashMap<>();

    static
    {
        for( MemoType t : values() )
        {
            TYPES.put( t.dbType, t );
        }
    }

    public static MemoType get( int type )
    {
        return TYPES.get( type );
    }
    private final int dbType;
    private final String cssClass;

    private MemoType( int dbType, String cssClass )
    {
        this.dbType = dbType;
        this.cssClass = cssClass;
    }

    public int getDbType()
    {
        return dbType;
    }

    public String getCssClass()
    {
        return cssClass;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response )
            throws ServletException,
                   IOException
    {
        request.getRequestDispatcher( cssClass ).
                forward( request, response );
    }
}
