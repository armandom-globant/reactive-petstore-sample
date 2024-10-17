package com.globant.petstore.mediator.strategies.pet;

import com.globant.petstore.model.request.RequestBody;
import com.globant.petstore.pet.model.request.PetPostRequestBody;
import com.globant.petstore.pet.model.request.PetPutRequestBody;
import com.globant.petstore.pet.model.response.PetResponseBody;
import com.globant.petstore.response.ResponseBody;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class PetRepository {

    public List<ResponseBody> findAll(Pageable pageable) {
        return Stream
                .generate(() ->
                        PetResponseBody
                                .builder()
                                .withId("Random id: " + UUID.randomUUID())
                                .withName(LocalDateTime.now().toString())
                                .build())
                .limit(pageable.getPageSize())
                .collect(Collectors.toUnmodifiableList());
    }

    public ResponseBody findById(Map<String, String> pathVariables) {
        return PetResponseBody
                .builder()
                .withId(pathVariables.getOrDefault("id", UUID.randomUUID().toString()))
                .withName(LocalDateTime.now().toString())
                .build();
    }


    public ResponseBody save(RequestBody requestBody) {

        if (requestBody instanceof PetPostRequestBody request) {
            return PetResponseBody
                    .builder()
                    .withId(request.getId())
                    .withName(request.getName())
                    .withCategories(request.getCategories())
                    .withStatus(request.getStatus())
                    .withTags(request.getTags())
                    .withVersion(1L)
                    .build();
        }
        throw new ClassCastException("Not the PetPostRequestBody class that was expected");
    }


    public ResponseBody replace(Map<String, String> pathVariables, RequestBody requestBody) {

        if (requestBody instanceof PetPutRequestBody request) {
            return PetResponseBody
                    .builder()
                    .withId(pathVariables.get("id"))
                    .withName(request.getName())
                    .withCategories(request.getCategories())
                    .withStatus(request.getStatus())
                    .withTags(request.getTags())
                    .withVersion(request.getVersion() + 1)
                    .build();
        }
        throw new ClassCastException("Not the PetPostRequestBody class that was expected");
    }


}
