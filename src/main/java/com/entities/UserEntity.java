package com.entities;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
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
    @Column(name = "phone", unique = true)
    private String phone;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "code")
    private String code;
    @Column(name = "status")
    private boolean status;
    @Column(name = "failed_login")
    private int failedLogin;
    @Column(name = "lock_status")
    private boolean lockStatus;
    @Column(name = "main_address")
    private Long mainAddress;
    @Column(name="avatar")
    private String avatar;
    public static final String FOLDER = "user/";

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private Set<Address> myAddress;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roleEntity;

}
