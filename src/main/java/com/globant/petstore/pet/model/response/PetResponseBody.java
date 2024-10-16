package com.globant.petstore.pet.model.response;

import com.globant.petstore.pet.model.request.internal.Category;
import com.globant.petstore.pet.model.request.internal.Status;
import com.globant.petstore.pet.model.request.internal.Tag;
import com.globant.petstore.response.ResponseBody;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

@Value
@Builder(setterPrefix = "with")
public class PetResponseBody implements ResponseBody {

    String id;
    String name;
    String status;
    @Builder.Default
    Set<Category> categories = Collections.emptySet();
    @Builder.Default
    Set<Tag> tags = Collections.emptySet();
    long version;

    public Long getVersion() {
        return version;
    }
}
