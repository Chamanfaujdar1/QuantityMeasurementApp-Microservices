package com.chaman.history.repository;

import com.chaman.history.entity.QuantityHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuantityHistoryRepository extends JpaRepository<QuantityHistoryEntity, Long> {
    Page<QuantityHistoryEntity> findByOperation(String operation, Pageable pageable);
    long countByOperationAndErrorFalse(String operation);
}
