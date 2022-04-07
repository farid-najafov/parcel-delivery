package com.delivery.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T> {

    private HttpStatus status;
    private T data;

    public static <T> RestResponse<T> of(T data) {
        return new RestResponse<>(HttpStatus.OK, data);
    }

    public static <T> RestResponse<T> success() {
        return new RestResponse<>(HttpStatus.OK, null);
    }
}