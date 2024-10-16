package com.globant.petstore.pet.model.request;

import com.globant.petstore.model.request.RequestBody;
import com.globant.petstore.pet.model.request.internal.Category;
import com.globant.petstore.pet.model.request.internal.Status;
import com.globant.petstore.pet.model.request.internal.Tag;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder(setterPrefix = "with")
public class PetPutRequestBody implements RequestBody {
    String name;
    String status;
    Set<Category> categories;
    Set<Tag> tags;
    Long version;
}
