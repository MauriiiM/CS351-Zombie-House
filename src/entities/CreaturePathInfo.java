package entities;

/**
 * @author Mauricio Monsivais, Javier Zazuta
 *         Created by mmonsivais on 9/20/16.
 */
public class CreaturePathInfo
{
  private float x, z, angle;
  //stores t/f value if player attack at that instance as a byte because so many of these are being store so we need as little memory as possible
  private byte didAttack, isDead;

  /**
   * @param x
   * @param z
   * @param angle
   * @param didAttack true being 1
   * @param isDead true being 1
   */
  public CreaturePathInfo(float x, float z, float angle, byte didAttack, byte isDead)
  {
    this.x = x;
    this.z = z;
    this.angle = angle;
    this.didAttack = didAttack;
    this.isDead = isDead;
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

  public byte getIsDead()
  {
    return isDead;
  }

  public void setIsDead(byte isDead)
  {
    this.isDead = isDead;
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
