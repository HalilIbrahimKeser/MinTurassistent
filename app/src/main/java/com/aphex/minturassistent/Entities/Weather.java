package com.aphex.minturassistent.Entities;

import java.util.List;

public class Weather {
    private double airTemperatureMax;
    private double airTemperatureMin;
    private String symbolCode;

    public Weather(double airTemperatureMax, double airTemperatureMin, String symbolCode) {
        this.airTemperatureMax = airTemperatureMax;
        this.airTemperatureMin = airTemperatureMin;
        this.symbolCode = symbolCode;
    }

    public double getAirTemperatureMax() {
        return airTemperatureMax;
    }

    public double getAirTemperatureMin() {
        return airTemperatureMin;
    }

    public String getSymbolCode() {
        return symbolCode;
    }
}
