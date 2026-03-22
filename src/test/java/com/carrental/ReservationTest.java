package com.carrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private static final String ID = UUID.randomUUID().toString();
    private static final LocalDateTime NOW = LocalDateTime.now();


    @Test
    void shouldCreateReservation() {
        Reservation r = new Reservation(ID, CarType.SUV, NOW, 5);

        assertEquals(ID, r.getId());
        assertEquals(CarType.SUV, r.getCarType());
        assertEquals(NOW, r.getStartDateTime());
        assertEquals(5, r.getRentalDays());
    }

    @Test
    void shouldCalculateEndDateTime() {
        Reservation r = new Reservation(ID, CarType.SEDAN, NOW, 3);

        assertEquals(NOW.plusDays(3), r.getEndDateTime());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(NullPointerException.class,
                () -> new Reservation(null, CarType.SEDAN, NOW, 1));
    }

    @Test
    void shouldRejectNullCarType() {
        assertThrows(NullPointerException.class,
                () -> new Reservation(ID, null, NOW, 1));
    }

    @Test
    void shouldRejectNullStartDateTime() {
        assertThrows(NullPointerException.class,
                () -> new Reservation(ID, CarType.SEDAN, null, 1));
    }
}