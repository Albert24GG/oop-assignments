package org.poo.bank.currency;

import org.poo.bank.type.Currency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class CurrencyExchangeService {
    private record ExchangeRatePair(Currency from, Currency to) {
    }

    private final Map<Currency, Set<Currency>> conversionGraph = new HashMap<>();
    private final Map<ExchangeRatePair, Double> exchangeRates = new HashMap<>();

    /**
     * Updates the exchange rate between two currencies.
     *
     * @param from the currency to convert from
     * @param to   the currency to convert to
     * @param rate the exchange rate
     * @throws IllegalArgumentException if the exchange rate is not positive
     */
    public void updateExchangeRate(final Currency from, final Currency to,
                                   final double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        conversionGraph.computeIfAbsent(from, k -> new LinkedHashSet<>()).add(to);
        conversionGraph.computeIfAbsent(to, k -> new LinkedHashSet<>()).add(from);
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
     * @throws IllegalArgumentException if one of the following occurs:
     *                                  <ul>
     *                                  <li>the currency to convert from is unknown</li>
     *                                  <li>there is no exchange rate between the two currencies</li>
     *                                  <li>the amount is negative</li>
     *                                  </ul>
     */
    public double convert(final Currency from, final Currency to,
                          final double amount) {
        if (from.equals(to)) {
            return amount;
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!conversionGraph.containsKey(from)) {
            throw new IllegalArgumentException("Unknown currency: " + from);
        }

        Set<Currency> visited = new HashSet<>();
        Queue<Currency> queue = new LinkedList<>();
        Queue<Double> rates = new LinkedList<>();
        queue.add(from);
        rates.add(1.0);

        while (!queue.isEmpty()) {
            Currency current = queue.poll();
            double currentRate = rates.poll();
            visited.add(current);

            if (current.equals(to)) {
                return amount * currentRate;
            }

            for (var next : conversionGraph.get(current)) {
                if (!visited.contains(next)) {
                    queue.add(next);
                    rates.add(currentRate * exchangeRates.get(new ExchangeRatePair(current, next)));
                }
            }
        }

        throw new IllegalArgumentException(
                "No exchange rate between " + from + " and " + to);
    }

}
