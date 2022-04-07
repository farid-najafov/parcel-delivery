package com.delivery.model.req;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCustomerReq {

    @NotNull
    @ApiModelProperty(example = "Peter P")
    private String fullName;

    @NotNull
    @Email
    @ApiModelProperty(example = "test@delivery.com")
    private String email;

    @NotNull
    @ApiModelProperty(example = "example123")
    private String password;
}
