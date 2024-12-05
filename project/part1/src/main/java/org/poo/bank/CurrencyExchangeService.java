package org.poo.bank;

import java.util.HashMap;
import java.util.Map;

public final class CurrencyExchangeService {
    private record ExchangeRatePair(String from, String to) {
    }

    public final Map<ExchangeRatePair, Double> exchangeRates = new HashMap<>();

    /**
     * Updates the exchange rate between two currencies.
     *
     * @param from the currency to convert from
     * @param to   the currency to convert to
     * @param rate the exchange rate
     */
    public void updateExchangeRate(final String from, final String to, final double rate) {
        exchangeRates.put(new ExchangeRatePair(from, to), rate);
        exchangeRates.put(new ExchangeRatePair(to, from), 1 / rate);
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param from   the currency to convert from
     * @param to     the currency to convert to
     * @param amount the amount to convert
     * @return the converted amount
     * @throws IllegalArgumentException if no exchange rate is found
     */
    public double convert(final String from, final String to, final double amount) {
        Double rate = exchangeRates.get(new ExchangeRatePair(from, to));
        if (rate == null) {
            throw new IllegalArgumentException("No exchange rate found for " + from + " to " + to);
        }

        return amount * rate;
    }

}
