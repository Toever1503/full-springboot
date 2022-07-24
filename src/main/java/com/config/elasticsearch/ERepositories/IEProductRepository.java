package com.config.elasticsearch.ERepositories;

import com.dtos.ProductAutoCompletionDto;
import com.dtos.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IEProductRepository extends ElasticsearchRepository<ProductDto, Long> {
    List<ProductDto> findAllByNameLikeOrNameEngLike(String s, String s1, Pageable page);

    @Query("{\"bool\":{\"must\":[{\"match\":{\"categoryId\":\"?0\"}}]}}")
    Page<ProductDto> findByCategoryId(Long categoryId, Pageable page);

    @Query("{\"bool\":{\"must\":[{\"match\":{\"categoryId\":\"?0\"}},{\"match\":{\"name\":\"?1\"}}]}}")
    Page<ProductDto> findByCategoryIdAndName(Long categoryId, String name, Pageable page);

}
