package com.config.elasticsearch.ERepositories;

import com.dtos.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface IEProductRepository extends ElasticsearchRepository<ProductDto, Long> {

}
