package com.globant.petstore.mediator;

import com.globant.petstore.mediator.strategies.pet.PetRepository;
import com.globant.petstore.model.request.RequestBody;
import com.globant.petstore.pet.model.response.PetResponseBody;
import com.globant.petstore.response.ResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PersistenceMediator {

    // mediator just knows of PetRepository, but this should be changed to support more than 1 repository
    private final PetRepository petRepository;

    public Flux<ResponseBody> findAllResources(Pageable pageable){
        return Flux.fromIterable(petRepository.findAll(pageable));
    }

    public Mono<ResponseBody> findResourceById(Map<String, String> pathVariables){
        return Mono.just(petRepository.findById(pathVariables));
    }

    public Mono<ResponseBody> saveResource(RequestBody requestBody){
        return Mono.just(petRepository.save(requestBody));
    }

    public Mono<ResponseBody> updateResource(Map<String, String> pathVariables, RequestBody requestBody){
        return Mono.just(petRepository.replace(pathVariables, requestBody));
    }

    public Mono<ResponseBody> findAlreadyExistingResource(RequestBody requestBody) {
        // intentionally avoid using requestBody
        return Mono.just(
                PetResponseBody
                        .builder()
                        .withId("1")
                        .withVersion(1L)
                        .build());
    }

    // set to return no name collision, that means the replacement new name is not taken by any other resource
    public Mono<ResponseBody> findResourceWithNameAlreadyTaken(RequestBody requestBody) {
        return Mono.empty();
    }
}
