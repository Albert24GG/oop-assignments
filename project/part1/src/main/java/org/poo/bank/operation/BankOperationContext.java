package org.poo.bank.operation;

import lombok.NonNull;
import org.poo.bank.currency.CurrencyExchangeService;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.UserService;
import org.poo.bank.card.CardService;
import org.poo.bank.transaction.TransactionService;

public record BankOperationContext(@NonNull BankAccService bankAccService,
                                   @NonNull UserService userService,
                                   @NonNull CardService cardService,
                                   @NonNull TransactionService transactionService,
                                   @NonNull CurrencyExchangeService currencyExchangeService) {
}
