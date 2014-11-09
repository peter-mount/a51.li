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

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Peter T Mount
 */
public class URLCodec
{

    private static final Pattern pattern = Pattern.compile( "^/a51li/([a-zA-Z0-9]+)([+]?)$" );

    /**
     * This determines the character values
     */
    private static final int RADIX = Character.MAX_RADIX;

    public static String getUID( long uid )
    {
        return Long.toString( uid, RADIX );
    }

    public static Optional<Long> getUID( String s )
    {
        if( s != null && !s.isEmpty() )
        {
            Matcher m = pattern.matcher( s );
            if( m.matches() )
            {
                try
                {
                    long uid = Long.valueOf( m.group( 1 ), RADIX );

                    // + modifier makes uid negative
                    if( "+".equals( m.group( 2 ) ) )
                    {
                        uid = -uid;
                    }

                    return Optional.of( uid );
                }
                catch( NumberFormatException ex )
                {
                    // Ignore
                }
            }
        }

        return Optional.empty();
    }
}
