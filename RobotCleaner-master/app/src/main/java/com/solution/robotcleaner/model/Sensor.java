package com.solution.robotcleaner.model;


public class Sensor {
    private String temperature;
    private String gas;
    private String water;
    private String carbonMonoxide;
    private String methane;
    private String sulphur;

    public Sensor() {
    }

    public String getTemperature() {
        return temperature;
    }

    public String getGas() {
        return gas;
    }

    public String getWater() {
        return water;
    }

    public String getCarbonMonoxide() {
        return carbonMonoxide;
    }

    public String getMethane() {
        return methane;
    }

    public String getSulphur() {
        return sulphur;
    }
}
