package com.aphex.minturassistent.Entities;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MetData {
    private String type;
    private Array geometry;
    private ArrayList<Weather> properties;

    public MetData(String type, Array geometry, ArrayList<Weather> properties) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "MetData{" +
                "type=" +
                "Feature=" +
                ", geometry=" +
                '}';
    }
}
