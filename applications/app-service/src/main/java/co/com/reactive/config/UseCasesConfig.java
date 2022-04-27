package co.com.reactive.config;

import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.reactive.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

     //@Bean
     //public DomainEventBus domainEventBus(){
     //    return new DomainEventBus() {
     //        @Override
     //        public <T> Publisher<Void> emit(DomainEvent<T> event) {
     //            return null;
     //        }
     //    };
     //}
}
