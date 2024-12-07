package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.bank.Bank;
import org.poo.fileio.CommandInput;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public final class CommandFactory {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, BiFunction<Integer, CommandInput, Command>> COMMANDS =
            Map.ofEntries(
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

}
