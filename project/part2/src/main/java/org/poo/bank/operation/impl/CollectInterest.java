package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.InterestClaimLog;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class CollectInterest extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() != BankAccountType.SAVINGS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This is not a savings account");
        }

        double collectedInterest = bankAccount.getBalance();

        context.bankAccService().collectInterest((SavingsAccount) bankAccount);

        collectedInterest = bankAccount.getBalance() - collectedInterest;

        AuditLog auditLog = InterestClaimLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.INTEREST_CLAIM)
                .description("Interest rate income")
                .amount(collectedInterest)
                .currency(bankAccount.getCurrency())
                .build();

        BankOperationUtils.recordLog(context, accountIban, auditLog);

        return BankOperationResult.success();
    }
}
