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
import java.util.function.Function;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PutCommand implements RestfulApiCommand {
    private final TransactionRequestWrapper request;

    @Override
    public Mono<ResponseEntity<ResponseBody>> executeMono(PersistenceMediator persistenceMediator) {
        return persistenceMediator
                .findAlreadyExistingResource(request.getRequestBody())

                // onComplete was produced - this is expecting to end if the resource does not exist
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource was expected to exist in order to replace it.")))

                // onNext was produced - then this is validating (filtering out) resources with different version values
                .filter(existingResource ->
                        Objects.equals(existingResource.getVersion(), request.getRequestBody().getVersion()))

                // onComplete was produced - if previous version check emits the completion signal (that means version check failed and item was filtered out)
                .switchIfEmpty(
                        Mono.error(new ResourceConflictException("Request version does not match latest version")))

                // async transformation
                .flatMap(existingResourceToRequest ->

                        persistenceMediator.findAlreadyExistingResource(request.getRequestBody())
                                .filterWhen(resourceInDbWithSameNameAlreadyTaken ->
                                        Mono.just(
                                                !Objects.equals(
                                                        existingResourceToRequest.getId(),
                                                        resourceInDbWithSameNameAlreadyTaken.getId())))
                                .doOnNext(duplicatedResource -> {
                                    throw new ResourceAlreadyExistsException(duplicatedResource.toString());
                                })
                                .then(
                                        Mono.fromCallable(() ->
                                                persistenceMediator.updateResource(request.getPathVariables(), request.getRequestBody()))
                                )
                )
                .flatMap(Function.identity())

                .map(ResponseEntity::ok);
    }


    @Override
    public Flux<ResponseBody> executeFlux(PersistenceMediator persistenceMediator) {
        return Flux.error(new UnsupportedOperationException("Bulk replacement is not supported"));
    }
}
