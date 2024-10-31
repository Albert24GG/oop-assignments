package gwentstone.actions;

import com.fasterxml.jackson.databind.node.BaseJsonNode;
import fileio.ActionsInput;
import gwentstone.GameManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Action {
    @Getter(AccessLevel.PROTECTED)
    private final ActionsInput input;

    protected Action() {
        input = null;
    }

    /**
     * Execute the action in a given game context
     *
     * @param gameManager The manager of the current game context
     * @return The output produced by the action
     */
    public abstract ActionOutput<? extends BaseJsonNode> execute(GameManager gameManager);
}
