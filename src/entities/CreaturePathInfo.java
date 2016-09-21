package entities;

/**
 * @author Mauricio Monsivais, Javier Zazuta
 *         Created by mmonsivais on 9/20/16.
 */
public class CreaturePathInfo
{
  private float x, z, angle;
  //stores t/f value if player attack at that instance as a byte because so many of these are being store so we need as little memory as possible
  private byte didAttack;

  public CreaturePathInfo(float x, float z, float angle, byte didAttack)
  {
    this.x = x;
    this.z = z;
    this.angle = angle;
    this.didAttack = didAttack;
  }

  public float getX()
  {
    return x;
  }

  public void setX(float x)
  {
    this.x = x;
  }

  public float getZ()
  {
    return z;
  }

  public void setZ(float z)
  {
    this.z = z;
  }

  public float getAngle()
  {
    return angle;
  }

  public void setAngle(float angle)
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
