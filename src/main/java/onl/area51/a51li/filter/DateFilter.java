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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter which adds the LocalDateTime and derivatives to the request
 * <p>
 * @author Peter T Mount
 */
@WebFilter( filterName = "DateFilter", urlPatterns = "/*" )
public class DateFilter
        extends AbstractFilter
{

    @Override
    protected void doFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
            throws IOException,
                   ServletException
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();

        // LocalDateTime of the request
        request.setAttribute( "now", now );

        // LocalDate of the request
        request.setAttribute( "date", date );

        // The year, used in page footers
        request.setAttribute( "year", date.get( ChronoField.YEAR ) );

        // The page generated time, yyyy-mm-dd hh:mm:ss
        request.setAttribute( "pageGenerated",
                              now.truncatedTo( ChronoUnit.SECONDS ).
                              toString().
                              replace( 'T', ' ' ) );

        chain.doFilter( request, response );
    }

}
