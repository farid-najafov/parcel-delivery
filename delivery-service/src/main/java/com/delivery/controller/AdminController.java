package com.delivery.controller;

import com.delivery.model.req.CreateCourierReq;
import com.delivery.model.req.UpdateDeliveryStatusReq;
import com.delivery.model.resp.CourierResp;
import com.delivery.model.resp.DeliveryOrderResp;
import com.delivery.model.shared.RestResponse;
import com.delivery.service.DeliveryService;
import com.delivery.service.UserService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/admin",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final DeliveryService deliveryService;
    private final UserService userService;

    @PutMapping("/orders/{order-id}/delivery-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Void>> changeDeliveryStatus(
            @PathVariable("order-id") Long orderId,
            @RequestBody UpdateDeliveryStatusReq request) {
        deliveryService.changeDeliveryStatus(orderId, request);
        return ResponseEntity.ok(RestResponse.success());
    }

    @GetMapping("/orders/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<List<DeliveryOrderResp>>> getAllOrders() {
        return ResponseEntity.ok(RestResponse.of(deliveryService.getAllOrders()));
    }

    @PostMapping("orders/{order-id}/courier/{courier-id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Void>> assignOrderToCourier(
            @PathVariable("order-id") Long orderId,
            @PathVariable("courier-id") String courierId) {
        deliveryService.assignOrderToCourier(orderId, courierId);
        return ResponseEntity.ok(RestResponse.success());
    }

    @PostMapping("create-courier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Void>> createCourier(@Valid @RequestBody CreateCourierReq request) {
        String courierId = userService.createCourier(request);
        URI uri = URI.create(String.format("/courier/%s", courierId));
        return ResponseEntity.created(uri).body(RestResponse.success());
    }

    @GetMapping("/couriers/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<List<CourierResp>>> getAllCouriers() {
        return ResponseEntity.ok(RestResponse.of(userService.getAllCouriers()));
    }
}
