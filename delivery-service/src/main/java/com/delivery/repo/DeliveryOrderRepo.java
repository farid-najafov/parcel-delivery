package com.delivery.repo;

import com.delivery.model.entity.DeliveryOrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryOrderRepo extends JpaRepository<DeliveryOrderEntity, Long> {

    List<DeliveryOrderEntity> findAllByCustomerId(String customerId);

    List<DeliveryOrderEntity> findAllByCourierId(String courierId);

    Optional<DeliveryOrderEntity> findByIdAndCourierId(Long id, String courierId);
}
