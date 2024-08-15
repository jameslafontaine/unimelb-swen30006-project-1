package snakeladder.game;

import java.util.HashMap;

public class PathTraversalStatistic extends Statistic {

    private HashMap<Integer, Integer> traversalValuesP1 = new HashMap<>();
    private HashMap<Integer, Integer> traversalValuesP2 = new HashMap<>();

    private static final int UP_PATH = 0;
    private static final int DOWN_PATH = 1;

    private boolean p1JustTraversedCon = false;
    private boolean p2JustTraversedCon = false;

    public PathTraversalStatistic(GamePane gp, NavigationPane np) {
        super(gp, np);
    }

    public void initialiseStatistic() {
        traversalValuesP1.put(UP_PATH, 0); // up path traversals player 1
        traversalValuesP1.put(DOWN_PATH, 0); // down path traversals player 1
        traversalValuesP2.put(UP_PATH, 0);
        traversalValuesP2.put(DOWN_PATH, 0);
        // System.out.println("TRAVERSAL VALUES INITIALISED");
    }

    public void trackStatistic() {
        if (hasTraversedDownPath() && hasTraversedConnection()) {
            updateTraversalValue(checkCurrentPlayerTurn(), DOWN_PATH);
        }
        else if (hasTraversedUpPath() && hasTraversedConnection()) {
            updateTraversalValue(checkCurrentPlayerTurn(), UP_PATH);
        }
        else if (checkCurrentPlayerTurn() == PLAYER_ONE) {
            p1JustTraversedCon = false;
        } else
            p2JustTraversedCon = false;
        // check if opponent moved during a push back
        if (hasOpponentTraversedDownPath() && hasOpponentTraversedConnection()) {
            if (checkCurrentPlayerTurn() == PLAYER_ONE && !p2JustTraversedCon) {
                updateTraversalValue(checkCurrentPlayerTurn(), DOWN_PATH);
            }
            else if (checkCurrentPlayerTurn() == PLAYER_TWO && !p1JustTraversedCon) {
                updateTraversalValue(checkCurrentPlayerTurn(), DOWN_PATH);
            }
        }
        else if (hasOpponentTraversedUpPath() && hasOpponentTraversedConnection() && !hasGameEnded()) {
            if (checkCurrentPlayerTurn() == PLAYER_ONE && !p2JustTraversedCon) {
                updateTraversalValue(checkCurrentPlayerTurn(), UP_PATH);
            }
            else if (checkCurrentPlayerTurn() == PLAYER_TWO && !p1JustTraversedCon){
                updateTraversalValue(checkCurrentPlayerTurn(), UP_PATH);
            }
        }
    }

    public void printStatistic(){
        System.out.println("Player 1 traversed: up-" + traversalValuesP1.get(UP_PATH) + ", down-" + traversalValuesP1.get(DOWN_PATH));
        System.out.println("Player 2 traversed: up-" + traversalValuesP2.get(UP_PATH) + ", down-" + traversalValuesP2.get(DOWN_PATH));
    }

    private void updateTraversalValue(int player, int path){
        if (player == PLAYER_ONE) {
            if (path == DOWN_PATH) {
                traversalValuesP1.put(DOWN_PATH, traversalValuesP1.get(DOWN_PATH) + 1);
            } else {
                traversalValuesP1.put(UP_PATH, traversalValuesP1.get(UP_PATH) + 1);
            }
            p1JustTraversedCon = true;
        } else {
            if (path == DOWN_PATH){
                traversalValuesP2.put(DOWN_PATH, traversalValuesP2.get(DOWN_PATH) + 1);
            } else {
                traversalValuesP2.put(UP_PATH, traversalValuesP2.get(UP_PATH) + 1);
            }
            p2JustTraversedCon = true;
        }
    }



}
