package com.globant.petstore.pet.controller;

import com.globant.petstore.command.GetCommand;
import com.globant.petstore.command.PostCommand;
import com.globant.petstore.command.PutCommand;
import com.globant.petstore.facade.RequestFacade;
import com.globant.petstore.pet.model.request.PetGetEmptyRequestBody;
import com.globant.petstore.pet.model.request.PetPostRequestBody;
import com.globant.petstore.pet.model.request.PetPutRequestBody;
import com.globant.petstore.response.ResponseBody;
import com.globant.petstore.wrapper.request_wrapper.TransactionRequestWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class PetController {
    private final RequestFacade requestFacade;

    @GetMapping("/pet")
    public ResponseEntity<Flux<ResponseBody>> findAll(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "10") int offset,
            @RequestParam(required = false, defaultValue = "name") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ){
        // immutability is crucial for reactive programming
        final TransactionRequestWrapper request = TransactionRequestWrapper
                .builder()
                .withPageable(
                        PageRequest.of(
                                offset,
                                limit,
                                Sort.by(Sort.Direction.fromString(direction), sort)))
                .withRequestBody(PetGetEmptyRequestBody.builder().build())  // use this when mediator gets a real implementation
                .build();
        return requestFacade.processRequestForFlux(new GetCommand(request));
    }

    @GetMapping("/pet/{petId}")
    public Mono<ResponseEntity<ResponseBody>> findById(@PathVariable String petId){
        final TransactionRequestWrapper request = TransactionRequestWrapper
                .builder()
                .withPathVariables(Map.of("id", petId))
                .build();
        return requestFacade.processRequestForMono(new GetCommand(request));
    }

    @PostMapping("/pet")
    public Mono<ResponseEntity<ResponseBody>> saveResource(
            @Valid @RequestBody PetPostRequestBody requestBody
    ){
        final TransactionRequestWrapper request = TransactionRequestWrapper
                .builder()
                .withRequestBody(requestBody)
                .build();
        return requestFacade.processRequestForMono(new PostCommand(request));
    }

    @PutMapping("/pet/{petId}")
    public Mono<ResponseEntity<ResponseBody>> replaceResource(
            @PathVariable String petId,
            @Valid @RequestBody PetPutRequestBody requestBody
    ){
        final TransactionRequestWrapper request = TransactionRequestWrapper
                .builder()
                .withPathVariables(Map.of("id", petId))
                .withRequestBody(requestBody)
                .build();
        return requestFacade.processRequestForMono(new PutCommand(request));
    }

}
