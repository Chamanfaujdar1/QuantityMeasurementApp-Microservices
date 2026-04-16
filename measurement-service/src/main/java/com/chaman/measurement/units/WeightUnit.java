package com.chaman.measurement.units;

public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override public double convertToBaseUnit(double value)    { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base)   { return base / conversionFactor; }
    @Override public String getUnitName()                      { return name(); }
    @Override public double getConversionFactor()              { return conversionFactor; }
    @Override public boolean supportsArithmetic()              { return true; }
}
