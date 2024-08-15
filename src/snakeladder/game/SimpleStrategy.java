package snakeladder.game;

import java.util.List;

public class SimpleStrategy extends Strategy {

    public SimpleStrategy(GamePane gp, NavigationPane np) {
        super(gp, np);
    }

    public boolean checkStrategy() {
        int numDownPaths = 0;
        int numUpPaths = 0;
        int opponentPosition = getOpponentPosition();
        List<Connection> connections = getAllConnections();
        for (Connection connection : connections) {
            if (connection.getCellStart() > getNumberDice() + opponentPosition &&
                    connection.getCellStart() < (getNumberDice() * 6) + opponentPosition) {
                if ((connection instanceof Snake && !getIsToggle()) || (connection instanceof Ladder && getIsToggle())) {
                    numDownPaths += 1;
                } else {
                    numUpPaths += 1;
                }
            }
        }

        if (numUpPaths >= numDownPaths)
            return true;
        else
            return false;
    }
}
