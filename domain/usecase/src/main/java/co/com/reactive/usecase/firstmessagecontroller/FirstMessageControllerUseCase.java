package co.com.reactive.usecase.firstmessagecontroller;

import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.model.modelmessage.functionsMQ.CommandBus;
import co.com.reactive.model.modelmessage.functionsMQ.EventsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
@Log
@RequiredArgsConstructor
public class FirstMessageControllerUseCase {

    private final EventsGateway eventsGateway;
    private final CommandBus commandBus;

    public Mono<Void> handler(ModelMessage modelMessage){

        System.out.println(modelMessage.toString());
        return eventsGateway.emit(modelMessage)
                .then(commandBus.retryMessage(modelMessage));
    }

    public Mono<Void> RecieveMessage(ModelMessage modelMessage){

        System.out.println("Se recibio por medio de un comando el mensaje "+modelMessage.toString());
        return Mono.empty();
    }
}
