package com.delivery.service;

import com.delivery.model.XUserDetails;
import com.delivery.model.entity.UserEntity;
import com.delivery.model.req.CreateCourierReq;
import com.delivery.model.req.CreateCustomerReq;
import com.delivery.model.resp.CourierResp;
import com.delivery.repo.UserRepo;
import com.delivery.util.Roles;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;
    private final DeliveryService deliveryService;
    private final ModelMapper mapper;

    public static XUserDetails mapperToXUser(UserEntity userEntity) {
        return new XUserDetails(
                userEntity.getId(),
                userEntity.getFullName(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmail(),
                userEntity.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .map(UserService::mapperToXUser)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with email %s is not found in our DB ", email)));
    }

    public UserDetails loadUserById(String id) {
        return userRepo.findById(id)
                .map(UserService::mapperToXUser)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with id %s is not found in our DB", id)));
    }

    public String createCustomer(CreateCustomerReq request) {
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .encryptedPassword(encoder.encode(request.getPassword()))
                .roles(Roles.CUSTOMER.name())
                .build();
        UserEntity saved = userRepo.save(userEntity);
        log.info("saved customer: {}", saved);
        return userEntity.getId();
    }

    public String createCourier(CreateCourierReq request) {
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .encryptedPassword(encoder.encode(request.getPassword()))
                .roles(Roles.COURIER.name())
                .build();
        UserEntity saved = userRepo.save(userEntity);
        log.info("saved courier: {}", saved);
        return userEntity.getId();
    }

    public List<CourierResp> getAllCouriers() {
        List<UserEntity> courierEntities = userRepo.findAllByRolesLike(Roles.COURIER.name());

        List<CourierResp> couriers = courierEntities
                .stream()
                .map(el -> mapper.map(el, CourierResp.class))
                .collect(Collectors.toList());

        couriers.forEach(el -> el.getDeliveryOrders().addAll(deliveryService.getAllOrdersPerCourier(el.getId())));
        return couriers;
    }
}
