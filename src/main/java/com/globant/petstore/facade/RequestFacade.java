package com.globant.petstore.facade;

import com.globant.petstore.command.RestfulApiCommand;
import com.globant.petstore.command.invoker.RestfulApiInvoker;
import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.response.ResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RequestFacade {

    // this persistence mediator is currently doing Read and Write persistence operations
    private final PersistenceMediator persistenceMediator;

    // TODO: split up persistence mediator into Read mediator and Write mediator to improve performance.
    //  Each split mediator can point to a different DB instance

    public Mono<ResponseEntity<ResponseBody>> processRequestForMono(RestfulApiCommand command){
        return new RestfulApiInvoker(command, persistenceMediator)
                .executeCommandForMono();
    }

    public ResponseEntity<Flux<ResponseBody>> processRequestForFlux(RestfulApiCommand command){
        return ResponseEntity.ok(
                new RestfulApiInvoker(command, persistenceMediator).executeCommandForFlux());
    }

}
