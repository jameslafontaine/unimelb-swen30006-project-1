package snakeladder.game;

import ch.aplu.jgamegrid.Actor;

public class Die extends Actor
{
  private int nb;

  Die(int nb)
  {
    super("sprites/pips" + nb + ".gif", 7);
  }

  public void act()
  {
    showNextSprite();
    if (getIdVisible() == 6)
    {
      setActEnabled(false);
      // np.startMoving(nb); CHANGED
    }
  }
}
