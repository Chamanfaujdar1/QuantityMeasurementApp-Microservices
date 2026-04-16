package com.chaman.history.service;

import com.chaman.history.entity.QuantityHistoryEntity;
import com.chaman.history.repository.QuantityHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final QuantityHistoryRepository repository;

    public HistoryService(QuantityHistoryRepository repository) {
        this.repository = repository;
    }

    public Page<QuantityHistoryEntity> getHistoryByOperation(String operation, Pageable pageable) {
        return repository.findByOperation(operation.toUpperCase(), pageable);
    }

    public long getOperationCount(String operation) {
        return repository.countByOperationAndErrorFalse(operation.toUpperCase());
    }

    public List<QuantityHistoryEntity> getAllHistory() {
        return repository.findAll();
    }

    public QuantityHistoryEntity saveHistory(QuantityHistoryEntity entity) {
        entity.setId(null); // Ensure fresh insert into history DB
        return repository.save(entity);
    }
}
