package org.poo.bank.operation;

import lombok.NonNull;
import org.poo.bank.currency.CurrencyExchangeService;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.UserService;
import org.poo.bank.card.CardService;
import org.poo.bank.transaction.TransactionLogService;

public record BankOperationContext(BankAccService bankAccService,
                                   UserService userService,
                                   CardService cardService,
                                   TransactionLogService transactionLogService,
                                   CurrencyExchangeService currencyExchangeService) {
}
