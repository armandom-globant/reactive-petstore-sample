package com.globant.petstore.command;

import com.globant.petstore.command.exception.ResourceNotFoundException;
import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.response.ResponseBody;
import com.globant.petstore.wrapper.request_wrapper.TransactionRequestWrapper;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class GetCommand implements RestfulApiCommand {

    private final TransactionRequestWrapper request;

    @Override
    public Mono<ResponseEntity<ResponseBody>> executeMono(PersistenceMediator persistenceMediator) {
        return persistenceMediator
                .findResourceById(request.getPathVariables())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException()))
                // Mono operations are allowed to take control of the Http response code
                .map(ResponseEntity::ok);
    }

    @Override
    public Flux<ResponseBody> executeFlux(PersistenceMediator persistenceMediator) {
        return persistenceMediator
                .findAllResources(request.getPageable())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException()));
    }
}
