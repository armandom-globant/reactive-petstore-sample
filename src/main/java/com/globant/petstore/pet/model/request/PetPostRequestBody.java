package com.globant.petstore.pet.model.request;

import com.globant.petstore.model.request.RequestBody;
import com.globant.petstore.pet.model.request.internal.Category;
import com.globant.petstore.pet.model.request.internal.Status;
import com.globant.petstore.pet.model.request.internal.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.Set;

@Value
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PetPostRequestBody implements RequestBody {
    String id;
    @NotNull(message = "name is compulsory")
    String name;
    @NotNull(message = "status is compulsory")
    String status;
    Set<Category> categories;
    Set<Tag> tags;
    @Null(message = "version field not allowed")
    Long version;
}

/*
@Builder(setterPrefix = "with")
public record PetPostRequestBody(
        String id,
        @NotNull(message = "name is compulsory")
        String name,
        @NotNull(message = "status is compulsory")
        String status,
        Set<Category> categories,
        Set<Tag> tags,
        @Null(message = "version field not allowed")
        Long version
) implements RequestBody { }
*/
