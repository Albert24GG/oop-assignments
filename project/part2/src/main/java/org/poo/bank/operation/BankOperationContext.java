package org.poo.bank.operation;

import org.poo.bank.currency.CurrencyExchangeService;
import org.poo.bank.account.BankAccService;
import org.poo.bank.account.UserService;
import org.poo.bank.card.CardService;
import org.poo.bank.eventSystem.BankEventService;
import org.poo.bank.merchant.MerchantService;
import org.poo.bank.splitPayment.SplitPaymentService;
import org.poo.bank.log.AuditLogService;

public record BankOperationContext(BankAccService bankAccService,
                                   UserService userService,
                                   CardService cardService,
                                   AuditLogService auditLogService,
                                   CurrencyExchangeService currencyExchangeService,
                                   MerchantService merchantService,
                                   SplitPaymentService splitPaymentService,
                                   BankEventService eventService
) {
}
