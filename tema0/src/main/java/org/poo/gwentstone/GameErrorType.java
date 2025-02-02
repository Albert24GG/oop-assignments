package org.poo.gwentstone;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameErrorType {
    NOT_ENOUGH_MANA("Not enough mana to place card on table."),
    ROW_FULL("Cannot place card on table since row is full."),
    ATTACKED_CARD_NOT_ENEMY("Attacked card does not belong to the enemy."),
    ATTACKER_ALREADY_ATTACKED("Attacker card has already attacked this turn."),
    ATTACKER_FROZEN("Attacker card is frozen."),
    ATTACKED_CARD_NOT_TANK("Attacked card is not of type 'Tank'."),
    ATTACKED_CARD_NOT_PLAYER("Attacked card does not belong to the current player."),
    NO_CARD_AT_POS("No card available at that position."),
    HERO_ABILITY_NO_MANA("Not enough mana to use hero's ability."),
    HERO_ABILITY_ALREADY_USED("Hero has already attacked this turn."),
    ROW_NOT_ENEMY("Selected row does not belong to the enemy."),
    ROW_NOT_PLAYER("Selected row does not belong to the current player.");

    private final String message;
}
