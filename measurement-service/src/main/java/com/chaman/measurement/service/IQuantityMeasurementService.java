package com.chaman.measurement.service;

import com.chaman.measurement.dto.QuantityInputDTO;
import com.chaman.measurement.entity.QuantityMeasurementEntity;

public interface IQuantityMeasurementService {
    QuantityMeasurementEntity compare(QuantityInputDTO input);
    QuantityMeasurementEntity convert(QuantityInputDTO input);
    QuantityMeasurementEntity add(QuantityInputDTO input);
    QuantityMeasurementEntity subtract(QuantityInputDTO input);
    QuantityMeasurementEntity divide(QuantityInputDTO input);
}
