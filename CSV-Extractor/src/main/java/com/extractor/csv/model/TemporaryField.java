package com.extractor.csv.model;

public class TemporaryField {
    private String Name;
    private String Value;

    public void setName(String content) {
        this.Name = content;
    }

    public void setValue(String content) {
        this.Value = content;
    }

    public String getName() {
        return this.Name;
    }

    public String getValue() {
        return this.Value;
    }

    public void clear() {
        this.Name = null;
        this.Value = null;
    }

    public Boolean isEmpty() {
        return this.Name == null && this.Value == null;
    }
}