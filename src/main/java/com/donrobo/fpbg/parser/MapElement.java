package com.donrobo.fpbg.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapElement implements Element {

    private final HashMap<String, Element> elements;

    public MapElement(HashMap<String, Element> elements) {
        this.elements = elements;
    }

    public MapElement(MapElement base, MapElement specialization) {
        elements = new HashMap<>();
        base.elements.forEach(elements::put);
        specialization.elements.forEach(elements::put);
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Element get(String key) {
        return elements.get(key);
    }

    public boolean containsKey(String key) {
        return elements.containsKey(key);
    }

    public Set<String> keySet() {
        return elements.keySet();
    }

    public Collection<Element> values() {
        return elements.values();
    }

    public Set<Map.Entry<String, Element>> entrySet() {
        return elements.entrySet();
    }

    public Element getOrDefault(String key, Element defaultValue) {
        return elements.getOrDefault(key, defaultValue);
    }

    public PrimitiveElement getPrimitiveElement(String key) {
        Element element = elements.get(key);
        if (element == null) {
            return null;
        }
        if (!(element instanceof PrimitiveElement)) {
            throw new RuntimeException("Not a primitive element. Is: " + element);
        }
        return (PrimitiveElement) element;
    }

    public String getString(String key) {
        return getPrimitiveElement(key).getStringValue();
    }

    public Integer getInt(String key) {
        PrimitiveElement primitiveElement = getPrimitiveElement(key);
        if (primitiveElement == null) {
            return null;
        }
        return primitiveElement.getIntValue();
    }

    public Double getDouble(String key) {
        PrimitiveElement primitiveElement = getPrimitiveElement(key);
        if (primitiveElement == null) {
            return null;
        }
        return primitiveElement.getDoubleValue();
    }

    public Boolean getBoolean(String key) {
        PrimitiveElement primitiveElement = getPrimitiveElement(key);
        if (primitiveElement == null) {
            return null;
        }
        return primitiveElement.getBooleanValue();
    }

    public HashMap<String, Object> toRawJavaObject() {
        HashMap<String, Object> map = new HashMap<>();

        elements.entrySet().stream().forEach(entry -> {
            Element value = entry.getValue();

            map.put(entry.getKey(), value.toRawJavaObject());
        });

        return map;
    }
}
