package gwentstone.actions;

import com.fasterxml.jackson.databind.node.BaseJsonNode;
import gwentstone.GameManager;

public final class StartRoundActionDecorator extends Action {
    private final Action action;

    public StartRoundActionDecorator(final Action action) {
        this.action = action;
    }

    @Override
    public ActionOutput<? extends BaseJsonNode> execute(final GameManager gameManager) {
        if (!gameManager.isRoundStarted()) {
            gameManager.startRound();
        }
        return action.execute(gameManager);
    }
}
