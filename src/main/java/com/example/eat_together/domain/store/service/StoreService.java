package com.example.eat_together.domain.store.service;

import com.example.eat_together.domain.store.repository.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
}
