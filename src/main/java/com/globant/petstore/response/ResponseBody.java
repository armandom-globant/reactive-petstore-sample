package com.globant.petstore.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public interface ResponseBody {


    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getId();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long getVersion();
}
