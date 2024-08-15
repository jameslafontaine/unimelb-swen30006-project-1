package snakeladder.game;

import ch.aplu.jgamegrid.*;
import java.awt.Point;

public class Puppet extends Actor {
  private GamePane gamePane;
  private NavigationPane navigationPane;
  private int cellIndex = 0;
  private int nbSteps;
  private Connection currentCon = null;
  private int y;
  private int dy;
  private boolean isAuto;
  private String puppetName;
  private Puppet opponent;
  private boolean conTraversed = false;
  private static boolean movingBack = false;

  Puppet(GamePane gp, NavigationPane np, String puppetImage) {
    super(puppetImage);
    this.gamePane = gp;
    this.navigationPane = np;
  }

  public boolean isAuto() {
    return isAuto;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public String getPuppetName() {
    return puppetName;
  }

  public Connection getCurrentCon() {
    return currentCon;
  }

  public int getDy() {
    return dy;
  }

  int getCellIndex() {
    return cellIndex;
  }

  public boolean getConTraversed() {
    return conTraversed;
  }

  public void setMovingBack(boolean condition) {
    movingBack = condition;
  }

  public void setPuppetName(String puppetName) {
    this.puppetName = puppetName;
  }


  void go(int nbSteps) {
    if (cellIndex == 100)  // after game over
    {
      cellIndex = 0;
      setLocation(gamePane.startLocation);
    }
    this.nbSteps = nbSteps;
    setActEnabled(true);
  }

  void resetToStartingPoint() {
    cellIndex = 0;
    setLocation(gamePane.startLocation);
    setActEnabled(true);
  }


  private void moveToNextCell() {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
    {
      if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() + 1, getY()));
    } else     // Cells starting left 20, 40, .. 100
    {
      if (ones == 0)
        setLocation(new Location(getX(), getY() - 1));
      else
        setLocation(new Location(getX() - 1, getY()));
    }
    cellIndex++;
  }

  private void moveToPreviousCell() {
    int tens = cellIndex / 10;
    int ones = cellIndex - tens * 10;
    if (tens % 2 == 0)     // Cells starting left 01, 21, .. 81
    {
      if (ones == 1 && cellIndex > 1)
        setLocation(new Location(getX(), getY() + 1));
      else if (ones == 0 && cellIndex > 0)
        setLocation(new Location(getX() + 1, getY()));
      else
        setLocation(new Location(getX() - 1, getY()));
    } else     // Cells starting left 20, 40, .. 100
    {
      if (ones == 1)
        setLocation(new Location(getX(), getY() + 1));
      else if (ones == 0)
        setLocation(new Location(getX() - 1, getY()));
      else
        setLocation(new Location(getX() + 1, getY()));
    }
    cellIndex--;
  }

  private void moveOnConnection() {
    int x = gamePane.x(y, currentCon);
    System.out.println("x assigned value");
    setPixelLocation(new Point(x, y));
    System.out.println("setPixelLocation called");
    y += dy;
    System.out.println("y += dy");

    // Check end of connection
    if ((dy > 0 && (y - gamePane.toPoint(currentCon.locEnd).y) > 0)
            || (dy < 0 && (y - gamePane.toPoint(currentCon.locEnd).y) < 0)) {
      System.out.println("end of connection reached");
      gamePane.setSimulationPeriod(100);
      setLocation(currentCon.locEnd);
      cellIndex = currentCon.cellEnd;
      setLocationOffset(new Point(0, 0));
      currentCon = null;
      conTraversed = true;
      // dont end turn if just moved opponent back
      if (!movingBack) {
        setActEnabled(false);
        navigationPane.prepareTurn(cellIndex);
      } else {
        setActEnabled(false); // does this break the code / is it necessary? probably better to leave this here if code still works
        movingBack = false;
        gamePane.getPuppet().checkCurrentConnection();
      }
    }
    return;
  }

  private void checkCurrentConnection() {
    if ((currentCon = gamePane.getConnectionAt(getLocation())) != null) {
      gamePane.setSimulationPeriod(50);
      y = gamePane.toPoint(currentCon.locStart).y;
      if (currentCon.locEnd.y > currentCon.locStart.y) {
        // if the current player made a minimum roll then end the turn without traversing down the connection
        if (navigationPane.getTotalRoll() == navigationPane.getNbDice() && this == gamePane.getPuppet()) {
          System.out.println("The player has made a minimum roll so will not traverse down this connection");
          setActEnabled(false);
          currentCon = null;
          navigationPane.prepareTurn(cellIndex);
        }
        else {
          dy = gamePane.animationStep;
          System.out.println("isCurrentCon = " + this.getCurrentCon() != null);
          System.out.println("dy = " + dy + " for " + this.puppetName);
        }
      } else if (currentCon.locEnd.y < currentCon.locStart.y) {
        dy = -gamePane.animationStep;
        System.out.println("dy = " + dy + " for " + this.puppetName);
        System.out.println("isCurrentCon = " + this.getCurrentCon() != null);
      }

      if (currentCon instanceof Snake) {
        navigationPane.showStatus("Digesting...");
        navigationPane.playSound(GGSound.MMM);
      } else if (currentCon instanceof Ladder) {
        navigationPane.showStatus("Climbing...");
        navigationPane.playSound(GGSound.BOING);
      }
    } else {
      setActEnabled(false);
      System.out.println("No current connection so setting act enabled to false for puppet: " + this.getPuppetName());
      // dont end turn if just moving opponent
      if (!movingBack) {
        navigationPane.prepareTurn(cellIndex);
      } else {
        movingBack = false;
      }
    }
  }

  private void checkOpponentSquare() { // check if player landed in same square as opponent w/o using connection
    opponent = gamePane.getOpponentPuppet();
    if (cellIndex == opponent.getCellIndex() && !conTraversed && cellIndex != 0) {
      opponent.setActEnabled(true);
      movingBack = true;
      opponent.moveToPreviousCell();
      opponent.checkCurrentConnection();
    }
  }

  public void act() {
    if ((cellIndex / 10) % 2 == 0) {
      if (isHorzMirror())
        setHorzMirror(false);
    } else {
      if (!isHorzMirror())
        setHorzMirror(true);
    }

    // Animation: Move on connection
    if (currentCon != null) {
      moveOnConnection();
      return;
    }

    // Normal movement
    if (nbSteps > 0) {
      moveToNextCell();

      if (cellIndex == 100)  // Game over
      {
        conTraversed = false;
        setActEnabled(false);
        navigationPane.prepareTurn(cellIndex);
        return;
      }

      nbSteps--;
      if (nbSteps == 0) {
        conTraversed = false;
        checkOpponentSquare();
        if (!movingBack)
          checkCurrentConnection();
      }
    }
  }
}

