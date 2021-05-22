package com.aphex.minturassistent.Entities;

import java.util.ArrayList;

public class MetData {
    private ArrayList<MetData> results;
    private int response_code;

    public MetData() {
    }

    public int getResponse_code() {
        return response_code;
    }
    public ArrayList<MetData> getResults() {
        return results;
    }

}
