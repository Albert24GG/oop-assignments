package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.BankAccount;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;

@Builder
@RequiredArgsConstructor
public final class SetAccountAlias extends BankOperation<Void> {
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final IBAN accountIban;
    @NonNull
    private final String alias;
    @NonNull
    private final Integer timestamp;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);
        BankAccount bankAccount = BankOperationUtils.getBankAccountByIban(context, accountIban);

        BankOperationUtils.validateAccountOwnership(context, bankAccount, userAccount);

        context.bankAccService().registerAlias(bankAccount, alias);
        return BankOperationResult.success();
    }
}
