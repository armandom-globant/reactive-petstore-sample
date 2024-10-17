package com.globant.petstore.command;

import com.globant.petstore.command.exception.ResourceAlreadyExistsException;
import com.globant.petstore.command.exception.ResourceConflictException;
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

import java.util.Objects;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PutCommand implements RestfulApiCommand {
    private final TransactionRequestWrapper request;

    @Override
    public Mono<ResponseEntity<ResponseBody>> executeMono(PersistenceMediator persistenceMediator) {
        return persistenceMediator.findAlreadyExistingResource(request.getRequestBody())
                // Resource not found
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource was expected to exist in order to replace it.")))
                .flatMap(existingResource -> {

                    // Version mismatch
                    if (!Objects.equals(existingResource.getVersion(), request.getRequestBody().getVersion())) {
                        return Mono.error(new ResourceConflictException("Request version does not match latest version"));
                    }

                    // Version match
                    return persistenceMediator
                            .findResourceWithNameAlreadyTaken(request.getRequestBody())
                            .filter(resourceInDbWithSameNameAlreadyTaken ->
                                    !Objects.equals(existingResource.getId(), resourceInDbWithSameNameAlreadyTaken.getId()))
                            .hasElement()
                            .flatMap(duplicateExists -> {

                                // Duplicate resource found
                                if (duplicateExists) {
                                    return Mono.error(new ResourceAlreadyExistsException("Duplicate resource found"));
                                }
                                // No duplicate resource
                                return persistenceMediator.updateResource(request.getPathVariables(), request.getRequestBody());
                            });
                })
                .map(ResponseEntity::ok);
    }

    @Override
    public Flux<ResponseBody> executeFlux(PersistenceMediator persistenceMediator) {
        return Flux.error(new UnsupportedOperationException("Bulk replacement is not supported"));
    }
}