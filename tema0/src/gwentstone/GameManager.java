package gwentstone;

import fileio.Coordinates;
import fileio.StartGameInput;
import gwentstone.actions.ActionException;
import gwentstone.cards.Deck;
import gwentstone.cards.impl.AbilityTarget;
import gwentstone.cards.impl.Minion;
import gwentstone.cards.impl.PlayableHero;
import gwentstone.cards.impl.PlayableMinion;
import gwentstone.utils.GameMessage;

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

    public static GameManager startNewGame(final List<Player> players, final StartGameInput input) {
        return new GameManager(players, input);
    }

    /**
     * Get the index of the active player.
     *
     * @return The index of the player (1 or 2)
     */
    public int getPlayerTurn() {
        // internally, we store the player index starting from zero
        return gameState.getTurnManager().getCurrentPlayerIdx() + 1;
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
            throw new ActionException(GameMessage.NOT_ENOUGH_MANA.getMessage());
        }

        if (!gameBoard.canPlace(playerIndex, minionInHand)) {
            throw new ActionException(GameMessage.ROW_FULL.getMessage());
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
     * Use a card to attack another card.
     *
     * @param attackerCoords The coordinates (row, column) of the attacker card
     * @param targetCoords   The coordinates (row, column) of the attacked card
     * @throws ActionException If the action fails, an exception with the appropriate message
     *                         is thrown
     */
    public void cardUsesAtack(final Coordinates attackerCoords, final Coordinates targetCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();

        if (gameBoard.getPlayerIdxHoldingCard(targetCoords) ==
                gameState.getTurnManager().getCurrentPlayerIdx()) {
            throw new ActionException(GameMessage.ATTACKED_CARD_NOT_ENEMY.getMessage());
        }

        if (gameBoard.attackedThisRound(attackerCoords)) {
            throw new ActionException(GameMessage.ATTACKER_ALREADY_ATTACKED.getMessage());
        }

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);

        if (attacker.isFrozen()) {
            throw new ActionException(GameMessage.ATTACKER_FROZEN.getMessage());
        }

        PlayableMinion target = gameBoard.getCard(targetCoords);

        if (!target.isTank() &&
                gameBoard.hasTanksOnBoard(gameState.getTurnManager().getInactivePlayerIdx())) {
            throw new ActionException(GameMessage.ATTACKED_CARD_NOT_TANK.getMessage());
        }

        attacker.attack(target);
        if (target.getCurrentHealth() == 0) {
            gameBoard.removeCard(targetCoords);
        }
        gameBoard.markAttacker(attackerCoords);
    }

    /**
     * Get the minion at some given coordinates on the game board.
     *
     * @param coords Card's coordinates on the game board
     * @return The minion
     * @throws ActionException If no minion is present at the specified location, an exception is
     *                         thrown
     */
    public PlayableMinion getCardAtPosition(final Coordinates coords) throws ActionException {
        try {
            return gameState.getGameBoard().getCard(coords);
        } catch (Exception e) {
            throw new ActionException(GameMessage.NO_CARD_AT_POS.getMessage());
        }
    }

    /**
     * Use a card's ability on another card.
     *
     * @param attackerCoords The coordinates (row, column) of the attacker card
     * @param targetCoords   The coordinates (row, column) of the attacked card
     * @throws ActionException If the action fails, an exception with the appropriate message
     *                         is thrown
     */
    public void cardUsesAbility(final Coordinates attackerCoords, final Coordinates targetCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);

        if (attacker.isFrozen()) {
            throw new ActionException(GameMessage.ATTACKER_FROZEN.getMessage());
        }

        if (gameBoard.attackedThisRound(attackerCoords)) {
            throw new ActionException(GameMessage.ATTACKER_ALREADY_ATTACKED.getMessage());
        }

        AbilityTarget attackerTarget = attacker.getAbilityTarget().orElse(null);

        // This should not happen
        if (attackerTarget == null) {
            return;
        }

        // check if the attacked card is either ally or enemy
        boolean isTargetEnemy = gameBoard.getPlayerIdxHoldingCard(targetCoords) !=
                gameState.getTurnManager().getCurrentPlayerIdx();
        PlayableMinion target = gameBoard.getCard(targetCoords);

        // we assume that the attacker has an ability
        if (attackerTarget == AbilityTarget.PLAYER && isTargetEnemy) {
            throw new ActionException(GameMessage.ATTACKED_CARD_NOT_PLAYER.getMessage());
        }

        if (attackerTarget == AbilityTarget.ENEMY) {

            if (!isTargetEnemy) {
                throw new ActionException(GameMessage.ATTACKED_CARD_NOT_ENEMY.getMessage());
            }

            if (!target.isTank() &&
                    gameBoard.hasTanksOnBoard(gameState.getTurnManager().getInactivePlayerIdx())) {
                throw new ActionException(GameMessage.ATTACKED_CARD_NOT_TANK.getMessage());
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
     * @throws ActionException If the action fails, an exception with the appropriate message
     *                         is thrown
     */
    public Optional<Integer> useAttackHero(final Coordinates attackerCoords)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();

        PlayableMinion attacker = gameBoard.getCard(attackerCoords);

        if (attacker.isFrozen()) {
            throw new ActionException(GameMessage.ATTACKER_FROZEN.getMessage());
        }

        if (gameBoard.attackedThisRound(attackerCoords)) {
            throw new ActionException(GameMessage.ATTACKER_ALREADY_ATTACKED.getMessage());
        }

        if (gameBoard.hasTanksOnBoard(gameState.getTurnManager().getInactivePlayerIdx())) {
            throw new ActionException(GameMessage.ATTACKED_CARD_NOT_TANK.getMessage());
        }

        TurnManager turnManager = gameState.getTurnManager();
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
     * @throws ActionException If the action fails, an exception with the appropriate message
     *                         is thrown
     */
    public void useHeroAbility(final int affectedRow)
            throws ActionException {
        GameBoard gameBoard = gameState.getGameBoard();
        PlayerGameData playerGameData = gameState.getTurnManager().getCurrentPlayer().getGameData();
        PlayableHero hero = playerGameData.getHero();

        if (hero.getMana() > playerGameData.getMana()) {
            throw new ActionException(GameMessage.HERO_ABILITY_NO_MANA.getMessage());
        }

        if (playerGameData.isUsedHeroAbility()) {
            throw new ActionException(GameMessage.HERO_ABILITY_ALREADY_USED.getMessage());
        }

        AbilityTarget heroAbilityTarget = hero.getAbilityTarget();

        // check if the attacked card is either ally or enemy
        boolean isTargetEnemy = gameBoard.getPlayerIdxHoldingRow(affectedRow) !=
                gameState.getTurnManager().getCurrentPlayerIdx();

        if (heroAbilityTarget == AbilityTarget.ENEMY && !isTargetEnemy) {
            throw new ActionException(GameMessage.ROW_NOT_ENEMY.getMessage());
        }

        if (heroAbilityTarget == AbilityTarget.PLAYER && isTargetEnemy) {
            throw new ActionException(GameMessage.ROW_NOT_PLAYER.getMessage());
        }

        List<PlayableMinion> targetRow = gameBoard.getRow(affectedRow);

        hero.useAbility(targetRow);

        // Remove killed cards
        if (heroAbilityTarget == AbilityTarget.ENEMY) {
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


