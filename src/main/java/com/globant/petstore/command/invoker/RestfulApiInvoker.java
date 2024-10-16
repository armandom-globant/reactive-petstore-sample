package com.globant.petstore.command.invoker;

import com.globant.petstore.command.RestfulApiCommand;
import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.response.ResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RestfulApiInvoker {

    private final RestfulApiCommand command;
    private final PersistenceMediator persistenceMediator;

    public Mono<ResponseEntity<ResponseBody>> executeCommandForMono(){
        return command.executeMono(persistenceMediator);
    }

    public Flux<ResponseBody> executeCommandForFlux(){
        return command.executeFlux(persistenceMediator);
    }
}
