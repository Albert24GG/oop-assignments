package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessOperation;
import org.poo.bank.account.UserAccount;
import org.poo.bank.log.AuditLog;
import org.poo.bank.log.AuditLogStatus;
import org.poo.bank.log.AuditLogType;
import org.poo.bank.log.impl.DepositLog;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class AddFunds extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Email userEmail;
    private final double amount;
    private final int timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {

        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);

        // Validate permissions
        if (bankAccount.getType() == BankAccountType.BUSINESS) {
            try {
                BankOperationUtils.validatePermissions(context, (BusinessAccount) bankAccount,
                        userAccount, new BusinessOperation.AddFunds(amount));
            } catch (BankOperationException e) {
                return BankOperationResult.silentError(e.getErrorType());
            }
        } else {
            BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);
        }

        BankOperationUtils.addFunds(context, bankAccount, amount);

        AuditLog log = DepositLog.builder()
                .logStatus(AuditLogStatus.SUCCESS)
                .logType(AuditLogType.DEPOSIT)
                .amount(amount)
                .bankAccount(bankAccount)
                .userAccount(userAccount)
                .timestamp(timestamp)
                .build();
        BankOperationUtils.recordLog(context, bankAccount, log);

        return BankOperationResult.success();
    }
}
