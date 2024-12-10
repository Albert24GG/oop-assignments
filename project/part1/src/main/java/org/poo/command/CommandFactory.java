package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.account.BankAccountType;
import org.poo.bank.card.CardType;
import org.poo.bank.payment.request.PaymentRequest;
import org.poo.bank.payment.request.impl.CardPaymentRequest;
import org.poo.bank.payment.request.impl.TransferRequest;
import org.poo.bank.type.CardNumber;
import org.poo.bank.type.Currency;
import org.poo.bank.type.Email;
import org.poo.bank.type.IBAN;
import org.poo.fileio.CommandInput;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class CommandFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Function<CommandInput, Command>> COMMANDS =
            Map.ofEntries(
                    Map.entry("printUsers", PrintUsers::new),
                    Map.entry("addAccount", AddAccount::new),
                    Map.entry("createCard", i -> new CreateCard(i, "DEBIT")),
                    Map.entry("createOneTimeCard", i -> new CreateCard(i, "SINGLE_USE")),
                    Map.entry("addFunds", AddFunds::new),
                    Map.entry("deleteAccount", DeleteAccount::new),
                    Map.entry("deleteCard", DeleteCard::new),
                    Map.entry("setMinimumBalance", SetMinBalance::new),
                    Map.entry("payOnline", i -> {
                        PaymentRequest paymentRequest = CardPaymentRequest.builder()
                                .cardNumber(CardNumber.of(i.getCardNumber()))
                                .ownerEmail(Email.of(i.getEmail()))
                                .description(i.getDescription())
                                .currency(Currency.of(i.getCurrency()))
                                .merchant(i.getCommerciant())
                                .amount(i.getAmount())
                                .timestamp(i.getTimestamp())
                                .build();
                        return new MakePayment(i, paymentRequest);
                    }),
                    Map.entry("sendMoney", i -> {
                        PaymentRequest paymentRequest = TransferRequest.builder()
                                .senderAccount(IBAN.of(i.getAccount()))
                                .receiverAccount(i.getReceiver())
                                .description(i.getDescription())
                                .amount(i.getAmount())
                                .timestamp(i.getTimestamp())
                                .build();
                        return new MakePayment(i, paymentRequest);
                    })
            );

    private CommandFactory() {
    }

    /**
     * Get a command.
     *
     * @param command the command
     * @param input   the input
     * @return the command, or {@code null} if the command does not exist
     */
    public static Command getCommand(final String command, final CommandInput input) {
        return COMMANDS.getOrDefault(command, i -> null).apply(input);
    }

    private static final class PrintUsers extends Command {
        private PrintUsers(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            return Optional.of(
                    CommandOutput.builder()
                            .command(getInput().getCommand())
                            .timestamp(getInput().getTimestamp())
                            .output(MAPPER.valueToTree(bank.getUsers()))
                            .build());
        }
    }

    private static final class AddAccount extends Command {
        private AddAccount(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.createAccount(
                    Email.of(input.getEmail()),
                    Currency.of(input.getCurrency()),
                    BankAccountType.of(input.getAccountType()),
                    input.getInterestRate(),
                    input.getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class CreateCard extends Command {
        private final String cardType;

        private CreateCard(final CommandInput input, final String cardType) {
            super(input);
            this.cardType = cardType;
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.createCard(
                    Email.of(input.getEmail()),
                    IBAN.of(input.getAccount()),
                    CardType.of(cardType),
                    input.getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class AddFunds extends Command {
        private AddFunds(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.addFunds(
                    IBAN.of(input.getAccount()),
                    input.getAmount(),
                    input.getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class DeleteAccount extends Command {
        private DeleteAccount(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            ObjectNode output = MAPPER.createObjectNode();
            try {
                bank.deleteAccount(
                        Email.of(input.getEmail()),
                        IBAN.of(input.getAccount()),
                        input.getTimestamp()
                );
                output.put("success", "Account deleted")
                        .put("timestamp", input.getTimestamp());
            } catch (IllegalArgumentException e) {
                output.put("error", e.getMessage())
                        .put("timestamp", input.getTimestamp());
            }
            return Optional.of(
                    CommandOutput.builder()
                            .command("deleteAccount")
                            .timestamp(input.getTimestamp())
                            .output(output)
                            .build());
        }
    }

    private static final class DeleteCard extends Command {
        private DeleteCard(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            bank.deleteCard(
                    CardNumber.of(input.getCardNumber()),
                    input.getTimestamp()
            );
            return Optional.empty();
        }
    }

    private static final class SetMinBalance extends Command {
        private SetMinBalance(final CommandInput input) {
            super(input);
        }

        @Override
        public Optional<CommandOutput> execute(final Bank bank) {
            CommandInput input = getInput();
            try {
                bank.setAccMinBalance(
                        IBAN.of(input.getAccount()),
                        input.getMinBalance(),
                        input.getTimestamp()
                );
            } catch (IllegalArgumentException e) {
                return Optional.of(
                        CommandOutput.builder()
                                .command(input.getCommand())
                                .timestamp(input.getTimestamp())
                                .output(MAPPER.createObjectNode()
                                        .put("error", e.getMessage())
                                        .put("timestamp", input.getTimestamp()))
                                .build());
            }
            return Optional.empty();
        }
    }

    private static final class MakePayment extends Command {
        private final PaymentRequest paymentRequest;

        private MakePayment(final CommandInput input,
                            final PaymentRequest paymentRequest) {
            super(input);
            this.paymentRequest = paymentRequest;
        }

        @Override
        public Optional<CommandOutput> execute(Bank bank) {
            CommandInput input = getInput();

            try {
                bank.makePayment(paymentRequest);
            } catch (IllegalArgumentException e) {
                return Optional.of(
                        CommandOutput.builder()
                                .command(input.getCommand())
                                .timestamp(input.getTimestamp())
                                .output(MAPPER.createObjectNode()
                                        .put("description", e.getMessage())
                                        .put("timestamp", input.getTimestamp()))
                                .build());
            }

            return Optional.empty();
        }
    }
}
