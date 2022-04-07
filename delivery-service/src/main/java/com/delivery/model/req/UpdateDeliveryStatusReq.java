package com.delivery.model.req;

import com.delivery.util.DeliveryStatus;
import lombok.Data;

@Data
public class UpdateDeliveryStatusReq {

    private DeliveryStatus deliveryStatus;
}
