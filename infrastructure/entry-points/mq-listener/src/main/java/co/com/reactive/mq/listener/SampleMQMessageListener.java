package co.com.reactive.mq.listener;
import lombok.AllArgsConstructor;
import org.reactivecommons.async.api.HandlerRegistry;
import org.reactivecommons.async.impl.config.annotations.EnableMessageListeners;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
@EnableMessageListeners
@AllArgsConstructor
public class SampleMQMessageListener {

    @Bean
    public HandlerRegistry eventSubscriptions(){
        return HandlerRegistry.register()
                .listenEvent("event.listener.queue", event -> controller.handler(event.getData()), MessageToBotmaker.class);
    }
}
