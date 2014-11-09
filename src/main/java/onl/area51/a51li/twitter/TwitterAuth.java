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
package onl.area51.a51li.twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 * @author Peter T Mount
 */
public class TwitterAuth
{

    public static void main( String args[] )
            throws Exception
    {
        final String consumerKey = args[0];
        final String consumerSecret = args[1];
        
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer( consumerKey, consumerSecret );
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
        while( null == accessToken )
        {
            System.out.println( "Open the following URL and grant access to your account:" );
            System.out.println( requestToken.getAuthorizationURL() );
            System.out.print( "Enter the PIN(if aviailable) or just hit enter.[PIN]:" );
            String pin = br.readLine();
            try
            {
                if( pin.length() > 0 )
                {
                    accessToken = twitter.getOAuthAccessToken( requestToken, pin );
                }
                else
                {
                    accessToken = twitter.getOAuthAccessToken();
                }
            }
            catch( TwitterException te )
            {
                if( 401 == te.getStatusCode() )
                {
                    System.out.println( "Unable to get the access token." );
                }
                else
                {
                    te.printStackTrace();
                }
            }
        }
        //persist to the accessToken for future reference.
        storeAccessToken( twitter.verifyCredentials().
                getId(), accessToken );
        Status status = twitter.updateStatus( args[0] );
        System.out.println( "Successfully updated the status to [" + status.getText() + "]." );
        System.exit( 0 );
    }

    private static void storeAccessToken( long useId, AccessToken accessToken )
    {
        System.out.println( "useId " + useId );
        System.out.println( "token " + accessToken.getToken() );
        System.out.println( "secret " + accessToken.getTokenSecret());

        //store accessToken.getToken()
        //store accessToken.getTokenSecret()
    }
}
