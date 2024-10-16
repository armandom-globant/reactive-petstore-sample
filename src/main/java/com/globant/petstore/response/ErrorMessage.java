package com.globant.petstore.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
public class ErrorMessage implements ResponseBody{
    String message;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Long getVersion() {
        return null;
    }
}
