package co.com.reactive.usecase.firstmessagecontroller;

import co.com.reactive.model.modelmessage.ModelMessage;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class FirstMessageControllerUseCase {

    private final ReactiveEventsGateway reactiveEventsGateway;

    public Mono<Void> handler(ModelMessage modelMessage){

        System.out.println(modelMessage.toString());
        return reactiveEventsGateway.emit(modelMessage);
    }
}
