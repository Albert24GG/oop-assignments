package org.poo.gwentstone;


import org.poo.fileio.Coordinates;
import org.poo.fileio.StartGameInput;
import org.poo.gwentstone.actions.ActionException;
import org.poo.gwentstone.cards.Deck;
import org.poo.gwentstone.cards.impl.AbilityTarget;
import org.poo.gwentstone.cards.impl.Minion;
import org.poo.gwentstone.cards.impl.PlayableHero;
import org.poo.gwentstone.cards.impl.PlayableMinion;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class containing the main API for interacting with the GameState.
 * It contains all the actions that get executed during a game.
 */
public final class GameManager {
    private final GameState gameState;

    private GameManager(final List<Player> players, final StartGameInput input) {
        gameState = new GameState(players, input);
    }

    /**
     * Start a new game
     *
     * @param players The list of players
     * @param input   The StartGame object containing the game parameters
     * @return The GameManager handler
     */
    public static GameManager startNewGame(final List<Player> players, final StartGameInput input) {
        return new GameManager(players, input);
    }

    /**
     * Get the index of the active player.
     *
     * @return The index of the player (0 or 1)
     */
    public int getPlayerTurn() {
        return gameState.getTurnManager().getCurrentPlayerIdx();
    }

    /**
     * Get the deck assigned to a player in the current game
     *
     * @param playerIdx The player index (0 or 1)
     * @return The specified player's deck
     */
    public Deck getPlayerDeck(final int playerIdx) {
        return gameState.getPlayers().get(playerIdx).getGameData().getCurrentDeck();
    }

    /**
     * Get the hero assigned to a player in the current game
     *
     * @param playerIdx The player index (0 or 1)
     * @return Player's hero
     */
    public PlayableHero getPlayerHero(final int playerIdx) {
        return gameState.getPlayers().get(playerIdx).getGameData().getHero();
    }

    /**
     * End current's player turn
     */
    public void endTurn() {
        TurnManager turnManager = gameState.getTurnManager();
        GameBoard gameBoard = gameState.getGameBoard();

        if (turnManager.isRoundEndable()) {
            gameState.startRoundRoutine();
        }

        gameBoard.resetPlayerAttackHistory(turnManager.getCurrentPlayerIdx());
        gameBoard.unfreezePlayerCards(turnManager.getCurrentPlayerIdx());
        turnManager.endTurn();
    }

    /**
     * Place the card at a given index on the game board.
     *
     * @param handIdx The index of the card in the current hand.
     * @throws ActionException If the action cannot be completed, an exception with the
     *                         appropriate message is thrown
     */
    public void placeCard(final int handIdx) throws ActionException {
        PlayerGameData playerGameData = gameState.getTurnManager().getCurrentPlayer()
                .getGameData();
        int playerIndex = gameState.getTurnManager().getCurrentPlayerIdx();
        PlayableMinion minionInHand = playerGameData.getMinionInHand(handIdx);
        GameBoard gameBoard = gameState.getGameBoard();

        if (minionInHand.getMana() > playerGameData.getMana()) {
            throw new ActionException(GameErrorType.NOT_ENOUGH_MANA);
        }

        if (!gameBoard.canPlace(playerIndex, minionInHand)) {
            throw new ActionException(GameErrorType.ROW_FULL);
        }

        gameBoard.placeMinion(playerIndex, minionInHand);
        playerGameData.removeMinionFromHand(handIdx);
        playerGameData.useMana(minionInHand.getMana());
    }

    /**
     * Get hands of a player by its index.
     *
     * @param playerIdx Index of the queried player (0 or 1)
     * @return The list of minions that the player has in hand.
     */
    public List<Minion> getCardsInHand(final int playerIdx) {
        return gameState
                .getPlayers()
                .get(playerIdx)
                .getGameData()
                .getHand()
                .stream()
                .map(PlayableMinion::getUnderlyingCard)
                .toList();
    }

    /**
     * Get the cards present on the game board.
     *
     * @return An immutable 3D list containing the minions placed on the game board
     */
    public List<List<PlayableMinion>> getCardsOnTable() {
        return gameState
                .getGameBoard()
                .getBoard()
                .stream()
                .map(Collections::unmodifiableList)
                .toList();
    }

    /**
     * Get player's mana level
     *
     * @param playerIdx Player's index
     * @return The level of mana the player has
     */
    public int getPlayerMana(final int playerIdx) {
        return gameState.getPlayers().get(playerIdx).getGameData().getMana();
    }

    /**
     * Get the minion at some given coordinates on the game board.
     *
     * @param coords Card's coordinates on the game board
     * @return The minion
     * @throws ActionException If no minion is present at the specified location, an exception is
     *                         with type {@code NO_CARD_AT_POS} is thrown
     */
    public PlayableMinion getCardAtPosition(final Coordinates coords) throws ActionException {
        try {
            return gameState.getGameBoard().getCard(coords);
        } catch (Exception e) {
            throw new ActionException(GameErrorType.NO_CARD_AT_POS);
        }
    }

    private void validateCardNotFrozen(final Coordinates attackerCoords) throws ActionException {
        PlayableMinion attacker = gameState.getGameBoard().getCard(attackerCoords);
        if (attacker.isFrozen()) {
            throw new ActionException(GameErrorType.ATTACKER_FROZEN);
        }
    }

    private void validateCardNotAttacked(final Coordinates attackerCoords) throws ActionException {
        if (gameState.getGameBoard().attackedThisRound(attackerCoords)) {
            throw new ActionException(GameErrorType.ATTACKER_ALREADY_ATTACKED);
        }
    }

    private void validateTankRule() throws ActionException {
        validateTankRule(null);
    }

    private void validateTankRule(final PlayableMinion target) throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        TurnManager turnManager = gameState.getTurnManager();

        if (Optional.ofNullable(target).map(t -> !t.isTank()).orElse(true)
                && gameBoard.hasTanksOnBoard(turnManager.getInactivePlayerIdx())) {
            throw new ActionException(GameErrorType.ATTACKED_CARD_NOT_TANK);
        }
    }

    private void validateTargetIsEnemy(final Coordinates targetCoords) throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        TurnManager turnManager = gameState.getTurnManager();

        if (gameBoard.getPlayerIdxHoldingCard(targetCoords) == turnManager.getCurrentPlayerIdx()) {
            throw new ActionException(GameErrorType.ATTACKED_CARD_NOT_ENEMY);
        }
    }

    private void validateManaAvailable(final int requiredMana) throws ActionException {
        PlayerGameData playerGameData = gameState.getTurnManager().getCurrentPlayer().getGameData();
        if (requiredMana > playerGameData.getMana()) {
            throw new ActionException(GameErrorType.HERO_ABILITY_NO_MANA);
        }
    }

    private void validateHeroAbilityUsage() throws ActionException {
        PlayerGameData playerGameData = gameState.getTurnManager().getCurrentPlayer().getGameData();
        if (playerGameData.isUsedHeroAbility()) {
            throw new ActionException(GameErrorType.HERO_ABILITY_ALREADY_USED);
        }
    }

    /**
     * Use a card to attack another card.
     *
     * @param attackerCoords The coordinates (row, column) of the attacker card
     * @param targetCoords   The coordinates (row, column) of the attacked card
     * @throws ActionException If the action fails, an exception with one of the following types is
     *                         thrown: {@code ATTACKED_CARD_NOT_ENEMY},
     *                         {@code ATTACKER_ALREADY_ATTACKED},
     *                         {@code ATTACKER_FROZEN}, {@code ATTACKED_CARD_NOT_TANK}
     */
    public void cardUsesAttack(final Coordinates attackerCoords, final Coordinates targetCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();

        validateTargetIsEnemy(targetCoords);
        validateCardNotAttacked(attackerCoords);
        validateCardNotFrozen(attackerCoords);
        validateTankRule(gameBoard.getCard(targetCoords));

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);
        PlayableMinion target = gameBoard.getCard(targetCoords);

        attacker.attack(target);
        if (target.getCurrentHealth() == 0) {
            gameBoard.removeCard(targetCoords);
        }
        gameBoard.markAttacker(attackerCoords);
    }

    /**
     * Use a card's ability on another card.
     *
     * @param attackerCoords The coordinates (row, column) of the attacker card
     * @param targetCoords   The coordinates (row, column) of the attacked card
     * @throws ActionException If the action fails, an exception with one of the following types is
     *                         thrown: {@code ATTACKED_CARD_NOT_ENEMY},
     *                         {@code ATTACKER_ALREADY_ATTACKED}, {@code ATTACKER_FROZEN},
     *                         {@code ATTACKED_CARD_NOT_TANK}, {@code ATTACKED_CARD_NOT_PLAYER}
     */
    public void cardUsesAbility(final Coordinates attackerCoords, final Coordinates targetCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        TurnManager turnManager = gameState.getTurnManager();

        validateCardNotFrozen(attackerCoords);
        validateCardNotAttacked(attackerCoords);

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);
        PlayableMinion target = gameBoard.getCard(targetCoords);

        AbilityTarget attackerTarget = attacker.getAbilityTarget().orElse(null);
        // This should not happen
        if (attackerTarget == null) {
            return;
        }

        boolean isTargetEnemy = gameBoard.getPlayerIdxHoldingCard(targetCoords)
                != turnManager.getCurrentPlayerIdx();

        // we assume that the attacker has an ability
        switch (attackerTarget) {
            case PLAYER -> {
                if (isTargetEnemy) {
                    throw new ActionException(GameErrorType.ATTACKED_CARD_NOT_PLAYER);
                }
            }
            case ENEMY -> {
                if (!isTargetEnemy) {
                    throw new ActionException(GameErrorType.ATTACKED_CARD_NOT_ENEMY);
                } else {
                    validateTankRule(gameBoard.getCard(targetCoords));
                }
            }
            default -> {
                // do nothing
            }
        }

        attacker.useAbility(target);
        if (target.getCurrentHealth() == 0) {
            gameBoard.removeCard(targetCoords);
        }
        gameBoard.markAttacker(attackerCoords);
    }

    /**
     * Attack the enemy hero using a card.
     *
     * @param attackerCoords The coordinates (row, column) of the attacker card
     * @return An {@code Optional} containing the index of the player who won, or an empty
     * {@code Optional} if the game did not end
     * @throws ActionException If the action fails, an exception with one of the following types is
     *                         thrown: {@code ATTACKER_ALREADY_ATTACKED}, {@code ATTACKER_FROZEN},
     *                         {@code ATTACKED_CARD_NOT_TANK}
     */
    public Optional<Integer> useAttackHero(final Coordinates attackerCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        TurnManager turnManager = gameState.getTurnManager();

        validateCardNotFrozen(attackerCoords);
        validateCardNotAttacked(attackerCoords);
        validateTankRule();

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);
        PlayableHero enemyHero = turnManager.getInactivePlayer().getGameData().getHero();

        attacker.attack(enemyHero);
        if (enemyHero.getCurrentHealth() == 0) {
            gameState.endGame();
            turnManager.getCurrentPlayer().addWin();
            return Optional.of(turnManager.getCurrentPlayerIdx());
        }
        gameBoard.markAttacker(attackerCoords);

        return Optional.empty();
    }

    /**
     * Use the ability of the current player's hero on a given row of the game board.
     *
     * @param affectedRow The affected row
     * @throws ActionException If the action fails, an exception with one of the following types is
     *                         thrown: {@code ROW_NOT_PLAYER}, {@code ROW_NOT_ENEMY},
     *                         {@code HERO_ABILITY_NO_MANA}, {@code HERO_ABILITY_ALREADY_USED}
     */

    public void useHeroAbility(final int affectedRow)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        TurnManager turnManager = gameState.getTurnManager();
        PlayerGameData playerGameData = turnManager.getCurrentPlayer().getGameData();
        PlayableHero hero = playerGameData.getHero();

        validateManaAvailable(hero.getMana());
        validateHeroAbilityUsage();

        // Validate the row ownership
        boolean isTargetEnemy = gameBoard.getPlayerIdxHoldingRow(affectedRow)
                != turnManager.getCurrentPlayerIdx();

        switch (hero.getAbilityTarget()) {
            case PLAYER -> {
                if (isTargetEnemy) {
                    throw new ActionException(GameErrorType.ROW_NOT_PLAYER);
                }
            }
            case ENEMY -> {
                if (!isTargetEnemy) {
                    throw new ActionException(GameErrorType.ROW_NOT_ENEMY);
                }
            }
            default -> {
                // do nothing
            }
        }


        List<PlayableMinion> targetRow = gameBoard.getRow(affectedRow);
        hero.useAbility(targetRow);

        if (hero.getAbilityTarget() == AbilityTarget.ENEMY) {
            targetRow.removeIf(m -> m.getCurrentHealth() == 0);
        }

        playerGameData.markUsedHeroAbility();
        playerGameData.useMana(hero.getMana());
    }


    /**
     * Get a list of the frozen cards on the game board
     *
     * @return The list of frozen cards
     */
    public List<PlayableMinion> getFrozenCardsOnTable() {
        return gameState.getGameBoard().getFrozenCards();
    }

    /**
     * Get the total number of games played.
     *
     * @return The number of games played
     */
    public int getTotalGamesPlayed() {
        return gameState.getPlayers()
                .stream()
                .map(Player::getWins)
                .reduce(0, Integer::sum);
    }

    /**
     * Get the number of wins of a given player.
     *
     * @param playerIdx The index of the player
     * @return The number of wins of the specified player
     */
    public int getPlayerWins(final int playerIdx) {
        return gameState.getPlayers().get(playerIdx).getWins();
    }

}


