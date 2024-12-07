package org.poo.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.bank.Bank;
import org.poo.fileio.CommandInput;

import java.util.Optional;

@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
public abstract class Command {
    private final int timestamp;
    private final CommandInput input;

    /**
     * Execute the command.
     *
     * @param bank the bank instance
     * @return an {@link Optional} containing the command output, or {@link Optional#empty()}
     * if the command has no output
     */
    public abstract Optional<CommandOutput> execute(final Bank bank);
}
