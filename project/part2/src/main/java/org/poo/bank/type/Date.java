package org.poo.bank.type;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

public record Date(@NonNull LocalDate date) {
    /**
     * Creates a Date object.
     *
     * @param date the date string, in the format "yyyy-MM-dd"
     * @return the Date object
     */
    public static Date of(@NonNull final String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Date(LocalDate.parse(date, formatter));
    }

    /**
     * Calculates the period between this date and the end date as a Period.
     * @param endDateExclusive the end date
     * @return the period between this date and the end date
     */
    public Period until(final ChronoLocalDate endDateExclusive) {
        return date.until(endDateExclusive);
    }
}
