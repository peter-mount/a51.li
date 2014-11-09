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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter T Mount
 */
public abstract class AbstractFilter
        implements Filter
{

    @Override
    public void init( FilterConfig filterConfig )
            throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public final void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
            throws IOException,
                   ServletException
    {
        doFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain );
    }

    protected abstract void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
            throws IOException,
                   ServletException;
}
