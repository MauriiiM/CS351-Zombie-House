package entities;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Javier Zazueta
 * @author Mauricio Monsivais
 */
public class PlayerGhost extends Creature
{
  protected ArrayList<Point2D.Double> path;

  public PlayerGhost(ArrayList<Point2D.Double> path)
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
