package com.globant.petstore.model.request;

public interface RequestBody {
    default Long getVersion() {
        return 0L;
    }
}
