package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.account.UserAccount;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.operation.util.BankOperationUtils;
import org.poo.bank.splitPayment.SplitPaymentType;
import org.poo.bank.type.Email;

@Builder
@RequiredArgsConstructor
public final class RejectSplitPayment extends BankOperation<Void> {
    @NonNull
    private final Integer timestamp;
    @NonNull
    private final Email ownerEmail;
    @NonNull
    private final SplitPaymentType splitPaymentType;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        UserAccount userAccount = BankOperationUtils.getUserByEmail(context, ownerEmail);

        context.splitPaymentService().rejectPayment(userAccount, splitPaymentType);

        return BankOperationResult.success();
    }
}
