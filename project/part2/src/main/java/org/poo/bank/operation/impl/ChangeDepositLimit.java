package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.account.BusinessAccount;
import org.poo.bank.account.BusinessAccountRole;
import org.poo.bank.account.BusinessOperation;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankErrorType;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class ChangeDepositLimit extends BankOperation<Void> {
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final Double newLimit;
    @NonNull
    private final Email userEmail;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        if (bankAccount.getType() != BankAccountType.BUSINESS) {
            throw new BankOperationException(BankErrorType.INVALID_OPERATION,
                    "This is not a business account");
        }

        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, userEmail);
        try {
            BankOperationUtils.validatePermissions(context, (BusinessAccount) bankAccount,
                    userAccount, new BusinessOperation.SetDepositLimit());
        } catch (BankOperationException e) {
            throw new BankOperationException(BankErrorType.PERMISSION_DENIED,
                    "You must be owner in order to change deposit limit.");
        }

        // Set the deposit limit only for employees
        context.bankAccService().setBusinessAccountDepositLimit((BusinessAccount) bankAccount,
                BusinessAccountRole.EMPLOYEE, newLimit);

        return BankOperationResult.success();
    }
}
