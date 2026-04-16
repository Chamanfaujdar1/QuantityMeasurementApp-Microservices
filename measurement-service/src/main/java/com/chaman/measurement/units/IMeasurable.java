package com.chaman.measurement.units;

public interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    double getConversionFactor();
    boolean supportsArithmetic();
}
