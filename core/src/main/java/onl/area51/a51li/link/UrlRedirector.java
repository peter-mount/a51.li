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
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import onl.area51.a51li.AbstractServlet;

/**
 * Servlet that handles a redirection request
 * <p>
 * @author Peter T Mount
 */
@WebServlet(name = "redirector", urlPatterns = "/redirector/")
public class UrlRedirector
        extends AbstractServlet
{

    @Inject
    private LinkManager linkManager;

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp, Url url )
            throws ServletException,
                   IOException
    {
        // Issue the redirect
        resp.sendRedirect( url.getUrl() );

        // Now log the url
        linkManager.recordVisit( url, req );
    }

}
