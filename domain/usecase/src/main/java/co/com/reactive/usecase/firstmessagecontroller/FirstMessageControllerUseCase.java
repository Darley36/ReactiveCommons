package co.com.reactive.usecase.firstmessagecontroller;

import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.model.modelmessage.functionsMQ.EventsGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FirstMessageControllerUseCase {

    private final EventsGateway eventsGateway;

    public Mono<Void> handler(ModelMessage modelMessage){

        System.out.println(modelMessage.toString());
        return eventsGateway.emit(modelMessage);
    }
}
