package com.carrental;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class Fleet {

    private final EnumMap<CarType, Integer> stock;

    public Fleet(Map<CarType, Integer> stock) {
        validateStock(stock);
        this.stock = new EnumMap<>(stock);
    }

    public int getCount(CarType type) {
        Objects.requireNonNull(type, "type must not be null");
        return stock.get(type);
    }

    private static void validateStock(Map<CarType, Integer> stock) {
        Objects.requireNonNull(stock, "stock must not be null");

        EnumSet<CarType> missing = EnumSet.allOf(CarType.class);
        missing.removeAll(stock.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Fleet stock must include all car types. Missing: " + missing);
        }

        List<Entry<CarType, Integer>> invalid = stock.entrySet().stream()
                .filter(e -> e.getValue() <= 0)
                .toList();

        if (!invalid.isEmpty()) {
            throw new IllegalArgumentException("Invalid car counts: " + invalid);
        }
    }
}
