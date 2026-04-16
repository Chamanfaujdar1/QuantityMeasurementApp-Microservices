package com.chaman.history.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only view of the quantity table written by measurement-service.
 * In dev both services share the same H2 in-memory DB URL (quantitydb).
 * In prod they point to the same MySQL quantitydb schema.
 */
@Entity
@Table(name = "quantity")
@Data
@NoArgsConstructor
public class QuantityHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double thisValue;
    private String thisUnit;
    private String thisMeasurementType;

    private Double thatValue;
    private String thatUnit;
    private String thatMeasurementType;

    private String operation;

    private Double resultValue;
    private String resultUnit;
    private String resultString;

    private boolean error;
    private String errorMessage;

    private LocalDateTime createdAt;
}
