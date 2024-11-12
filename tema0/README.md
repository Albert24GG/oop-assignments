# OOP Assignment - GwentStone

## Game Overview

The game’s core functionality is encapsulated within several classes, each responsible for specific aspects of gameplay,
such as managing turns, handling players, and defining card behaviors. The main entry point is the `GameManager` class,
which provides a public API for interacting with the game.

## Class Structure and Responsibilities

### GameManager

The `GameManager` class serves as the main controller of the game, handling the game flow, player actions, and
interactions between components. Its public methods provide access to all available actions, facilitating the
progression of the game and managing turn-based gameplay.

### GameState

The `GameState` class represents the current state of the game, initializing the players, board, and turn manager. It
acts as a central access point for all game-related data, ensuring consistent management of player states, the board
layout, and other game elements required to control game flow.

### Player and PlayerGameData

The `Player` class represents a player in the game, while `PlayerGameData` maintains a player's state during a match,
such as their deck, health, and other relevant attributes. This setup allows for clean separation of static player
details from game-specific state, supporting multiple games with different states.

### GameBoard

The `GameBoard` class manages the layout and interaction of cards on the board. It provides methods for placing and
moving cards, interacting with them, and managing spaces. The board is the main arena where cards are played and
abilities are activated.

### TurnManager

The `TurnManager` class tracks the active player and facilitates switching turns. By keeping track of the current
player, it ensures smooth turn-based gameplay, removing the need for manual player tracking in the `GameManager`.

### Cards and Abilities

The `cards` package defines the card hierarchy. The `Card` class is an abstract base that holds common attributes, with
subclasses `Minion` and `Hero` providing specific card types. Stateful cards that can be played are derived from the
`PlayableCard` class, with `PlayableMinion` and `PlayableHero` adding unique behaviors and states. Each card’s abilities
are defined based on card type and can target either friendly or enemy cards. Because the underlying card of the
`PlayableCard` had to be retrieved by its type, not the generic `Card` type, I found that a clean `PlayableCard`
solution was to use a generic type parameter to specify the underlying card type, that extends `Card`.

### Action and ActionFactory

The `Action` class represents specific actions in the game, with an abstract `execute` method implemented by each action
type. The `ActionFactory` class generates `Action` objects based on input parameters, streamlining the process of
creating and executing actions. Each `Action` produces an `ActionOutput`, which can be converted to JSON, making it
easier to handle and display results.

### Utils Package

The `utils` package contains helper classes for parsing input, mapping objects to JSON, and handling common error
messages. These utilities support various backend functionalities, improving code readability and maintainability.

## Game Flow and Interactions

### Main Game Loop

Initially, the players are created and initialized with their respective decks. For each game, a new `GameManager` is
created through the `startNewGame` method, which sets up the all game components such as the game state, and the state
of each player (including their selected deck). The game then progresses by executing each action. When reading an
action from the input, the `ActionFactory` is used to create the corresponding `Action` object, which is then executed.
This action is just a proxy to the actual action that is executed by the `GameManager`, and that also processes the
output of the action. Finally, the `ActionOutput` resulted from the execution of the action is converted to JSON and
added to the output list (if the action produced any output).

### Actions

- **Debug actions**: these are pretty trivial, as they just gather information about the game state and the players,
  and return them as JSON.

- **Attack/Use ability actions**: these actions are not particularly complex, but they require some validation to
  ensure that the action is legal. Apart from that, the execution of the ability/attack is handled through the methods
  provided by the `PlayableCard` class and its subclasses. At the end of the action, the card is marked as used, and
  it is checked if it should be removed from the board. Also, we check if the game has ended after the action and invoke
  the appropriate routines if it has.

- **End turn action**: this action is pretty straightforward, as it just switches the turn to the other player. It also
  invokes some routines associated with the rules of the game, such as unfreezing the cards of the current player.
