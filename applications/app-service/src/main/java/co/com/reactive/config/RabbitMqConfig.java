package co.com.reactive.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.api.handlers.registered.RegisteredCommandHandler;
import org.reactivecommons.async.api.handlers.registered.RegisteredEventListener;
import org.reactivecommons.async.impl.DiscardNotifier;
import org.reactivecommons.async.impl.HandlerResolver;
import org.reactivecommons.async.impl.communications.ReactiveMessageListener;
import org.reactivecommons.async.impl.communications.TopologyCreator;
import org.reactivecommons.async.impl.config.ConnectionFactoryProvider;
import org.reactivecommons.async.impl.config.RabbitProperties;
import org.reactivecommons.async.impl.config.props.AsyncProps;
import org.reactivecommons.async.impl.config.props.BrokerConfigProps;
import org.reactivecommons.async.impl.converters.MessageConverter;
import org.reactivecommons.async.impl.listeners.ApplicationEventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;
import reactor.util.retry.Retry;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


@Log
@Component
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({
        RabbitProperties.class,
        AsyncProps.class
})
@Import(BrokerConfigProps.class)
public class RabbitMqConfig {

    @Value("${spring.application.name}")
    private String appName;

    private static final String SENDER_TYPE = "sender";
    private static final String LISTENER_TYPE = "listener";
    private static final String ERROR_RABBIT_CONNECTION = "Error creating connection to RabbitMq Broker. Starting retry process...";
    private final AsyncProps asyncProps;

    @Primary
    @Bean("INTEGRATION")
    public ConnectionFactoryProvider appConnectionFactory(RabbitProperties properties) throws KeyManagementException, NoSuchAlgorithmException {
        final ConnectionFactory factory = new ConnectionFactory();
        PropertyMapper map = PropertyMapper.get();
        map.from(properties::determineHost).whenNonNull().to(factory::setHost);
        map.from(properties::determinePort).to(factory::setPort);
        map.from(properties::determineUsername).whenNonNull().to(factory::setUsername);
        map.from(properties::determinePassword).whenNonNull().to(factory::setPassword);
        map.from(properties::determineVirtualHost).whenNonNull().to(factory::setVirtualHost);
        map.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds).to(factory::setRequestedHeartbeat);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        return () -> factory;
    }

    //@Bean("senderIntegration")
    //@Primary
    //public ReactiveMessageSender reactiveSender(@Qualifier("INTEGRATION") ConnectionFactoryProvider provider, MessageConverter converter,
    //                                            BrokerConfigProps brokerConfigProps, RabbitProperties rabbitProperties) {
    //    final Mono<Connection> senderConnection = createConnectionMonoIntegracion(provider.getConnectionFactory(), appName, SENDER_TYPE);
    //    final ChannelPoolOptions channelPoolOptions = new ChannelPoolOptions();
    //    final PropertyMapper map = PropertyMapper.get();
//
    //    map.from(rabbitProperties.getCache().getChannel()::getSize).whenNonNull()
    //            .to(channelPoolOptions::maxCacheSize);
//
    //    final ChannelPool channelPool = ChannelPoolFactory.createChannelPool(
    //            senderConnection,
    //            channelPoolOptions
    //    );
//
    //    final Sender sender = RabbitFlux.createSender(new SenderOptions()
    //            .channelPool(channelPool)
    //            .resourceManagementChannelMono(channelPool.getChannelMono()
    //                    .transform(Utils::cache)));
//
    //    return new ReactiveMessageSender(sender, brokerConfigProps.getAppName(), converter, new TopologyCreator(sender));
    //}

    //Mono<Connection> createConnectionMonoIntegracion(@Qualifier("INTEGRATION") ConnectionFactory factory, String connectionPrefix, String connectionType) {
    //    return Mono.fromCallable(() -> factory.newConnection(connectionPrefix + " " + connectionType + "integration"))
    //            .doOnError(err ->
    //                    log.log(Level.SEVERE, "Error creating connection to RabbitMq Broker. Starting retry process...", err)
    //            )
    //            .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(300))
    //                    .maxBackoff(Duration.ofMillis(3000)))
    //            .cache();
    //}


    //Relacionado con todo lo del listener del evento
/*
    @Bean
    @Primary
    public ApplicationEventListener eventListener(@Qualifier("integrationResolver")HandlerResolver resolver, MessageConverter messageConverter,
                                                  @Qualifier("integrationListener") ReactiveMessageListener receiver,
                                                  DiscardNotifier discardNotifier) {

        final ApplicationEventListener listener = new ApplicationEventListener(receiver,
                appName + ".subsEvents", resolver, asyncProps.getDomain().getEvents().getExchange(),
                messageConverter, asyncProps.getWithDLQRetry(), asyncProps.getMaxRetries(), asyncProps.getRetryDelay(),
                asyncProps.getDomain().getEvents().getMaxLengthBytes(),
                discardNotifier);

        listener.startListener();

        return listener;
    }

    @Bean("integrationListener")
    @Primary
    public ReactiveMessageListener messageListener(@Qualifier("INTEGRATION") ConnectionFactoryProvider provider) {
        final Mono<Connection> connection =
                createConnectionMono(provider.getConnectionFactory(), appName, LISTENER_TYPE);
        final Receiver receiver = RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connection));
        final Sender sender = RabbitFlux.createSender(new SenderOptions().connectionMono(connection));

        return new ReactiveMessageListener(receiver,
                new TopologyCreator(sender),
                asyncProps.getFlux().getMaxConcurrency(),
                asyncProps.getPrefetchCount());
    }

    Mono<Connection> createConnectionMono(@Qualifier("INTEGRATION") ConnectionFactory factory, String connectionPrefix, String connectionType) {
        return Mono.fromCallable(() -> factory.newConnection(connectionPrefix + " " + connectionType))
                .doOnError(err ->
                        log.log(Level.SEVERE, ERROR_RABBIT_CONNECTION, err)
                )
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(300))
                        .maxBackoff(Duration.ofMillis(3000)))
                .cache();
    }

    @Bean("integrationResolver")
    @Primary
    public HandlerResolver integrationResolver(ApplicationContext context) {

        final HandlerRegistry registry = context.getBean("integrationRegistry", HandlerRegistry.class);
        ConcurrentHashMap<String, RegisteredEventListener> eventListeners = registry.getEventListeners()
                .stream()
                .collect(ConcurrentHashMap::new, (map, handler) -> map.put(handler.getPath(), handler),
                        ConcurrentHashMap::putAll);

        //final HandlerRegistry reprocessRegistry = context.getBean("eventNotificationListener", HandlerRegistry.class);
        //ConcurrentHashMap<String, RegisteredCommandHandler> commandHandlers = reprocessRegistry.getCommandHandlers()
        //        .stream()
        //        .collect(ConcurrentHashMap::new, (map, handler) -> map.put(handler.getPath(), handler),
        //                ConcurrentHashMap::putAll);
        return new HandlerResolver(null, eventListeners, null, null);
    }

 */

}
