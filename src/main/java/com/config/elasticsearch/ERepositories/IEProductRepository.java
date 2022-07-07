package com.config.elasticsearch.ERepositories;

import com.dtos.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface IEProductRepository extends ElasticsearchRepository<ProductDto, Long> {
    @Query("{\"bool\":{\"must\":[{\"match\":{\"name\":\"?0\"}}]}}")
    Page<ProductDto> findByName(String name, Pageable page);

    @Query("{\"bool\":{\"must\":[{\"match\":{\"categoryId\":\"?0\"}}]}}")
    Page<ProductDto> findByCategoryId(Long categoryId, Pageable page);
}
