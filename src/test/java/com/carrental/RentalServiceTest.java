package com.carrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RentalServiceTest {

    private static final Instant INSTANT_NOW = Instant.now();
    private static final LocalDateTime NOW = LocalDateTime.ofInstant(INSTANT_NOW, ZoneId.systemDefault());
    private static final Clock FIXED_CLOCK = Clock.fixed(INSTANT_NOW, ZoneId.systemDefault());


    private RentalService service;

    @BeforeEach
    void setUp() {
        Fleet fleet = new Fleet(Map.of(
                CarType.SEDAN, 2,
                CarType.SUV, 1,
                CarType.VAN, 1
        ));
        service = new RentalService(fleet, FIXED_CLOCK);
    }

    @Test
    void shouldAcceptReservationStartingNow() {
        Reservation r = service.createReservation(CarType.SEDAN, LocalDateTime.now(), 1);

        assertNotNull(r);
        assertEquals(CarType.SEDAN, r.getCarType());
    }

    @Test
    void shouldRejectReservationStartingOneNanosecondBeforeNow() {
        LocalDateTime justBeforeNow = NOW.minusNanos(1);

        assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(CarType.SEDAN, justBeforeNow, 1));
    }

    @Test
    void shouldAcceptReservationWithExactlyOneDay() {
        Reservation r = service.createReservation(CarType.SUV, NOW.plusDays(1), 1);

        assertEquals(1, r.getRentalDays());
    }

    @Test
    void shouldRejectReservationWithZeroDays() {
        LocalDateTime startDateTime = NOW.plusDays(1);

        assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(CarType.SEDAN, startDateTime, 0));
    }

    @Test
    void shouldAcceptTwoReservationsWhenOneEndsExactlyWhenOtherStarts() {
        LocalDateTime start1 = NOW.plusDays(10);
        LocalDateTime start2 = NOW.plusDays(11);

        Reservation r1 = service.createReservation(CarType.SUV, start1, 1);
        Reservation r2 = service.createReservation(CarType.SUV, start2, 1);

        assertNotNull(r1);
        assertNotNull(r2);
    }

    @Test
    void shouldReturnFullFleetCountWhenNoReservationsExist() {
        int available = service.queryAvailability(
                CarType.SEDAN, NOW.plusDays(1), NOW.plusDays(5));
        assertEquals(2, available);
    }

    @Test
    void shouldReturnZeroAvailabilityWhenAllSlotsAreBooked() {
        LocalDateTime start = NOW.plusDays(1);
        service.createReservation(CarType.SEDAN, start, 3);
        service.createReservation(CarType.SEDAN, start, 3);

        int available = service.queryAvailability(CarType.SEDAN, start, start.plusDays(3));
        assertEquals(0, available);
    }

    @Test
    void shouldRejectReservationWhenNoAvailability() {
        LocalDateTime start = NOW.plusDays(1);
        service.createReservation(CarType.VAN, start, 5);

        assertThrows(IllegalStateException.class,
                () -> service.createReservation(CarType.VAN, start, 3));
    }

}