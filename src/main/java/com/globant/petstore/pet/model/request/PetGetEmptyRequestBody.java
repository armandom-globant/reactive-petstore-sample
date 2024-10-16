package com.globant.petstore.pet.model.request;

import com.globant.petstore.model.request.RequestBody;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class PetGetEmptyRequestBody implements RequestBody {
}
