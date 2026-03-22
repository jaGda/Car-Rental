package com.carrental;

import static java.lang.IO.print;
import static java.lang.IO.println;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.Properties;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static final String FLEET_PROPERTIES = "fleet.properties";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static void main() {

        Scanner scanner = new Scanner(System.in);

        println("=== Car Rental System ===");

        Fleet fleet = loadFleet();
        RentalService service = new RentalService(fleet, Clock.systemDefaultZone());

        println("Fleet loaded from " + FLEET_PROPERTIES + ":");
        for (CarType type : CarType.values()) {
            println("  " + type + ": " + fleet.getCount(type));
        }
        println("\nAvailable commands:");
        printHelp();

        boolean running = true;
        while (running) {
            print("\n> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "reserve" -> handleReserve(scanner, service);
                case "availability" -> handleAvailability(scanner, service);
                case "help" -> printHelp();
                case "quit", "exit" -> running = false;
                default -> println("Unknown command. Type 'help' for options.");
            }
        }

        println("Bye!");
    }

    private static Fleet loadFleet() {
        Properties props = new Properties();

        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(FLEET_PROPERTIES)) {
            if (is == null) {
                throw new IllegalStateException(String.join(FLEET_PROPERTIES, " not found on classpath"));
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(String.join("Failed to load ", FLEET_PROPERTIES), e);
        }

        var counts = new EnumMap<CarType, Integer>(CarType.class);
        for (CarType type : CarType.values()) {
            String value = props.getProperty(type.name());
            if (value == null) {
                throw new IllegalStateException("Missing fleet count for: " + type);
            }
            counts.put(type, Integer.parseInt(value.trim()));
        }
        return new Fleet(counts);
    }

    private static void printHelp() {
        println("  reserve      - Make a new reservation");
        println("  availability - Check car availability");
        println("  help         - Show this message");
        println("  quit         - Exit");
    }

    private static void handleReserve(Scanner scanner, RentalService service) {
        try {
            CarType type = readCarType(scanner);
            LocalDateTime start = readDateTime(scanner, "Start date/time (yyyy-MM-dd HH:mm): ");
            int days = readPositiveInt(scanner);

            Reservation r = service.createReservation(type, start, days);
            println("Reservation confirmed!");
            println("  ID:    " + r.getId());
            println("  Type:  " + r.getCarType());
            println("  From:  " + r.getStartDateTime().format(FORMATTER));
            println("  To:    " + r.getEndDateTime().format(FORMATTER));
        } catch (IllegalArgumentException | IllegalStateException e) {
            println("Error: " + e.getMessage());
        }
    }

    private static CarType readCarType(Scanner scanner) {
        print("Car type (SEDAN, SUV, VAN): ");
        return CarType.valueOf(scanner.nextLine().trim().toUpperCase());
    }

    private static LocalDateTime readDateTime(Scanner scanner, String prompt) {
        print(prompt);
        try {
            return LocalDateTime.parse(scanner.nextLine().trim(), FORMATTER);
        } catch (DateTimeParseException _) {
            throw new IllegalArgumentException("Invalid date format. Use: yyyy-MM-dd HH:mm");
        }
    }

    private static int readPositiveInt(Scanner scanner) {
        print("Duration (days): ");
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            if (value < 1) {
                throw new IllegalArgumentException("Must be at least 1");
            }
            return value;
        } catch (NumberFormatException _) {
            throw new IllegalArgumentException("Invalid number");
        }
    }

    private static void handleAvailability(Scanner scanner, RentalService service) {
        try {
            CarType type = readCarType(scanner);
            LocalDateTime from = readDateTime(scanner, "From (yyyy-MM-dd HH:mm): ");
            LocalDateTime to = readDateTime(scanner, "To   (yyyy-MM-dd HH:mm): ");

            int available = service.queryAvailability(type, from, to);
            println("Available " + type + " cars: " + available);
        } catch (IllegalArgumentException e) {
            println("Error: " + e.getMessage());
        }
    }
}
