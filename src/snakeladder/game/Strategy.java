package snakeladder.game;

import java.util.List;

public abstract class Strategy {
    protected GamePane gp;
    protected NavigationPane np;

    public Strategy (GamePane gp, NavigationPane np) {
        this.gp = gp;
        this.np = np;
    }

    public abstract boolean checkStrategy();

    protected int checkCurrentPlayerTurn() { return gp.getCurrentPuppetIndex(); }
    protected boolean hasGameEnded() { return gp.getPuppet().getCellIndex() >= 100; }
    protected boolean hasTraversedConnection() { return gp.getPuppet().getConTraversed(); } // must be checked alongside whether an up or down path has been traversed
    protected boolean hasTraversedUpPath() { return gp.getPuppet().getDy() < 0; }
    protected boolean hasTraversedDownPath() { return gp.getPuppet().getDy() > 0; }
    protected boolean hasOpponentTraversedConnection() { return gp.getOpponentPuppet().getConTraversed(); }
    protected boolean hasOpponentTraversedUpPath() { return gp.getOpponentPuppet().getDy() < 0; }
    protected boolean hasOpponentTraversedDownPath() { return gp.getOpponentPuppet().getDy() > 0; }
    protected int getNumberDice() { return np.getNbDice(); }
    protected int getTotalRoll() { return np.getTotalRoll(); }
    protected boolean getIsToggle() { return np.getIsToggle(); }
    protected int getOpponentPosition() { return gp.getOpponentPuppet().getCellIndex(); }
    protected int getPlayerPosition() { return gp.getPuppet().getCellIndex(); }
    protected List<Connection> getAllConnections() { return gp.getAllConnections(); }
}
