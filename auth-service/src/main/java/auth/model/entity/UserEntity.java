package auth.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users", schema = "parcel_delivery")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String fullName;
    private String encryptedPassword;
    private String email;
    private String roles;

    public String[] getRoles() {
        return roles == null || roles.isEmpty() ? new String[] {} : roles.split(":");
    }
}
