package com.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {
    public final static String ADMINISTRATOR = "ADMINISTRATOR";
    public final static String USER = "USER";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", unique = true)
    private String roleName;
    @ManyToMany(mappedBy = "roleEntity",targetEntity = UserEntity.class)
    private Set<UserEntity> userEntitySet;
}
