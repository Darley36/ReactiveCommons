package co.com.reactive.config;

import co.com.reactive.model.modelmessage.ModelMessage;
import co.com.reactive.model.modelmessage.functionsMQ.CommandBus;
import co.com.reactive.model.modelmessage.functionsMQ.EventsGateway;
import co.com.reactive.usecase.firstmessagecontroller.FirstMessageControllerUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import reactor.core.publisher.Mono;

@Configuration
//@ComponentScan(basePackages = "co.com.reactive.usecase",
//        includeFilters = {
//                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
//        },
//        useDefaultFilters = false)
public class UseCasesConfig {


        @Bean
        public FirstMessageControllerUseCase firstMessageControllerUseCase(EventsGateway eventsGateway, CommandBus commandBus) {
                return new FirstMessageControllerUseCase(eventsGateway,commandBus);
        }
}
