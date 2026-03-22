package com.carrental;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {

    private final String id;
    private final CarType carType;
    private final LocalDateTime startDateTime;
    private final int rentalDays;

    public Reservation(String id, CarType carType, LocalDateTime startDateTime, int rentalDays) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.startDateTime = Objects.requireNonNull(startDateTime, "startDateTime must not be null");
        this.rentalDays = rentalDays;
    }

    public String getId() {
        return id;
    }

    public CarType getCarType() {
        return carType;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public LocalDateTime getEndDateTime() {
        return startDateTime.plusDays(rentalDays);
    }
}
