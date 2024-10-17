package com.globant.petstore.command;

import com.globant.petstore.command.exception.ResourceAlreadyExistsException;
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
public class PostCommand implements RestfulApiCommand {

    private final TransactionRequestWrapper request;

    @Override
    public Mono<ResponseEntity<ResponseBody>> executeMono(PersistenceMediator persistenceMediator) {
        return persistenceMediator
                .findAlreadyExistingResource(request.getRequestBody())

                .doOnNext(responseBody -> {throw new ResourceAlreadyExistsException(responseBody.toString());})

                .then(Mono.fromCallable(() ->persistenceMediator.saveResource(request.getRequestBody())))

                .flatMap(savedResource -> savedResource)

                .map(ResponseEntity::ok);
    }

    @Override
    public Flux<ResponseBody> executeFlux(PersistenceMediator persistenceMediator) {
        return Flux.error(new UnsupportedOperationException("Bulk save is not supported"));
    }
}
