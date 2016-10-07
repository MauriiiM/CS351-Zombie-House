package entities;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Javier Zazueta
 * @author Mauricio Monsivais
 */
public class PlayerGhost extends Creature
{
  private ArrayList<CreaturePathInfo> path;

  public PlayerGhost(ArrayList<CreaturePathInfo> path)
  {
    this.path = path;
  }

  public void tick()
  {

  }

  public void stepSound()
  {

  }

  public double calculateDistance()
  {
    return 0;
  }
}
