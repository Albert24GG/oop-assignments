package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.fileio.CommandInput;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public final class CommandFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, BiFunction<Integer, CommandInput, Command>> COMMANDS =
            Map.ofEntries(
                    Map.entry("printUsers", PrintUsers::new),
                    Map.entry("addAccount", AddAccount::new),
                    Map.entry("createCard", CreateCard::new),
                    Map.entry("addFunds", AddFunds::new),
                    Map.entry("deleteAccount", DeleteAccount::new)
            );

    private CommandFactory() {
    }

    /**
     * Get a command.
     *
     * @param command   the command
     * @param timestamp the timestamp
     * @param input     the input
     * @return the command, or {@code null} if the command does not exist
     */
    public static Command getCommand(final String command, final int timestamp,
                                     final CommandInput input) {
        return COMMANDS.getOrDefault(command, (t, i) -> null).apply(timestamp, input);
    }

    private static final class PrintUsers extends Command {
        private PrintUsers(final int timestamp, final CommandInput input) {
            super(timestamp, input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            var users = bank.getUsers();
            return Optional.of(
                    CommandOutput.builder()
                            .command("printUsers")
                            .timestamp(getTimestamp())
                            .output(MAPPER.valueToTree(bank.getUsers()))
                            .build());
        }
    }

    private static final class AddAccount extends Command {
        private AddAccount(final int timestamp, final CommandInput input) {
            super(timestamp, input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.createAccount(
                    input.getEmail(),
                    input.getCurrency(),
                    input.getAccountType(),
                    input.getInterestRate(),
                    getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class CreateCard extends Command {
        private CreateCard(final int timestamp, final CommandInput input) {
            super(timestamp, input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.createCard(
                    input.getEmail(),
                    input.getAccount(),
                    "DEBIT",
                    getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class AddFunds extends Command {
        private AddFunds(final int timestamp, final CommandInput input) {
            super(timestamp, input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.addFunds(
                    input.getAccount(),
                    input.getAmount(),
                    getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class DeleteAccount extends Command {
        private DeleteAccount(final int timestamp, final CommandInput input) {
            super(timestamp, input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            ObjectNode output = MAPPER.createObjectNode();
            try {
                bank.deleteAccount(
                        input.getEmail(),
                        input.getAccount(),
                        getTimestamp()
                );
                output.put("success", "Account deleted")
                        .put("timestamp", getTimestamp());
            } catch (IllegalArgumentException e) {
                output.put("error", e.getMessage())
                        .put("timestamp", getTimestamp());
            }
            return Optional.of(
                    CommandOutput.builder()
                            .command("deleteAccount")
                            .timestamp(getTimestamp())
                            .output(output)
                            .build());
        }
    }
}
