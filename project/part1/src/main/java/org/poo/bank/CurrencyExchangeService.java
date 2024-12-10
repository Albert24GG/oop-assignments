package org.poo.bank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class CurrencyExchangeService {
    private record ExchangeRatePair(String from, String to) {
    }

    public final Map<String, Set<String>> conversionGraph = new HashMap<>();
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
     * @throws IllegalArgumentException if the exchange rate is not positive
     */
    public void updateExchangeRate(final String from, final String to, final double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        String fromNormalized = normalizeCurrency(from);
        String toNormalized = normalizeCurrency(to);

        conversionGraph.computeIfAbsent(fromNormalized, k -> new LinkedHashSet<>()).add(toNormalized);
        conversionGraph.computeIfAbsent(toNormalized, k -> new LinkedHashSet<>()).add(fromNormalized);
        exchangeRates.put(new ExchangeRatePair(fromNormalized, toNormalized), rate);
        exchangeRates.put(new ExchangeRatePair(toNormalized, fromNormalized), 1 / rate);
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param from   the currency to convert from
     * @param to     the currency to convert to
     * @param amount the amount to convert
     * @return the converted amount
     * @throws IllegalArgumentException if one of the following occurs:
     *                                  <ul>
     *                                      <li>the currency to convert from is unknown</li>
     *                                      <li>there is no exchange rate between the two currencies</li>
     *                                      <li>the amount is negative</li>
     *                                  </ul>
     */
    public double convert(final String from, final String to, final double amount) {
        String fromNormalized = normalizeCurrency(from);
        String toNormalized = normalizeCurrency(to);

        if (fromNormalized.equals(toNormalized)) {
            return amount;
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!conversionGraph.containsKey(fromNormalized)) {
            throw new IllegalArgumentException("Unknown currency: " + fromNormalized);
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Queue<Double> rates = new LinkedList<>();
        queue.add(fromNormalized);
        rates.add(1.0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            double currentRate = rates.poll();
            visited.add(current);

            if (current.equals(toNormalized)) {
                return amount * currentRate;
            }

            for (String next : conversionGraph.get(current)) {
                if (!visited.contains(next)) {
                    queue.add(next);
                    rates.add(currentRate * exchangeRates.get(new ExchangeRatePair(current, next)));
                }
            }
        }

        throw new IllegalArgumentException("No exchange rate between " + fromNormalized + " and " + toNormalized);
    }

}
