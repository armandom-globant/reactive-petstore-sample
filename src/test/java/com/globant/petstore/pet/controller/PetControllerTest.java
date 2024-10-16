package com.globant.petstore.pet.controller;


import com.globant.petstore.command.RestfulApiCommand;
import com.globant.petstore.command.exception.ResourceNotFoundException;
import com.globant.petstore.facade.RequestFacade;
import com.globant.petstore.pet.model.response.PetResponseBody;
import com.globant.petstore.response.ErrorMessage;
import com.globant.petstore.response.ResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = PetController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class PetControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RequestFacade requestFacadeMock;

    @Test
    void shouldReturnHttpOkAndBodyWhenListingAllItemsHasTwoItems() {

        when(requestFacadeMock.processRequestForFlux(any(RestfulApiCommand.class)))
                .thenReturn(
                        ResponseEntity.ok(
                                Flux.just(
                                        PetResponseBody.builder().build(),
                                        PetResponseBody.builder().build()
                                )
                        ));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/pet").build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(PetResponseBody.class)
                .hasSize(2);

        verify(requestFacadeMock, only()).processRequestForFlux(any(RestfulApiCommand.class));
    }

    @Test
    void shouldReturnHttpNotFoundAndErrorBodyMessageWhenListingAllItemsThatHasNoItems() {

        when(requestFacadeMock.processRequestForFlux(any(RestfulApiCommand.class)))
                .thenReturn(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Flux.just(ErrorMessage.builder().withMessage("Resource does not exist.").build())));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/pet").build())
                .exchange()
                .expectStatus()
                .isNotFound()
                //.expectBody(ErrorMessage.class)
                //.contains(ErrorMessage.builder().withMessage("Resource does not exist.").build())
        ;

        verify(requestFacadeMock, only()).processRequestForFlux(any(RestfulApiCommand.class));
    }


}