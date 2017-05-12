package com.donrobo.fpbg.parser;

public class PrimitiveElement implements Element {

    private final Object value;

    public PrimitiveElement(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new RuntimeException("Wrong value type. Expected: String, was: " + value);
        }
    }

    public int getIntValue() {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new RuntimeException("Wrong value type. Expected: Integer, was: " + value);
        }
    }

    public double getDoubleValue() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new RuntimeException("Wrong value type. Expected: Integer, was: " + value);
        }

    }

    public boolean getBooleanValue() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new RuntimeException("Wrong value type. Expected: Boolean, was: " + value);
        }
    }

    @Override
    public Object toRawJavaObject() {
        return value;
    }
}
