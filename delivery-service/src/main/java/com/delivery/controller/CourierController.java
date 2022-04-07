package com.delivery.controller;

import com.delivery.model.req.UpdateDeliveryStatusReq;
import com.delivery.model.resp.DeliveryOrderResp;
import com.delivery.model.shared.RestResponse;
import com.delivery.service.DeliveryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/courier",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CourierController {

    private final DeliveryService deliveryService;

    @GetMapping("/orders")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<RestResponse<List<DeliveryOrderResp>>> getAllOrdersPerCourier() {
        return ResponseEntity.ok(RestResponse.of(deliveryService.getAllOrdersPerCourier()));
    }

    @PutMapping("/orders/{order-id}/delivery-status")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<RestResponse<Void>> changeDeliveryStatus(
            @PathVariable("order-id") Long orderId,
            @RequestBody UpdateDeliveryStatusReq request) {
        deliveryService.changeDeliveryStatusByCourier(orderId, request);
        return ResponseEntity.ok(RestResponse.success());
    }

    @GetMapping("/orders/{order-id}")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<RestResponse<DeliveryOrderResp>> getAllOrdersByOrderId(
            @PathVariable("order-id") Long orderId) {
        return ResponseEntity.ok(RestResponse.of(deliveryService.getOrderById(orderId)));
    }
}
