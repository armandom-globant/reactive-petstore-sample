package com.globant.petstore.command;

import com.globant.petstore.command.exception.ResourceNotFoundException;
import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.pet.model.response.PetResponseBody;
import com.globant.petstore.response.ResponseBody;
import com.globant.petstore.wrapper.request_wrapper.TransactionRequestWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCommandTest {

    private GetCommand subject;

    @Mock
    private PersistenceMediator persistenceMediatorMock;

    @Test
    void shouldReturnAListOfNonEmptyPets() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withPageable(mock(Pageable.class))
                        .build();

        subject = new GetCommand(request);

        // setup stubs
        when(persistenceMediatorMock.findAllResources(any(Pageable.class)))
                .thenReturn(
                        Flux.just(PetResponseBody.builder().build()));

        final Flux<ResponseBody> result = subject.executeFlux(persistenceMediatorMock);

        StepVerifier
                .create(result)
                .expectSubscription()
                .expectNext(PetResponseBody.builder().build())
                .verifyComplete();

        verify(persistenceMediatorMock, only()).findAllResources(any(Pageable.class));
    }

    @Test
    void shouldReturnANotFoundExceptionErrorWhenListingAllResourcesHasZeroResourcesStored() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withPageable(mock(Pageable.class))
                        .build();

        subject = new GetCommand(request);

        // setup stubs
        when(persistenceMediatorMock.findAllResources(any(Pageable.class)))
                .thenReturn(Flux.empty());

        final Flux<ResponseBody> result = subject.executeFlux(persistenceMediatorMock);

        StepVerifier
                .create(result)
                .expectSubscription()
                .expectError(ResourceNotFoundException.class);

        verify(persistenceMediatorMock, only()).findAllResources(any(Pageable.class));
    }


    @Test
    void shouldReturnAnExistingResourceWhenFindingById() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withPathVariables(Map.of("id", "some-id"))
                        .build();

        subject = new GetCommand(request);

        // setup stubs
        when(persistenceMediatorMock.findResourceById(anyMap()))
                .thenReturn(Mono.just(PetResponseBody.builder().build()));

        final Mono<ResponseEntity<ResponseBody>> result = subject.executeMono(persistenceMediatorMock);

        StepVerifier
                .create(result)
                .expectSubscription()
                .expectNext(ResponseEntity.ok(PetResponseBody.builder().build()))
                .verifyComplete();

        verify(persistenceMediatorMock, only()).findResourceById(anyMap());
    }


    @Test
    void shouldReturnANotFoundExceptionErrorWhenListByIdDoesNotExist() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withPathVariables(Map.of("id", "some-id"))
                        .build();

        subject = new GetCommand(request);

        // setup stubs
        when(persistenceMediatorMock.findResourceById(anyMap()))
                .thenReturn(Mono.empty());

        final Mono<ResponseEntity<ResponseBody>> result = subject.executeMono(persistenceMediatorMock);

        StepVerifier.create(result)
                .expectSubscription()
                .expectError(ResourceNotFoundException.class);

        verify(persistenceMediatorMock, only()).findResourceById(anyMap());
    }

}