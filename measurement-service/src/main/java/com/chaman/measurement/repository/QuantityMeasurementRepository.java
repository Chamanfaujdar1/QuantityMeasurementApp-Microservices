package com.chaman.measurement.repository;

import com.chaman.measurement.entity.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {
    List<QuantityMeasurementEntity> findByOperation(String operation);
    long countByOperationAndErrorFalse(String operation);
}
