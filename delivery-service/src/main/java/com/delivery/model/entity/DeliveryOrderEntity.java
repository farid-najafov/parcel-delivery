package com.delivery.model.entity;

import com.delivery.util.DeliveryStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_orders", schema = "parcel_delivery")
public class DeliveryOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    private Long id;
    private String customerId;
    private String courierId;
    private String contactPerson;
    private String source;
    private String destination;
    private String description;
    private String deliveryStatus = DeliveryStatus.NEW.name();
    private LocalDateTime createdAt = LocalDateTime.now();
}
