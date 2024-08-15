package snakeladder.game;

public abstract class Statistic {
    protected GamePane gp;
    protected NavigationPane np;

    protected static final int PLAYER_ONE = 0;
    protected static final int PLAYER_TWO = 1;

    public Statistic (GamePane gp, NavigationPane np) {
        this.gp = gp;
        this.np = np;
    }

    public abstract void initialiseStatistic();
    public abstract void trackStatistic();
    public abstract void printStatistic();

    protected int checkCurrentPlayerTurn() { return gp.getCurrentPuppetIndex(); }
    protected boolean hasGameEnded() { return gp.getPuppet().getCellIndex() >= 100; }
    protected boolean hasTraversedConnection() { return gp.getPuppet().getConTraversed(); }    // must be checked alongside whether an up or down path has been traversed
    protected boolean hasTraversedUpPath() { return gp.getPuppet().getDy() < 0; }
    protected boolean hasTraversedDownPath() { return gp.getPuppet().getDy() > 0; }
    protected boolean hasOpponentTraversedConnection() { return gp.getOpponentPuppet().getConTraversed(); }
    protected boolean hasOpponentTraversedUpPath() { return gp.getOpponentPuppet().getDy() < 0; }
    protected boolean hasOpponentTraversedDownPath() { return gp.getOpponentPuppet().getDy() > 0; }
    protected int getNumberDice() { return np.getNbDice(); }
    protected int getTotalRoll() { return np.getTotalRoll(); }

    // uses its own methods for easy of adding new statistics and ensuring low coupling between statistics and game pane and navigation pane
    // i.e. if methods change or functionality changes in game pane or navigation pane then it simply must be updated / added to
    // the statistic superclass and any subclass statistics will still function correctly as they use the superclass methods
}
