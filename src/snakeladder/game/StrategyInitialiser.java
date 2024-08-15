package snakeladder.game;

public class StrategyInitialiser {
    private Strategy strategy;

    public StrategyInitialiser(GamePane gp, NavigationPane np) {
        strategy = new SimpleStrategy(gp, np); // initialise the desired strategy here
    }

    public Strategy getStrategy() { return strategy; }
}
