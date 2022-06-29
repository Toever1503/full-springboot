package com.config.elasticsearch.ERepositories;

import com.dtos.ProductDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EProductRepository extends ElasticsearchRepository<ProductDto, Long> {
}
