package gwentstone.utils;

public enum GameMessage {
    NOT_ENOUGH_MANA("Not enough mana to place card on table."),
    ROW_FULL("Cannot place card on table since row is full."),
    ATTACKED_CARD_NOT_ENEMY("Attacked card does not belong to the enemy."),
    ATTACKER_ALREADY_ATTACKED("Attacker card has already attacked this turn."),
    ATTACKER_FROZEN("Attacker card is frozen."),
    ATTACKED_CARD_NOT_TANK("Attacked card is not of type 'Tank'."),
    ATTACKED_CARD_NOT_PLAYER("Attacked card does not belong to the current player."),
    NO_CARD_AT_POS("No card available at that position.");


    private final String message;

    GameMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
