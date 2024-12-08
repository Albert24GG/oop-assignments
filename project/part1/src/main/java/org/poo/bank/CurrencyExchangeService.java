package org.poo.bank;

import java.util.HashMap;
import java.util.Map;

public final class CurrencyExchangeService {
    private record ExchangeRatePair(String from, String to) {
    }

    public final Map<ExchangeRatePair, Double> exchangeRates = new HashMap<>();

    private static String normalizeCurrency(String currency) {
        return currency.toLowerCase();
    }

    /**
     * Updates the exchange rate between two currencies.
     *
     * @param from the currency to convert from
     * @param to   the currency to convert to
     * @param rate the exchange rate
     */
    public void updateExchangeRate(final String from, final String to, final double rate) {
        String from_ = normalizeCurrency(from);
        String to_ = normalizeCurrency(to);

        exchangeRates.put(new ExchangeRatePair(from_, to_), rate);
        exchangeRates.put(new ExchangeRatePair(to_, from_), 1 / rate);
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
        String from_ = normalizeCurrency(from);
        String to_ = normalizeCurrency(to);

        if (from_.equals(to_)) {
            return amount;
        }

        Double rate = exchangeRates.get(new ExchangeRatePair(from_, to_));
        if (rate == null) {
            throw new IllegalArgumentException(
                    "No exchange rate found for " + from_ + " to " + to_);
        }

        return amount * rate;
    }

}
