package com.example.wendy.myapp.module;

public class BaseDropdownEntity {
    private String name;
    private String value;

    public BaseDropdownEntity(String _name, String _value) {
        name = _name;
        value = _value;
    }

    public String getName() {
        return name;
    }

    public  String getValue() {
        return value;
    }

    public  String toString() {
        return value;
    }
}
