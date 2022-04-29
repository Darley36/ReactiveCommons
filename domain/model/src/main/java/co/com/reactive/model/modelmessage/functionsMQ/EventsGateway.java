package co.com.reactive.model.modelmessage.functionsMQ;

import co.com.reactive.model.modelmessage.ModelMessage;
import reactor.core.publisher.Mono;

public interface EventsGateway {
    Mono<Void> emit(ModelMessage event);
}
