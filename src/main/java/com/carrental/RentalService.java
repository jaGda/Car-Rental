package com.carrental;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RentalService {

    private final Fleet fleet;
    private final Clock clock;
    private final List<Reservation> reservations = new ArrayList<>();

    public RentalService(Fleet fleet, Clock clock) {
        this.fleet = Objects.requireNonNull(fleet, "fleet must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public synchronized Reservation createReservation(CarType carType, LocalDateTime startDateTime, int rentalDays) {
        Objects.requireNonNull(carType, "carType must not be null");
        Objects.requireNonNull(startDateTime, "startDateTime must not be null");

        if (rentalDays < 1) {
            throw new IllegalArgumentException(
                    "Rental period must be at least 1 day, got: " + rentalDays);
        }

        if (startDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException(
                    "Start date/time must not be in the past: " + startDateTime);
        }

        LocalDateTime endDateTime = startDateTime.plusDays(rentalDays);
        int overlapping = countOverlapping(carType, startDateTime, endDateTime);

        if (overlapping >= fleet.getCount(carType)) {
            throw new IllegalStateException(
                    "No " + carType + " cars available for the requested period");
        }

        var reservation = new Reservation(UUID.randomUUID().toString(), carType, startDateTime, rentalDays);
        reservations.add(reservation);
        return reservation;
    }

    public synchronized int queryAvailability(CarType carType, LocalDateTime from, LocalDateTime to) {
        Objects.requireNonNull(carType, "carType must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");

        return fleet.getCount(carType) - countOverlapping(carType, from, to);
    }

    private int countOverlapping(CarType carType, LocalDateTime start, LocalDateTime end) {
        return (int) reservations.stream()
                .filter(r -> r.getCarType() == carType)
                .filter(r -> r.getStartDateTime().isBefore(end) && start.isBefore(r.getEndDateTime()))
                .count();
    }

}
