package org.poo.bank.log;

import org.poo.bank.type.IBAN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AuditLogService {
    private final Map<IBAN, List<AuditLog>> logs = new HashMap<>();

    /**
     * Records am audit log for a given account.
     *
     * @param account the IBAN of the account to log the transaction for
     * @param log     the log to record
     */
    public void recordLog(final IBAN account,
                          final AuditLog log) {
        logs.computeIfAbsent(account, k -> new ArrayList<>()).add(log);
    }

    /**
     * Gets the transaction logs for an account.
     *
     * @param account the IBAN of the account to get the transaction logs for
     * @return the transaction logs for the account, or an empty list if the account does not exist
     */
    public List<AuditLog> getLogs(final IBAN account) {
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
    public List<AuditLog> getLogs(final IBAN account, final int startTimestamp,
                                  final int endTimestamp) {
        return logs.getOrDefault(account, Collections.emptyList()).stream()
                .filter(log -> log.getTimestamp() >= startTimestamp
                        && log.getTimestamp() <= endTimestamp)
                .toList();
    }
}
