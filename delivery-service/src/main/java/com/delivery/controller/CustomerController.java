package com.delivery.controller;

import com.delivery.model.req.ChangeDestinationReq;
import com.delivery.model.req.CreateCustomerReq;
import com.delivery.model.req.DeliveryOrderReq;
import com.delivery.model.resp.DeliveryOrderResp;
import com.delivery.model.shared.RestResponse;
import com.delivery.service.DeliveryService;
import com.delivery.service.UserService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "/customer",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private final DeliveryService deliveryService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<RestResponse<Void>> createCustomer(@Valid CreateCustomerReq request) {
        String customerId = userService.createCustomer(request);
        URI uri = URI.create(String.format("/customer/%s", customerId));
        return ResponseEntity.created(uri).body(RestResponse.success());
    }

    @PostMapping("/orders/create-order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestResponse<DeliveryOrderResp>> createDeliveryOrder(@RequestBody DeliveryOrderReq request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestResponse.of(deliveryService.createDeliveryOrder(request)));
    }

    @PutMapping("/orders/{order-id}/destination")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestResponse<Void>> changeDeliveryAddress(
            @PathVariable("order-id") Long orderId,
            @RequestBody ChangeDestinationReq request) {
        deliveryService.changeDeliveryAddress(orderId, request);
        return ResponseEntity.ok(RestResponse.success());
    }

    @PutMapping("{order-id}/cancellation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestResponse<Void>> cancelDeliveryOrder(@PathVariable("order-id") Long orderId) {
        deliveryService.cancelDeliveryOrder(orderId);
        return ResponseEntity.ok(RestResponse.success());
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RestResponse<List<DeliveryOrderResp>>> getAllOrdersPerCustomer() {
        return ResponseEntity.ok(RestResponse.of(deliveryService.getAllOrdersPerCustomer()));
    }
}
