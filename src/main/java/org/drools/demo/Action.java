package org.drools.demo;

public class Action {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Action [value=" + value + "]";
    }
}
