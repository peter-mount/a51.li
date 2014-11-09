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
package onl.area51.a51li;

import onl.area51.a51li.link.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractServlet
        extends HttpServlet
{

    @Override
    protected final void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException,
                   IOException
    {
        Url url = (Url) req.getAttribute( "url" );
        if( url == null )
        {
            notFound( resp, "Not provided by filter" );
        }
        else
        {
            doGet( req, resp, url );
        }
    }

    protected final void notFound( HttpServletResponse resp )
            throws IOException
    {
        notFound( resp, "Page not found" );
    }

    protected final void notFound( HttpServletResponse resp, String reason )
            throws IOException
    {
        resp.sendError( HttpServletResponse.SC_NOT_FOUND, reason );
    }

    protected abstract void doGet( HttpServletRequest req, HttpServletResponse resp, Url url )
            throws ServletException,
                   IOException;
}
