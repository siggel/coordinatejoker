package com.github.siggel.coordinatejoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntegerRange {

    private List<Integer> values;
    private String text;

    public IntegerRange() {
        values = Collections.emptyList();
    }

    public IntegerRange(String text) {
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        Set<Integer> numbers = new HashSet<>();
        if (text.length() > 0) {
            String[] splits = text.split(",");
            for (String split : splits) {
                String trimmedSplit = split.trim();
                int dashIndex = trimmedSplit.indexOf('-');
                if (dashIndex == 0) {
                    throw new IllegalArgumentException("Negative numbers (" + trimmedSplit + ") are not allowed!");
                } else if (dashIndex == -1) {
                    numbers.add(Integer.parseInt(trimmedSplit));
                } else {
                    int step = 1;
                    int from = Integer.parseInt(trimmedSplit.substring(0, dashIndex).trim());
                    int to;
                    int atIndex = trimmedSplit.indexOf('@');
                    if (atIndex == -1) {
                        to = Integer.parseInt(trimmedSplit.substring(dashIndex + 1).trim());
                    } else {
                        to = Integer.parseInt(trimmedSplit.substring(dashIndex + 1, atIndex).trim());
                        step = Integer.parseInt(trimmedSplit.substring(atIndex + 1).trim());
                    }
                    if (from > to) {
                        throw new IllegalArgumentException("'From' must be smaller than 'to' (" + trimmedSplit + ")!");
                    }
                    if (step < 0) {
                        throw new IllegalArgumentException("Negative step (" + trimmedSplit + ") is not allowed!");
                    }
                    for (int num = from; num <= to; num += step) {
                        numbers.add(num);
                    }
                }
            }
        }

        values = new ArrayList<>(numbers);
        Collections.sort(values);
    }

    public String getText() {
        return text;
    }

    public List<Integer> getValues() {
        return values;
    }
}
