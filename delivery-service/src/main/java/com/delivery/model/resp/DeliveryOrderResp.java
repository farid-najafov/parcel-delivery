package com.delivery.model.resp;

import lombok.Data;

@Data
public class DeliveryOrderResp {

    private Long id;
    private String customerId;
    private String courierId;
    private String contactPerson;
    private String source;
    private String destination;
    private String description;
    private String deliveryStatus;
}
