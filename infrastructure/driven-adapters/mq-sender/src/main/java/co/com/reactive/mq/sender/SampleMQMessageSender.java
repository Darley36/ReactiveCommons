package co.com.reactive.mq.sender;

import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.model.modelmessage.functionsMQ.EventsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@Component
@EnableDomainEventBus
@RequiredArgsConstructor
public class SampleMQMessageSender implements EventsGateway {

    private final DomainEventBus domainEventBus;

    @Override
    public Mono<Void> emit(ModelMessage event) {
        log.log(Level.INFO, "Emitiendo evento de dominio: {0}: {1}", new String[]{"event.publisher.queue", event.toString()});
        return from(domainEventBus.emit(new DomainEvent<>("event.publisher.queue", UUID.randomUUID().toString(), event)));
    }
}
