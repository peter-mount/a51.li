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
package onl.area51.a51li.filter;

import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import onl.area51.a51li.link.LinkManager;
import onl.area51.a51li.URLCodec;
import onl.area51.a51li.link.Url;

/**
 * Filter which intercepts any short url requests
 * <p>
 * @author Peter T Mount
 */
@WebFilter(filterName = "shortUrl", urlPatterns = "/*")
public class ShortURLFilter
        extends AbstractFilter
{

    @Inject
    private LinkManager linkManager;

    @Override
    protected void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
            throws IOException,
                   ServletException
    {
        Optional<Long> uid = URLCodec.getUID( request.getRequestURI() );

        if( uid.isPresent() ) {
            Url url = linkManager.getUrl( uid.get() );
            if( url == null ) {
                response.sendError( HttpServletResponse.SC_NOT_FOUND );
            }
            else {
                request.setAttribute( "url", url );
//                if( uid.get() > 0 )
//                {
                url.getLinkType().
                        forward( request, response );
//                }
//                else if( uid.get() < 0 )
//                {
//                    request.getRequestDispatcher( "/details/" ).
//                            forward( request, response );
//                }
            }
        }
        else {
            chain.doFilter( request, response );
        }
    }

}