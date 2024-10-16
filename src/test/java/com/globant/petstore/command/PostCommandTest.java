package com.globant.petstore.command;

import com.globant.petstore.command.exception.ResourceAlreadyExistsException;
import com.globant.petstore.mediator.PersistenceMediator;
import com.globant.petstore.pet.model.request.PetPostRequestBody;
import com.globant.petstore.pet.model.response.PetResponseBody;
import com.globant.petstore.response.ResponseBody;
import com.globant.petstore.wrapper.request_wrapper.TransactionRequestWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCommandTest {

    private PostCommand subject;

    @Mock
    private PersistenceMediator persistenceMediatorMock;

    @Test
    void shouldSaveANewAndNonExistingResource() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withRequestBody(
                                PetPostRequestBody
                                        .builder()
                                        .withName("some-name")
                                        .withStatus("random-status")
                                        .build())
                        .build();

        subject = new PostCommand(request);

        when(persistenceMediatorMock.findAlreadyExistingResource(any(PetPostRequestBody.class)))
                .thenReturn(Mono.empty());

        when(persistenceMediatorMock.saveResource(any(PetPostRequestBody.class)))
                .thenReturn(Mono.just(PetResponseBody.builder().build()));

        Mono<ResponseEntity<ResponseBody>> result = subject.executeMono(persistenceMediatorMock);

        StepVerifier.create(result)
                .expectSubscription()
                .expectNext(ResponseEntity.ok(PetResponseBody.builder().build()))
                .verifyComplete();

        verify(persistenceMediatorMock).findAlreadyExistingResource(any(PetPostRequestBody.class));
        verify(persistenceMediatorMock).saveResource(any(PetPostRequestBody.class));
    }

    @Test
    void shouldThrowResourceAlreadyExistsExceptionWhenResourceExists() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withRequestBody(
                                PetPostRequestBody
                                        .builder()
                                        .withName("existing-name")
                                        .withStatus("existing-status")
                                        .build())
                        .build();

        subject = new PostCommand(request);

        when(persistenceMediatorMock.findAlreadyExistingResource(any(PetPostRequestBody.class)))
                .thenReturn(Mono.just(PetResponseBody.builder().build()));

        Mono<ResponseEntity<ResponseBody>> result = subject.executeMono(persistenceMediatorMock);

        StepVerifier.create(result)
                .expectSubscription()
                .expectError(ResourceAlreadyExistsException.class)
                .verify();

        verify(persistenceMediatorMock).findAlreadyExistingResource(any(PetPostRequestBody.class));
    }

    @Test
    void shouldReturnUnsupportedOperationExceptionWhenBulkPersistenceIsExecuted() {
        TransactionRequestWrapper request =
                TransactionRequestWrapper
                        .builder()
                        .withRequestBody(
                                PetPostRequestBody
                                        .builder()
                                        .withName("some-name")
                                        .withStatus("random-status")
                                        .build())
                        .build();

        subject = new PostCommand(request);

        Flux<ResponseBody> result = subject.executeFlux(persistenceMediatorMock);

        StepVerifier.create(result)
                .expectSubscription()
                .expectError(UnsupportedOperationException.class)
                .verify();
    }
}