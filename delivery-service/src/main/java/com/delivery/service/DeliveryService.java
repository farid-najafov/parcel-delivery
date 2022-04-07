package com.delivery.service;

import com.delivery.exception.AddressChangeNotAllowedException;
import com.delivery.exception.RecordNotFoundException;
import com.delivery.model.XUserDetails;
import com.delivery.model.entity.DeliveryOrderEntity;
import com.delivery.model.req.ChangeDestinationReq;
import com.delivery.model.req.DeliveryOrderReq;
import com.delivery.model.req.UpdateDeliveryStatusReq;
import com.delivery.model.resp.DeliveryOrderResp;
import com.delivery.repo.DeliveryOrderRepo;
import com.delivery.util.DeliveryStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryOrderRepo deliveryOrderRepo;
    private final ModelMapper mapper;

    private static String getUserId() {
        XUserDetails principal = (XUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getId();
    }

    private DeliveryOrderEntity save(DeliveryOrderEntity entity) {
        DeliveryOrderEntity saved = deliveryOrderRepo.save(entity);
        log.info("saved delivery order: {}", saved);
        return saved;
    }

    public DeliveryOrderResp createDeliveryOrder(DeliveryOrderReq request) {
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DeliveryOrderEntity entity = mapper.map(request, DeliveryOrderEntity.class);
        entity.setCustomerId(getUserId());
        DeliveryOrderEntity saved = save(entity);
        return mapper.map(saved, DeliveryOrderResp.class);
    }

    public void changeDeliveryAddress(Long orderId, ChangeDestinationReq request) {
        DeliveryOrderEntity entity = findDeliveryOrderById(orderId);
        if (!DeliveryStatus.NEW.name().equals(entity.getDeliveryStatus())) {
            throw new AddressChangeNotAllowedException(String.format(
                    "destination of orders with status %s is not allowed to be changed", entity.getDeliveryStatus()));
        }
        entity.setDestination(request.getDestination());
        save(entity);
    }

    public void cancelDeliveryOrder(Long orderId) {
        DeliveryOrderEntity entity = findDeliveryOrderById(orderId);
        entity.setDeliveryStatus(DeliveryStatus.CANCELLED.name());
        save(entity);
    }

    public DeliveryOrderEntity findDeliveryOrderById(Long orderId) {
        DeliveryOrderEntity entity = deliveryOrderRepo.findById(orderId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("delivery order with id %s is not found", orderId)));
        log.info("found delivery order: {}", entity);
        return entity;
    }

    public List<DeliveryOrderResp> getAllOrdersPerCustomer() {
        List<DeliveryOrderEntity> orderEntities = deliveryOrderRepo.findAllByCustomerId(getUserId());
        log.info("found delivery orders per customer: {}", orderEntities);
        return orderEntities.stream().map(el -> mapper.map(el, DeliveryOrderResp.class)).collect(Collectors.toList());
    }

    public List<DeliveryOrderResp> getAllOrdersPerCourier(String courierId) {
        List<DeliveryOrderEntity> orderEntities = deliveryOrderRepo.findAllByCourierId(courierId);
        log.info("found delivery orders per courier: {}", orderEntities);
        return orderEntities.stream().map(el -> mapper.map(el, DeliveryOrderResp.class)).collect(Collectors.toList());
    }

    public List<DeliveryOrderResp> getAllOrdersPerCourier() {
        List<DeliveryOrderEntity> orderEntities = deliveryOrderRepo.findAllByCourierId(getUserId());
        log.info("found delivery orders per courier: {}", orderEntities);
        return orderEntities.stream().map(el -> mapper.map(el, DeliveryOrderResp.class)).collect(Collectors.toList());
    }

    public void changeDeliveryStatus(Long orderId, UpdateDeliveryStatusReq request) {
        DeliveryOrderEntity entity = findDeliveryOrderById(orderId);
        entity.setDeliveryStatus(request.getDeliveryStatus().name());
        save(entity);
    }

    public void changeDeliveryStatusByCourier(Long orderId, UpdateDeliveryStatusReq request) {
        String courierId = getUserId();
        deliveryOrderRepo.findByIdAndCourierId(orderId, courierId)
                .map(el -> {
                    el.setDeliveryStatus(request.getDeliveryStatus().name());
                    save(el);
                    return el;
                }).orElseThrow(() -> new RecordNotFoundException(
                        String.format("delivery order with id %s courierId %s is not found", orderId, courierId)));
    }

    public List<DeliveryOrderResp> getAllOrders() {
        List<DeliveryOrderEntity> orderEntities = deliveryOrderRepo.findAll();
        log.info("found delivery orders: {}", orderEntities);
        return orderEntities.stream().map(el -> mapper.map(el, DeliveryOrderResp.class)).collect(Collectors.toList());
    }

    public void assignOrderToCourier(Long orderId, String courierId) {
        DeliveryOrderEntity entity = findDeliveryOrderById(orderId);
        entity.setCourierId(courierId);
        save(entity);
    }

    public DeliveryOrderResp getOrderById(Long orderId) {
        String courierId = getUserId();
        return deliveryOrderRepo.findByIdAndCourierId(orderId, courierId)
                .map(el -> mapper.map(el, DeliveryOrderResp.class))
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("delivery order with id %s courierId %s is not found", orderId, courierId)));
    }
}
