package com.chaman.measurement.service;

import com.chaman.measurement.dto.QuantityDTO;
import com.chaman.measurement.dto.QuantityInputDTO;
import com.chaman.measurement.entity.QuantityMeasurementEntity;
import com.chaman.measurement.repository.QuantityMeasurementRepository;
import com.chaman.measurement.units.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private final QuantityMeasurementRepository repository;
    private final RestTemplate restTemplate;

    @Value("${HISTORY_SERVICE_HOST:localhost}")
    private String historyServiceHost;

    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    private void syncToHistory(QuantityMeasurementEntity entity) {
        try {
            String url = "http://" + historyServiceHost + ":8083/api/v1/quantities/history";
            restTemplate.postForObject(url, entity, Object.class);
        } catch (Exception e) {
            // Log and continue, we don't want to fail the measurement if history fails
            System.err.println("Failed to sync history: " + e.getMessage());
        }
    }

    private IMeasurable getUnit(String measurementType, String unit) {
        unit = unit.toUpperCase();
        return switch (measurementType.toUpperCase()) {
            case "LENGTH"      -> LengthUnit.valueOf(unit);
            case "WEIGHT"      -> WeightUnit.valueOf(unit);
            case "VOLUME"      -> VolumeUnit.valueOf(unit);
            case "TEMPERATURE" -> TemperatureUnit.valueOf(unit);
            default -> throw new IllegalArgumentException("Invalid measurement type: " + measurementType);
        };
    }

    @Override
    public QuantityMeasurementEntity compare(QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
        IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());

        double base1 = unit1.convertToBaseUnit(q1.getValue());
        double base2 = unit2.convertToBaseUnit(q2.getValue());

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        setCommonFields(entity, input);
        entity.setOperation("COMPARE");
        entity.setCreatedAt(LocalDateTime.now());
        QuantityMeasurementEntity saved = repository.save(entity);
        syncToHistory(saved);
        return saved;
    }

    @Override
    public QuantityMeasurementEntity convert(QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        if (q1 == null || q2 == null) throw new IllegalArgumentException("Invalid input: both quantities required");

        IMeasurable fromUnit = getUnit(q1.getMeasurementType(), q1.getUnit());
        IMeasurable toUnit   = getUnit(q2.getMeasurementType(), q2.getUnit());

        double base  = fromUnit.convertToBaseUnit(q1.getValue());
        double result = toUnit.convertFromBaseUnit(base);

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        setCommonFields(entity, input);
        entity.setOperation("CONVERT");
        entity.setResultValue(result);
        entity.setCreatedAt(LocalDateTime.now());
        QuantityMeasurementEntity saved = repository.save(entity);
        syncToHistory(saved);
        return saved;
    }

    @Override
    public QuantityMeasurementEntity add(QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
        IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());

        requireArithmetic(unit1, unit2);

        double result = unit1.convertFromBaseUnit(
                unit1.convertToBaseUnit(q1.getValue()) + unit2.convertToBaseUnit(q2.getValue())
        );

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        setCommonFields(entity, input);
        entity.setOperation("ADD");
        entity.setResultValue(result);
        entity.setResultUnit(q1.getUnit());
        entity.setCreatedAt(LocalDateTime.now());
        QuantityMeasurementEntity saved = repository.save(entity);
        syncToHistory(saved);
        return saved;
    }

    @Override
    public QuantityMeasurementEntity subtract(QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
        IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());

        requireArithmetic(unit1, unit2);

        double result = unit1.convertFromBaseUnit(
                unit1.convertToBaseUnit(q1.getValue()) - unit2.convertToBaseUnit(q2.getValue())
        );

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        setCommonFields(entity, input);
        entity.setOperation("SUBTRACT");
        entity.setResultValue(result);
        entity.setResultUnit(q1.getUnit());
        entity.setCreatedAt(LocalDateTime.now());
        QuantityMeasurementEntity saved = repository.save(entity);
        syncToHistory(saved);
        return saved;
    }

    @Override
    public QuantityMeasurementEntity divide(QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        IMeasurable unit1 = getUnit(q1.getMeasurementType(), q1.getUnit());
        IMeasurable unit2 = getUnit(q2.getMeasurementType(), q2.getUnit());

        requireArithmetic(unit1, unit2);

        double base2 = unit2.convertToBaseUnit(q2.getValue());
        if (base2 == 0) throw new ArithmeticException("Cannot divide by zero");

        double result = unit1.convertToBaseUnit(q1.getValue()) / base2;

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        setCommonFields(entity, input);
        entity.setOperation("DIVIDE");
        entity.setResultValue(result);
        entity.setCreatedAt(LocalDateTime.now());
        QuantityMeasurementEntity saved = repository.save(entity);
        syncToHistory(saved);
        return saved;
    }

    // ---------- helpers ----------

    private void requireArithmetic(IMeasurable u1, IMeasurable u2) {
        if (!u1.supportsArithmetic() || !u2.supportsArithmetic())
            throw new UnsupportedOperationException("Arithmetic not supported for this unit type");
    }

    private void setCommonFields(QuantityMeasurementEntity entity, QuantityInputDTO input) {
        QuantityDTO q1 = input.getThisQuantityDTO();
        QuantityDTO q2 = input.getThatQuantityDTO();

        entity.setThisValue(q1.getValue());
        entity.setThisUnit(q1.getUnit());
        entity.setThisMeasurementType(q1.getMeasurementType());

        if (q2 != null) {
            entity.setThatValue(q2.getValue());
            entity.setThatUnit(q2.getUnit());
            entity.setThatMeasurementType(q2.getMeasurementType());
        }
    }
}
