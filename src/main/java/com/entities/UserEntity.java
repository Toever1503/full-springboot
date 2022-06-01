package com.entities;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "password")
    private String password;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "code")
    private String code;
    @Column(name = "status")
    private boolean status;
    @Column(name = "failed_login")
    private int failedLogin;
    @Column(name = "lock_status")
    private boolean lock_status;
    @Column(name = "main_address")
    private Long main_address;
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roleEntity;
}
