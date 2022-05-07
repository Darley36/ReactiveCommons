package co.com.reactive.mq.listener;
import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.usecase.firstmessagecontroller.FirstMessageControllerUseCase;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.reactivecommons.async.api.HandlerRegistry.register;


@Configuration
@EnableMessageListeners
@RequiredArgsConstructor
public class SampleMQMessageListener {

    private final FirstMessageControllerUseCase controllerUseCase;

    @Bean("integrationRegistry")
    public HandlerRegistry eventSubscriptions() {
        return register()
                .listenEvent("event.listener.queue", event -> controllerUseCase.handler(event.getData()), ModelMessage.class);
    }

    @Bean("eventNotificationListener")
    public HandlerRegistry eventSubscriptionsReprocess() {
        return register()
                .handleCommand("message.command.retry",command -> controllerUseCase.RecieveMessage(command.getData()),ModelMessage.class);
    }
}
