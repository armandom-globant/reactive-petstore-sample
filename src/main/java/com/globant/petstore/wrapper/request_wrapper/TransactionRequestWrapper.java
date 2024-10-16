package com.globant.petstore.wrapper.request_wrapper;

import com.globant.petstore.model.request.RequestBody;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Pageable;

import java.util.Map;

@Value
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class TransactionRequestWrapper {
    RequestBody requestBody;
    Pageable pageable;
    Map<String, String> requestParameters;
    Map<String, String> pathVariables;

    public Long getVersion() {
        return requestBody.getVersion();
    }


}
