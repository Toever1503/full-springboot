package com.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_address")
public class UserAddress {
    @EmbeddedId
    private UserAddressId id;

    public UserAddressId getId() {
        return id;
    }

    public void setId(UserAddressId id) {
        this.id = id;
    }

//TODO [JPA Buddy] generate columns from DB
}