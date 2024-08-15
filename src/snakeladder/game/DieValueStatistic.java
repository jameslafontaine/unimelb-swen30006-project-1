package snakeladder.game;

import java.util.HashMap;

public class DieValueStatistic extends Statistic {
    private HashMap<Integer, Integer> dieValuesP1 = new HashMap<>();
    private HashMap<Integer, Integer> dieValuesP2 = new HashMap<>();

    public DieValueStatistic(GamePane gp, NavigationPane np) {
        super(gp, np);
    }

    public void initialiseStatistic() {
        int nbDice = getNumberDice();
        for (int i=nbDice; i <= nbDice * 6; i++) {
            dieValuesP1.put(i, 0);
            dieValuesP2.put(i, 0);
        }
    }

    public void trackStatistic() {
        if (checkCurrentPlayerTurn() == PLAYER_ONE) {   // player one
            dieValuesP1.put(getTotalRoll(), dieValuesP1.get(getTotalRoll()) + 1);
        } else {                                        // player two
            dieValuesP2.put(getTotalRoll(), dieValuesP2.get(getTotalRoll()) + 1);
        }
    }

    public void printStatistic() {
        System.out.print("Player 1 rolled: ");
        printDieValues(dieValuesP1);
        System.out.print("Player 2 rolled: ");
        printDieValues(dieValuesP2);


    }

    private void printDieValues(HashMap<Integer, Integer> dieValues) {
        for (Integer dieValue: dieValues.keySet()) {
            if (getNumberDice() == dieValue) {    // first roll value
                System.out.print(dieValue + "-" + dieValues.get(dieValue));
                continue;
            }
            System.out.print(", " + dieValue + "-" + dieValues.get(dieValue));
        }
        System.out.print("\n");
    }
}
