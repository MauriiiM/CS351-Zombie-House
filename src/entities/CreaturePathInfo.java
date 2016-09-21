package entities;

/**
 * @author Mauricio Monsivais, Javier Zazuta
 *         Created by mmonsivais on 9/20/16.
 */
public class CreaturePathInfo
{
  private double x, z, angle;
  //stores t/f value if player attack at that instance as a byte because so many of these are being store so we need as little memory as possible
  private byte didAttack;

  public CreaturePathInfo(double x, double z, double angle, byte didAttack)
  {
    this.x = x;
    this.z = z;
    this.angle = angle;
    this.didAttack = didAttack;
  }

  public double getX()
  {
    return x;
  }

  public void setX(double x)
  {
    this.x = x;
  }

  public double getZ()
  {
    return z;
  }

  public void setZ(double z)
  {
    this.z = z;
  }

  public double getAngle()
  {
    return angle;
  }

  public void setAngle(double angle)
  {
    this.angle = angle;
  }

  public byte getDidAttack()
  {
    return didAttack;
  }

  public void setDidAttack(byte didAttack)
  {
    this.didAttack = didAttack;
  }
}
