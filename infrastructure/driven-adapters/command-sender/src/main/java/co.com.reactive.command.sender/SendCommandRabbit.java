package co.com.reactive.command.sender;

import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.model.modelmessage.functionsMQ.CommandBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.Command;
import org.reactivecommons.async.api.DirectAsyncGateway;
import org.reactivecommons.async.impl.config.annotations.EnableDirectAsyncGateway;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

@Log
@Component
@EnableDirectAsyncGateway
@RequiredArgsConstructor
public class SendCommandRabbit implements CommandBus {

    private static final String MESSAGE = "message.command.retry";
    private final DirectAsyncGateway asyncGateway;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public Mono<Void> retryMessage(ModelMessage modelMessage) {
        System.out.println("retrySendingReportToSinco");
        log.log(Level.INFO, "retrySendingReportToSinco");
        return asyncGateway
                .sendCommand(new Command<>(MESSAGE, UUID.randomUUID().toString(),modelMessage),appName).then();

    }
}
