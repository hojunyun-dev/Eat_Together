package com.example.eat_together.domain.store.repository;


import com.example.eat_together.domain.store.entity.StoreDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, String> {

    Page<StoreDocument> findByNormalizationNameContaining(String keyword, Pageable pageable);
}
