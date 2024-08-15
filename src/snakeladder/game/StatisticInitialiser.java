package snakeladder.game;

import java.util.ArrayList;

public class StatisticInitialiser {
    private ArrayList<Statistic> statistics = new ArrayList<>();

    public StatisticInitialiser(GamePane gp, NavigationPane np) {
        Statistic dieValueStatistic = new DieValueStatistic(gp, np); // initialise new statistics here and add them to the statistics array list
        Statistic pathTraversalStatistic = new PathTraversalStatistic(gp, np);
        statistics.add(dieValueStatistic);
        statistics.add(pathTraversalStatistic);
    }

    public ArrayList<Statistic> getStatistics() { return statistics; }
}
