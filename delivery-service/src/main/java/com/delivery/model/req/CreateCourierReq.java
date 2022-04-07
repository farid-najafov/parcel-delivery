package com.delivery.model.req;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourierReq {

    @NotNull
    @ApiModelProperty(example = "Jeremy j")
    private String fullName;

    @NotNull
    @Email
    @ApiModelProperty(example = "test2@delivery.com")
    private String email;

    @NotNull
    @ApiModelProperty(example = "example1234")
    private String password;
}
