package com.delivery.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "parcel_delivery")
public class UserEntity {

    @Id
    private String id;
    private String fullName;
    private String encryptedPassword;
    private String email;
    private String roles;

    public String[] getRoles() {
        return roles == null || roles.isEmpty() ? new String[] {} : roles.split(":");
    }
}
