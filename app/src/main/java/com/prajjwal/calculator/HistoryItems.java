package com.prajjwal.calculator;

public class HistoryItems {
    private String expression, result;

    public HistoryItems(String expression, String result) {
        this.expression = expression;
        this.result = result;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }
}
