package gwentstone.utils;

public enum GameMessage {
    NOT_ENOUGH_MANA("Not enough mana to place card on table."),
    ROW_FULL("Cannot place card on table since row is full.");

    private final String message;

    GameMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
