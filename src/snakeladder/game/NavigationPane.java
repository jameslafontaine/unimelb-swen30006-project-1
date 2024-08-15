package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import ch.aplu.util.*;
import snakeladder.game.custom.CustomGGButton;
import snakeladder.utility.ServicesRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("serial")
public class NavigationPane extends GameGrid
  implements GGButtonListener
{
  // players.x.isAuto must be set to false for both players and autorun = false to properly use interactive behaviour
  private class SimulatedPlayer extends Thread
  {
    public void run()
    {
      while (true)
      {
        Monitor.putSleep();
        rollAllDice();
      }
    }
  }

  private final int DIE1_BUTTON_TAG = 1;
  private final int DIE2_BUTTON_TAG = 2;
  private final int DIE3_BUTTON_TAG = 3;
  private final int DIE4_BUTTON_TAG = 4;
  private final int DIE5_BUTTON_TAG = 5;
  private final int DIE6_BUTTON_TAG = 6;
  private final int MAX_NUM_DICE = 6;

  private final Location handBtnLocation = new Location(110, 70);
  private final Location dieBoardLocation = new Location(100, 180);
  private final Location pipsLocation = new Location(70, 230);
  private final Location statusLocation = new Location(20, 330);
  private final Location statusDisplayLocation = new Location(100, 320);
  private final Location scoreLocation = new Location(20, 430);
  private final Location scoreDisplayLocation = new Location(100, 430);
  private final Location resultLocation = new Location(20, 495);
  private final Location resultDisplayLocation = new Location(100, 495);

  private final Location autoChkLocation = new Location(15, 375);
  private final Location toggleModeLocation = new Location(95, 375);

  private final Location die1Location = new Location(20, 270);
  private final Location die2Location = new Location(50, 270);
  private final Location die3Location = new Location(80, 270);
  private final Location die4Location = new Location(110, 270);
  private final Location die5Location = new Location(140, 270);
  private final Location die6Location = new Location(170, 270);

  private GamePane gp;
  private GGButton handBtn = new GGButton("sprites/handx.gif");

  private GGButton die1Button = new CustomGGButton(DIE1_BUTTON_TAG, "sprites/Number_1.png");
  private GGButton die2Button = new CustomGGButton(DIE2_BUTTON_TAG, "sprites/Number_2.png");
  private GGButton die3Button = new CustomGGButton(DIE3_BUTTON_TAG, "sprites/Number_3.png");
  private GGButton die4Button = new CustomGGButton(DIE4_BUTTON_TAG, "sprites/Number_4.png");
  private GGButton die5Button = new CustomGGButton(DIE5_BUTTON_TAG, "sprites/Number_5.png");
  private GGButton die6Button = new CustomGGButton(DIE6_BUTTON_TAG, "sprites/Number_6.png");

  private GGTextField pipsField;
  private GGTextField statusField;
  private GGTextField resultField;
  private GGTextField scoreField;
  private boolean isAuto;
  private GGCheckButton autoChk;
  private boolean isToggle = false;
  private GGCheckButton toggleCheck =
          new GGCheckButton("Toggle Mode", YELLOW, TRANSPARENT, isToggle);
  private int nbRolls = 0;
  private volatile boolean isGameOver = false;
  private Properties properties;
  private java.util.List<java.util.List<Integer>> dieValues = new ArrayList<>();
  private GamePlayCallback gamePlayCallback;
  private int nbDice;
  private int totalRoll = 0;
  private int currentDie = 0;

  private List<Statistic> statistics = new ArrayList<>();


  private Strategy strategy;

  NavigationPane(Properties properties)
  {
    this.properties = properties;
    nbDice = // Number of six-sided dice
    (properties.getProperty("dice.count") == null || Integer.parseInt(properties.getProperty("dice.count")) > MAX_NUM_DICE)
            ? 1  // default
            : Integer.parseInt(properties.getProperty("dice.count"));
    System.out.println("numberOfDice = " + nbDice);
    isAuto = Boolean.parseBoolean(properties.getProperty("autorun"));
    autoChk = new GGCheckButton("Auto Run", YELLOW, TRANSPARENT, isAuto);
    System.out.println("autorun = " + isAuto);
    setSimulationPeriod(200);
    setBgImagePath("sprites/navigationpane.png");
    setCellSize(1);
    setNbHorzCells(200);
    setNbVertCells(600);
    doRun();
    new SimulatedPlayer().start();
  }

  void setupDieValues() {
    for (int i = 0; i < gp.getNumberOfPlayers(); i++) {
      java.util.List<Integer> dieValuesForPlayer = new ArrayList<>();
      if (properties.getProperty("die_values." + i) != null) {
        String dieValuesString = properties.getProperty("die_values." + i);
        String[] dieValueStrings = dieValuesString.split(",");
        for (int j = 0; j < dieValueStrings.length; j++) {
          dieValuesForPlayer.add(Integer.parseInt(dieValueStrings[j]));
        }
        dieValues.add(dieValuesForPlayer);
      } else {
        System.out.println("All players need to be set a die value for the full testing mode to run. " +
                "Switching off the full testing mode");
        dieValues = null;
        break;
      }
    }
    System.out.println("dieValues = " + dieValues);
  }

  int getTotalRoll() {
    return totalRoll;
  }

  int getNbRolls() {
    return nbRolls;
  }

  int getNbDice() {
    return nbDice;
  }

  boolean getIsToggle() { return isToggle; }

  void setGamePlayCallback(GamePlayCallback gamePlayCallback) {
    this.gamePlayCallback = gamePlayCallback;
  }

  void setGamePane(GamePane gp)
  {
    this.gp = gp;
    setupDieValues();
  }

  void initialiseStrategy() {
    StrategyInitialiser strategyInitialiser = new StrategyInitialiser(gp, this);
    strategy = strategyInitialiser.getStrategy();
  }

  void initialiseStatistics() {
      StatisticInitialiser statisticInitialiser = new StatisticInitialiser(gp, this);
      for (Statistic statistic: statisticInitialiser.getStatistics()) {
        statistic.initialiseStatistic();
        statistics.add(statistic);
      }

  }

  private void rollAllDice() {
    int currentDieValue;
    for (int currentDie=0; currentDie < nbDice; currentDie++) {
      handBtn.show(1);
      currentDieValue = getDieValue(currentDie);
      totalRoll += currentDieValue;
      roll(currentDieValue);
      delay(1000);
      handBtn.show(0);
      delay(1000);
    }
    gp.getPuppet().setMovingBack(false);
    startMoving(totalRoll);
  }

  class ManualDieButton implements GGButtonListener {
    @Override
    public void buttonPressed(GGButton ggButton) {

    }

    @Override
    public void buttonReleased(GGButton ggButton) {

    }

    @Override
    public void buttonClicked(GGButton ggButton) {
      System.out.println("manual die button clicked");
      if (ggButton instanceof CustomGGButton) {
        CustomGGButton customGGButton = (CustomGGButton) ggButton;
        int tag = customGGButton.getTag();
        if (currentDie < nbDice - 1) {
          rollOneDie(tag);
        } else if (currentDie < nbDice) {
          rollOneDie(tag);
          startMoving(totalRoll);
        }
      }
    }

    private void rollOneDie(int tag) {
      currentDie++;
      handBtn.show(1);
      prepareBeforeRoll();
      totalRoll += tag;
      roll(tag);
      System.out.println("manual die button clicked - tag: " + tag);
      delay(1000);
      handBtn.show(0);
      delay(1000);
    }
  }

  void addDieButtons() {
    ManualDieButton manualDieButton = new ManualDieButton();

    addActor(die1Button, die1Location);
    addActor(die2Button, die2Location);
    addActor(die3Button, die3Location);
    addActor(die4Button, die4Location);
    addActor(die5Button, die5Location);
    addActor(die6Button, die6Location);

    die1Button.addButtonListener(manualDieButton);
    die2Button.addButtonListener(manualDieButton);
    die3Button.addButtonListener(manualDieButton);
    die4Button.addButtonListener(manualDieButton);
    die5Button.addButtonListener(manualDieButton);
    die6Button.addButtonListener(manualDieButton);
  }


  private int getDieValue(int currentDie) {
    if (dieValues == null) {
        return ServicesRandom.get().nextInt(6) + 1;
    }
    int currentRound = nbRolls / (gp.getNumberOfPlayers() * nbDice);
    // System.out.println("currentRound = " + currentRound);
    int playerIndex = (nbRolls / nbDice) % gp.getNumberOfPlayers();
    // System.out.println("playerIndex = " + playerIndex);
    if (dieValues.get(playerIndex).size() > (currentRound * nbDice) + currentDie) {
      // System.out.println("Retrieved die value = " + dieValues.get(playerIndex).get(currentRound * nbDice + currentDie));
      return dieValues.get(playerIndex).get(currentRound * nbDice + currentDie);
    }
    return ServicesRandom.get().nextInt(6) + 1;
  }

  void createGui()
  {
    addActor(new Actor("sprites/dieboard.gif"), dieBoardLocation);

    handBtn.addButtonListener(this);
    addActor(handBtn, handBtnLocation);
    addActor(autoChk, autoChkLocation);
    autoChk.addCheckButtonListener(new GGCheckButtonListener() {
      @Override
      public void buttonChecked(GGCheckButton button, boolean checked)
      {
        isAuto = checked;
        if (isAuto)
          Monitor.wakeUp();
      }
    });

    addActor(toggleCheck, toggleModeLocation);
    toggleCheck.addCheckButtonListener(new GGCheckButtonListener() {
      @Override
      public void buttonChecked(GGCheckButton ggCheckButton, boolean checked) {
        isToggle = checked;
        // System.out.println("isToggle = " + isToggle);
        // System.out.println("Mode has been toggled");
        for (Connection connection: gp.getAllConnections()) {
          connection.switchConnection();
        }
      }
    });

    addDieButtons();

    pipsField = new GGTextField(this, "", pipsLocation, false);
    pipsField.setFont(new Font("Arial", Font.PLAIN, 16));
    pipsField.setTextColor(YELLOW);
    pipsField.show();

    addActor(new Actor("sprites/linedisplay.gif"), statusDisplayLocation);
    statusField = new GGTextField(this, "Click the hand!", statusLocation, false);
    statusField.setFont(new Font("Arial", Font.PLAIN, 16));
    statusField.setTextColor(YELLOW);
    statusField.show();

    addActor(new Actor("sprites/linedisplay.gif"), scoreDisplayLocation);
    scoreField = new GGTextField(this, "# Rolls: 0", scoreLocation, false);
    scoreField.setFont(new Font("Arial", Font.PLAIN, 16));
    scoreField.setTextColor(YELLOW);
    scoreField.show();

    addActor(new Actor("sprites/linedisplay.gif"), resultDisplayLocation);
    resultField = new GGTextField(this, "Current pos: 0", resultLocation, false);
    resultField.setFont(new Font("Arial", Font.PLAIN, 16));
    resultField.setTextColor(YELLOW);
    resultField.show();
  }

  void showPips(String text)
  {
    pipsField.setText(text);
    if (text != "") System.out.println(text);
  }

  void showStatus(String text)
  {
    statusField.setText(text);
    System.out.println("Status: " + text);
  }

  void showScore(String text)
  {
    scoreField.setText(text);
    System.out.println(text);
  }

  void showResult(String text)
  {
    resultField.setText(text);
    System.out.println("Result: " + text);
  }


  private void toggleMode() {
    toggleCheck.setChecked(!isToggle);
    isToggle = !isToggle;
    for (Connection connection: gp.getAllConnections()) {
      connection.switchConnection();
    }
  }

  void prepareTurn(int currentIndex)
  {
    if (currentIndex == 100)  // Game over
    {
      // gp.getPuppet().setConTraversed(false);
      for (Statistic statistic: statistics) {
        statistic.trackStatistic();
      }
      playSound(GGSound.FADE);
      showStatus("Click the hand!");
      showResult("Game over");
      for (Statistic statistic: statistics) {
        statistic.printStatistic();
      }
      isGameOver = true;
      handBtn.setEnabled(true);

      java.util.List  <String> playerPositions = new ArrayList<>();
      for (Puppet puppet: gp.getAllPuppets()) {
        playerPositions.add(puppet.getCellIndex() + "");
      }
      gamePlayCallback.finishGameWithResults(nbRolls % gp.getNumberOfPlayers(), playerPositions);
      gp.resetAllPuppets();
    }
    else
    {
      playSound(GGSound.CLICK);
      showStatus("Done. Click the hand!");
      String result = gp.getPuppet().getPuppetName() + " - pos: " + currentIndex;
      showResult(result);
      if (strategy != null) {
        if (strategy.checkStrategy() == true) {
          toggleMode();
        }
      }
      for (Statistic statistic: statistics) {
        statistic.trackStatistic();
      }
      gp.switchToNextPuppet();
      totalRoll = 0;
      currentDie = 0;
      System.out.println("current puppet - auto: " + gp.getPuppet().getPuppetName() + "  " + gp.getPuppet().isAuto() );

      if (isAuto) {
        Monitor.wakeUp();
      } else if (gp.getPuppet().isAuto()) {
        Monitor.wakeUp();
      } else {
        handBtn.setEnabled(true);
      }
    }
  }

  void displayRollInformation(int nb)
  {
    showPips("Pips: " + nb);
    showScore("# Rolls: " + (++nbRolls));
  }

  void startMoving(int nb)
  {
    showStatus("Moving...");
    gp.getPuppet().go(nb);
  }

  void prepareBeforeRoll() {
    handBtn.setEnabled(false);
    if (isGameOver)  // First click after game over
    {
      isGameOver = false;
      nbRolls = 0;
    }
  }

  public void buttonClicked(GGButton btn)
  {
    System.out.println("hand button clicked");
    prepareBeforeRoll();
    rollAllDice();
  }

  private void roll(int rollNumber)
  {
    int nb = rollNumber;
    showStatus("Rolling...");
    showPips("");

    if (nbRolls % nbDice == 0 && nbRolls != 0) {
      removeActors(Die.class);
      // System.out.println("Removing dice classes");
    }
    Die die = new Die(nb);
    addActor(die, dieBoardLocation);
    displayRollInformation(nb);
  }

  public void buttonPressed(GGButton btn)
  {
  }

  public void buttonReleased(GGButton btn)
  {
  }

  public void checkAuto() {
    if (isAuto) Monitor.wakeUp();
  }
}
