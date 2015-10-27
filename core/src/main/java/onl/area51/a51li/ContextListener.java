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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import onl.area51.a51li.link.LinkGenerator;
import onl.area51.a51li.memo.MemoGenerator;
import onl.area51.a51li.sql.VisitConsumer;
import onl.area51.a51li.twitter.TwitterConsumer;
import uk.trainwatch.rabbitmq.Rabbit;
import uk.trainwatch.rabbitmq.RabbitMQ;
import uk.trainwatch.util.sql.SQLConsumer;

/**
 *
 * @author Peter T Mount
 */
@WebListener
@ApplicationScoped
public class ContextListener
        implements ServletContextListener
{

    protected static final Logger LOG = Logger.getLogger( ContextListener.class.getName() );

    @Inject
    private Rabbit rabbit;

    @Inject
    private TwitterConsumer twitterConsumer;

    @Inject
    private VisitConsumer visitConsumer;

    @Inject
    private LinkGenerator linkGenerator;

    @Inject
    private MemoGenerator memoGenerator;

    @Override
    public void contextInitialized( ServletContextEvent sce )
    {
        // Note: NO_HOSTNAME for all of these as we want multiple instances to share the same queues

        Map<String, Object> properties = new HashMap<>();
        properties.put( RabbitMQ.NO_HOSTNAME, true );
        rabbit.queueDurableConsumer( "twitter.tweet", "twitter.tweet", properties, RabbitMQ.toJsonObject, twitterConsumer );

        // Background recording of visits
        properties = new HashMap<>();
        properties.put( RabbitMQ.NO_HOSTNAME, true );
        rabbit.queueDurableConsumer( "a51.li.visit", "a51.li.visit", properties, RabbitMQ.toJsonObject, visitConsumer );

        // Consumer that receives link create requests
        properties = new HashMap<>();
        properties.put( RabbitMQ.NO_HOSTNAME, true );
        // TODO add ability to respond once we have created a link
        rabbit.queueDurableConsumer( "a51.li.link", "a51.li.link", properties, RabbitMQ.toJsonObject, SQLConsumer.guard( m -> linkGenerator.apply( m ) ) );

        // Consumer that receives memo create requests.
        // If json contains tweet details then we also tweet
        properties = new HashMap<>();
        properties.put( RabbitMQ.NO_HOSTNAME, true );
        // If on dev then use a dummy dev queue
        if( rabbit.isDev() ) {
            rabbit.queueConsumer( "a51.li.memodev", "a51.li.memodev", properties, RabbitMQ.toJsonObject, SQLConsumer.guard( memoGenerator ) );
        }
        else {
            rabbit.queueDurableConsumer( "a51.li.memo", "a51.li.memo", properties, RabbitMQ.toJsonObject, SQLConsumer.guard( memoGenerator ) );
        }
    }

    @Override
    public void contextDestroyed( ServletContextEvent sce )
    {
    }

}
