package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class DeleteBankAccount extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);

        if (!context.bankAccService().canDeleteAccount(bankAccount)) {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(timestamp)
                    .logStatus(AuditLogStatus.FAILURE)
                    .logType(AuditLogType.ACCOUNT_DELETION)
                    .description("Account couldn't be deleted - there are funds remaining")
                    .build();
            BankOperationUtils.recordLog(context, bankAccount, auditLog);

            throw new BankOperationException(BankErrorType.ACCOUNT_DELETION_FAILED);
        }

        // Remove the account and its cards
        context.bankAccService().removeAccount(bankAccount);
        bankAccount.getCards().forEach(context.cardService()::removeCard);

        AuditLog auditLog = AuditLog.builder()
                .timestamp(timestamp)
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.ACCOUNT_DELETION)
                .description("Account deleted")
                .build();
        BankOperationUtils.recordLog(context, bankAccount, auditLog);

        return BankOperationResult.success();
    }
}
