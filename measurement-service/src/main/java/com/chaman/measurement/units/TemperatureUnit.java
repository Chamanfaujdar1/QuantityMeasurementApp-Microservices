package com.chaman.measurement.units;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS,
    FAHRENHEIT;

    @Override
    public double convertToBaseUnit(double value) {
        if (this == FAHRENHEIT) return (value - 32) * 5.0 / 9.0;
        return value;
    }

    @Override
    public double convertFromBaseUnit(double value) {
        if (this == FAHRENHEIT) return (value * 9.0 / 5.0) + 32;
        return value;
    }

    @Override public String getUnitName()         { return name(); }
    @Override public double getConversionFactor() { return 1; }
    @Override public boolean supportsArithmetic() { return false; }
}
