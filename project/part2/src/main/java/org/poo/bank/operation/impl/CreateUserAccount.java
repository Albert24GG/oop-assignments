package org.poo.bank.operation.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.poo.bank.operation.BankOperation;
import org.poo.bank.operation.BankOperationContext;
import org.poo.bank.operation.BankOperationException;
import org.poo.bank.operation.BankOperationResult;
import org.poo.bank.type.Date;
import org.poo.bank.type.Email;

@Builder
@RequiredArgsConstructor
public final class CreateUserAccount extends BankOperation<Void> {
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final Email email;
    @NonNull
    private final Date birthDate;
    @NonNull
    private final String occupation;

    @Override
    protected BankOperationResult<Void> internalExecute(final BankOperationContext context)
            throws BankOperationException {
        context.userService().createUser(firstName, lastName, email, birthDate, occupation);
        return BankOperationResult.success();
    }
}
