package com.donrobo.fpbg.parser;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ArrayElement implements Element, Iterable<Element> {

    @NotNull
    private final List<Element> elements;

    public ArrayElement(@NotNull List<Element> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Elements must not be null!");
        }
        this.elements = elements;
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public Element get(int index) {
        return elements.get(index);
    }

    public Stream<Element> stream() {
        return elements.stream();
    }

    public Stream<Element> parallelStream() {
        return elements.parallelStream();
    }

    public void forEach(Consumer<? super Element> action) {
        elements.forEach(action);
    }

    @Override
    public Object toRawJavaObject() {
        List<Object> list = new ArrayList<>();

        elements.stream().map(Element::toRawJavaObject).forEach(list::add);

        return list;
    }
}
