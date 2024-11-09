package org.poo.fileio;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class GameInput {
    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;

    public GameInput() {
    }

    @Override
    public String toString() {
        return "GameInput{"
                + "startGame="
                + startGame
                + ", actions="
                + actions
                + '}';
    }
}
