package com.example.credhubber.domain;

import java.util.HashMap;
import java.util.Map;

public class Secret {

    public String name;
    public Map<String,Object> value;

    public Secret() {
        name = "nil";
        value = new HashMap<String, Object>();
    }

    public Secret( String name, Map<String,Object> value ) {
        this.name = name;
        this.value = value;
    }
}
