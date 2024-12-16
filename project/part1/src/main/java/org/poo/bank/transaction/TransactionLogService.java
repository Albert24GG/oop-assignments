package org.poo.bank.transaction;

import lombok.NonNull;
import org.poo.bank.type.IBAN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TransactionLogService {
    private final Map<IBAN, List<TransactionLog>> logs = new HashMap<>();

    /**
     * Logs a transaction.
     *
     * @param account the IBAN of the account to log the transaction for
     * @param log     the transaction log to log
     */
    public void logTransaction(final IBAN account,
                               @NonNull final TransactionLog log) {
        logs.computeIfAbsent(account, k -> new ArrayList<>()).add(log);
    }

    /**
     * Gets the transaction logs for an account.
     *
     * @param account the IBAN of the account to get the transaction logs for
     * @return the transaction logs for the account, or an empty list if the account does not exist
     */
    public List<TransactionLog> getLogs(final IBAN account) {
        return List.copyOf(logs.getOrDefault(account, Collections.emptyList()));
    }

    /**
     * Gets the transaction logs for an account within a time range.
     *
     * @param account        the IBAN of the account to get the transaction logs for
     * @param startTimestamp the start timestamp
     * @param endTimestamp   the end timestamp
     * @return the transaction logs for the account within the time range, or an empty list if the
     * account does not exist
     */
    public List<TransactionLog> getLogs(final IBAN account, int startTimestamp, int endTimestamp) {
        return logs.getOrDefault(account, Collections.emptyList()).stream()
                .filter(log -> log.getTimestamp() >= startTimestamp &&
                        log.getTimestamp() <= endTimestamp)
                .toList();
    }

    /**
     * Removes the transaction logs for an account.
     *
     * @param account the IBAN of the account to remove the transaction logs for
     * @return the transaction logs for the account, or{@code null} if the account does not exist
     */
    public List<TransactionLog> removeLogs(final IBAN account) {
        return logs.remove(account);
    }
}
