package com.config.elasticsearch.ERepositories;

import com.dtos.DetailIndustryDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface IEIndustryRepository extends ElasticsearchRepository<DetailIndustryDto, Long> {

    Optional<DetailIndustryDto> findBySlug(String slug);

}
