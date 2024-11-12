package org.poo.gwentstone.actions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.poo.fileio.ActionsInput;
import org.poo.gwentstone.GameManager;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Action {
    @Getter(AccessLevel.PROTECTED)
    private final ActionsInput input;

    /**
     * Execute the action in a given game context
     *
     * @param gameManager The manager of the current game context
     * @return The output produced by the action
     */
    public abstract ActionOutput execute(GameManager gameManager);
}
