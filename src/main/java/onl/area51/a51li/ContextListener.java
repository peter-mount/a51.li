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

import onl.area51.a51li.link.LinkManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import onl.area51.a51li.twitter.TwitterManager;
import uk.trainwatch.util.DaemonThreadFactory;

/**
 *
 * @author Peter T Mount
 */
@WebListener
public class ContextListener
        implements ServletContextListener
{

    @Override
    public void contextInitialized( ServletContextEvent sce )
    {
        DataSource dataSource;
        String rabbitUser, rabbitPassword, rabbitHost;

        try
        {
            dataSource = InitialContext.doLookup( "java:/comp/env/jdbc/links" );
            rabbitUser = InitialContext.doLookup( "java:/comp/env/rabbit/a51li/user" );
            rabbitPassword = InitialContext.doLookup( "java:/comp/env/rabbit/a51li/password" );
            rabbitHost = InitialContext.doLookup( "java:/comp/env/rabbit/a51li/host" );
        }
        catch( NamingException ex )
        {
            throw new RuntimeException( ex );
        }

        TwitterManager.INSTANCE.setDataSource( dataSource );
        LinkManager.INSTANCE.contextInitialized( dataSource, rabbitUser, rabbitPassword, rabbitHost );
    }

    @Override
    public void contextDestroyed( ServletContextEvent sce )
    {
        DaemonThreadFactory.INSTANCE.shutdown();
        LinkManager.INSTANCE.contextDestroyed();
    }

}
