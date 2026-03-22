package com.carrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class FleetTest {

    @Test
    void shouldCreateFleetWithValidCounts() {
        Fleet fleet = new Fleet(Map.of(
                CarType.SEDAN, 3, CarType.SUV, 2, CarType.VAN, 1));

        assertEquals(3, fleet.getCount(CarType.SEDAN));
        assertEquals(2, fleet.getCount(CarType.SUV));
        assertEquals(1, fleet.getCount(CarType.VAN));
    }

    @Test
    void shouldRejectNullStock() {
        assertThrows(NullPointerException.class, () -> new Fleet(null));
    }

    @Test
    void shouldRejectMissingCarType() {
        Map<CarType, Integer> incomplete = Map.of(
                CarType.SEDAN, 3, CarType.SUV, 2);

        var ex = assertThrows(IllegalArgumentException.class, () -> new Fleet(incomplete));
        assertTrue(ex.getMessage().contains("VAN"));
    }

    @Test
    void shouldRejectZeroCount() {
        Map<CarType, Integer> withZero = Map.of(
                CarType.SEDAN, 3, CarType.SUV, 0, CarType.VAN, 1);

        var ex = assertThrows(IllegalArgumentException.class, () -> new Fleet(withZero));
        assertTrue(ex.getMessage().contains("SUV=0"));
    }

    @Test
    void shouldRejectNegativeCount() {
        Map<CarType, Integer> withNegative = Map.of(
                CarType.SEDAN, 3, CarType.SUV, 2, CarType.VAN, -5);

        var ex = assertThrows(IllegalArgumentException.class, () -> new Fleet(withNegative));
        assertTrue(ex.getMessage().contains("VAN=-5"));
    }

    @Test
    void shouldRejectNullCarTypeInGetCount() {
        Fleet fleet = new Fleet(Map.of(
                CarType.SEDAN, 1, CarType.SUV, 1, CarType.VAN, 1));

        assertThrows(NullPointerException.class, () -> fleet.getCount(null));
    }
}