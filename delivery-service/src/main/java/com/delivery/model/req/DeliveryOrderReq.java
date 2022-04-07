package com.delivery.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeliveryOrderReq {

    @ApiModelProperty(example = "Philip P")
    private String contactPerson;

    @ApiModelProperty(example = "Amazon warehouse")
    private String source;

    @ApiModelProperty(example = "New Jersey")
    private String destination;

    @ApiModelProperty(example = "Fragile")
    private String description;
}
