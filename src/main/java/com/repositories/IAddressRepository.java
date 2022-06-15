package com.repositories;

import com.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IAddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    Set<Address> findAllByUser_Id(Long uid);
}
