package com.repositories;

import com.entities.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBannerRepository extends JpaRepository<BannerEntity, Long> {

}
