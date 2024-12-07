package org.poo.bank.transaction;

import lombok.NonNull;
import org.poo.bank.account.BankAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TransactionService {
    /**
     * Map of bank accounts(IBANs) to their transaction logs.
     */
    private final Map<String, List<TransactionLog>> logs = new HashMap<>();

    /**
     * Logs a transaction.
     *
     * @param account the IBAN of the account to log the transaction for
     * @param log     the transaction log to log
     */
    public void logTransaction(final String account,
                               @NonNull final TransactionLog log) {
        logs.computeIfAbsent(account, k -> new ArrayList<>()).add(log);
    }

    /**
     * Gets the transaction logs for an account.
     *
     * @param account the IBAN of the account to get the transaction logs for
     * @return the transaction logs for the account
     */
    public List<TransactionLog> getLogs(final String account) {
        return Collections.unmodifiableList(logs.getOrDefault(account, new ArrayList<>()));
    }

    /**
     * Removes the transaction logs for an account.
     *
     * @param account the IBAN of the account to remove the transaction logs for
     * @return the transaction logs for the account, or{@code null} if the account does not exist
     */
    public List<TransactionLog> removeLogs(final String account) {
        return logs.remove(account);
    }
}
