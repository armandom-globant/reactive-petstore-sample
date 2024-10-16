package com.globant.petstore.command;

import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.response.ResponseBody;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RestfulApiCommand {

    Mono<ResponseEntity<ResponseBody>> executeMono(PersistenceMediator persistenceMediator);

    Flux<ResponseBody> executeFlux(PersistenceMediator persistenceMediator);
}
