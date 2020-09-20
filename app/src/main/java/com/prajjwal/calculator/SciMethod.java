package com.prajjwal.calculator;

public class SciMethod {
    private int startIdx, endIdx;
    private String function;
    private boolean rad;

    public SciMethod(int startIdx, int endIdx, String function, boolean rad) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.function = function;
        this.rad = rad;
    }

    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public String getFunction() {
        return function;
    }

    public boolean isRad() {
        return rad;
    }
}
