package com.delivery.model.resp;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourierResp {

    private String id;
    private String fullName;
    private String email;
    private List<DeliveryOrderResp> deliveryOrders;
}
