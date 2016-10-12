package entities;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import game_engine.Attributes;
import graphing.GraphNode;
import graphing.TileGraph;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import levels.Tile;
import sounds.Sound;
import utilities.ZombieBoardRenderer;

/**
 * @author Atle Olson
 *         Jeffrey McCall
 *         Player object for the game. All methods having
 *         to do with the player object are in this class.
 */
public class Player extends Creature
{
  public static final double SPRINT_SPEED = Tile.tileSize / 4d;
  public static final double WALKING_SPEED = Tile.tileSize / 8d;
  private static final double START_X = 3;
  private static final double START_Y = 0;
  private static final double START_Z = 2;
  public Chainsaw chainsaw;
  private static final int MAX_HEALTH = 500;

  //entityManager
  EntityManager entityManager;

  //camera:
  public PerspectiveCamera camera;
  public PointLight light;
  public int brightness = 255;
  public boolean lightOn = true;

  //
  public double strafeVelocity;
  int counter = 0;

  //position and orientation:
  double newX = 0;
  double newZ = 0;
  double offSetX = 0;
  double offSetZ = 0;
  public double radius = .25;

  //atomic booleans:
  public AtomicBoolean shiftPressed = new AtomicBoolean(false);
  public AtomicBoolean wDown = new AtomicBoolean(false);
  public AtomicBoolean dDown = new AtomicBoolean(false);
  public AtomicBoolean aDown = new AtomicBoolean(false);
  public AtomicBoolean sDown = new AtomicBoolean(false);
  public AtomicBoolean gameIsRunning = new AtomicBoolean(true);
  public AtomicBoolean staminaOut = new AtomicBoolean(false);

  //other player fields:
  Cylinder boundingCircle = null;
  AtomicBoolean isDead = new AtomicBoolean(false);
  AtomicBoolean foundExit = new AtomicBoolean(false);

  //Player Movement
  public boolean turnLeft = false;
  public boolean turnRight = false;

  private double stamina = 5;
  private double regen = .2;
  private double deltaTime = 0;

  public int lives = 4; //for GUI displaying purposes
  private int numDeaths = 0; // for indexing creaturePathInfo array purposes
  private int damage = 15; //the damage taken by the player when hit by a zombie
  private int healthRegen = 5; //how fast the player heals when not taking damage(after two seconds)
  private byte didAttack = 0;

  private boolean gotHit = false;
  private int healTime = 0;

  public boolean attacking;


  /**
   * A constructor for a 3D player. takes in a camera object
   *
   * @param camera        camera object used for player sight
   * @param entityManager entityManager object which updates many of the player fields as
   *                      the game runs
   * @param light         The light that emanates from the player
   */
  public Player(PerspectiveCamera camera, EntityManager entityManager, PointLight light, Chainsaw chainsaw)
  {
    stepDistance = 3;
    this.entityManager = entityManager;
    this.xPos = START_X;
    this.yPos = START_Y;
    this.zPos = START_Z;
    this.velocity = 0;
    this.angle = 0;
    this.strafeVelocity = 0;
    this.chainsaw = chainsaw;
    camera.setRotate(this.angle);
    chainsaw.setRotate(this.angle);
    this.camera = camera;
    camera.setTranslateX(START_X);
    chainsaw.setTranslateX(START_X);
    camera.setTranslateZ(START_Z);
    chainsaw.setTranslateZ(START_Z);
    this.light = light;
    light.setRotationAxis(Rotate.Y_AXIS);
    boundingCircle = new Cylinder(radius, 1);
    PlayerStamina staminaCounter = new PlayerStamina();
    staminaCounter.start();
    boundingCircle.setTranslateX(camera.getTranslateX());
    boundingCircle.setTranslateZ(camera.getTranslateZ());
    lastX = camera.getTranslateX();
    lastZ = camera.getTranslateZ();
    pathTaken = new ArrayList<>();

    //give the player an initial health of 5
    health = MAX_HEALTH;
  }

  /**
   * A constructor for a 2D player.
   *
   * @param x x coordinate of the player
   * @param y y coordinate of the player
   */
  public Player(double x, double y)
  {
    this.xPos = x;
    this.yPos = y;
    this.velocity = 0;
    this.angle = 0;
  }

  /**
   * Called when LMB is clicked and used to attack Zombies
   */
  public void attack()
  {
    System.out.println("Attack");
    didAttack = 1;
  }

  public void notAttack()
  {
    didAttack = 0;
  }

  /**
   * Calculates Distance for camera
   *
   * @return The distance between lastX/Z and Camera.getTranslateX/Z
   */
  @Override
  public double calculateDistance()
  {
    double xDist = camera.getTranslateX() - lastX;
    double zDist = camera.getTranslateZ() - lastZ;
    return Math.sqrt((xDist * xDist) + (zDist * zDist));
  }

  /**
   * Plays player foot step sounds
   */
  @Override
  public void stepSound()
  {
    entityManager.soundManager.playSoundClip(Sound.footstep);
//    System.out.println(xPos + yPos + " in stepSound()");
  }

  public boolean getDidAttack()
  {
    if (didAttack == 1)
    {
      return true;
    }
    return false;
  }

  /**
   * Updates the player values when called from an animation timer
   * Implemented in 2 dimensions
   */
  public void tick2d()
  {
    if (xPos + (velocity * Math.cos(angle)) > 0 && yPos + (velocity * Math.sin(angle)) > 0 && xPos + (velocity * Math.cos(angle)) < ZombieBoardRenderer.boardWidth * ZombieBoardRenderer.cellSize && yPos + (velocity * Math.sin(angle)) < ZombieBoardRenderer.boardWidth * ZombieBoardRenderer.cellSize)
    {
      xPos += (velocity * Math.cos(angle));
      yPos += (velocity * Math.sin(angle));
    }
  }

  //return the number of times the player has die
  // this should be used to know how many clones there should be
  public int getNumDeaths()
  {
    return numDeaths;
  }

  /**
   * Updates the player values when called from an animation timer
   * Implemented in 3 dimensions
   */
  public void tick()
  {
    counter++;
    Cylinder tempX = new Cylinder(boundingCircle.getRadius(), boundingCircle.getHeight());
    Cylinder tempZ = new Cylinder(boundingCircle.getRadius(), boundingCircle.getHeight());

    double movementX = boundingCircle.getTranslateX();
    double movementZ = boundingCircle.getTranslateZ();

    movementX += (velocity * Math.sin(angle * (Math.PI / 180)));
    movementX += (strafeVelocity * Math.sin(angle * (Math.PI / 180) - Math.PI / 2));
    movementZ += (velocity * Math.cos(angle * (Math.PI / 180)));
    movementZ += (strafeVelocity * Math.cos(angle * (Math.PI / 180) - Math.PI / 2));


    tempX.setTranslateX(movementX);
    tempX.setTranslateZ(boundingCircle.getTranslateZ());

    tempZ.setTranslateX(boundingCircle.getTranslateX());
    tempZ.setTranslateZ(movementZ);

    Box collisionX = entityManager.getWallCollision(tempX);
    Box collisionZ = entityManager.getWallCollision(tempZ);

    if (turnLeft || turnRight)
    {
      if (turnLeft) angle -= Attributes.Player_Rotate_sensitivity;
      else angle += Attributes.Player_Rotate_sensitivity;
      camera.setRotate(angle);
      chainsaw.setRotate(angle);
    }

    lastX = camera.getTranslateX();
    lastZ = camera.getTranslateZ();

    if (collisionX == null)
    {

      camera.setTranslateX(movementX);
      //chainsaw.setTranslateX(movementX);
      chainsaw.setTranslateX(.45 * Math.sin(Math.toRadians(angle)) + movementX);
    }
    if (collisionZ == null)
    {
      //System.out.println("movememtZ " + movementZ);

      camera.setTranslateZ(movementZ);
      //chainsaw.setTranslateZ(movementZ);
      chainsaw.setTranslateZ(.45 * Math.cos(Math.toRadians(angle)) + movementZ - .4);
    }


    boundingCircle.setTranslateX(camera.getTranslateX());
    boundingCircle.setTranslateZ(camera.getTranslateZ());

    if (entityManager.checkPlayerCollision(boundingCircle))
    {
      //every time the zombie touches you, then you lose health
      health -= damage;
      //set gotHit to true and healTime back to zero because you only heal after two seconds of not getting hit
      gotHit = true;
      healTime = 0;

      if(health >= 0)
        light.setColor(Color.rgb((int) ((MAX_HEALTH + health) / 4), (int) ((health / 50) + 10), (int) ((health / 5) + 10)));

      //if the health is 0, or less than 0(which will only occur once) then you're dead
      if (health <= 0)
      {
        isDead.set(true);
      }
    }
    //the zombie is not touching you, and so you need to heal
    else if (gotHit)
    {
      healTime++;

      if (health < MAX_HEALTH && healTime >= 120)
      {
        health += healthRegen;
        light.setColor(Color.rgb((int) ((MAX_HEALTH + health) / 4), (int) (100 - (health / 5)), (int) (100 - (health / 5))));
      }
      //do not want the health to be greater than the MAX_HEALTH, then if it is
      //set it to MAX_HEALTH
      if (health >= MAX_HEALTH)
      {
        fullHealth();
      }
    }
    //System.out.println(health + "    in tick()");

    //checking for exit collision
    for (Box box : entityManager.zombieHouse.exits)
    {
      if (box.getBoundsInParent().intersects(boundingCircle.getBoundsInParent()))
      {
        foundExit.set(true);
        System.out.println("exit");
      }
    }

    if (shiftPressed.get() && !staminaOut.get())
    {
      if (wDown.get()) velocity = SPRINT_SPEED;
      if (sDown.get()) velocity = -SPRINT_SPEED;
      if (aDown.get()) strafeVelocity = SPRINT_SPEED;
      if (dDown.get()) strafeVelocity = -SPRINT_SPEED;
    }
    if (staminaOut.get())
    {
      if (wDown.get()) velocity = WALKING_SPEED;
      if (sDown.get()) velocity = -WALKING_SPEED;
      if (aDown.get()) strafeVelocity = WALKING_SPEED;
      if (dDown.get()) strafeVelocity = -WALKING_SPEED;
    }

    updateDistance();
    light.setTranslateX(camera.getTranslateX());
    light.setTranslateZ(camera.getTranslateZ());
    light.setRotate(camera.getRotate() - 180);
    xPos = camera.getTranslateX();
    zPos = camera.getTranslateZ();

    //adds EVERY step taken to path. There'll be many repeats because it records how long player stays there
    pathTaken.add(new CreaturePathInfo((float) xPos, (float) zPos, (float) angle, didAttack));
    if (didAttack == 1) notAttack();
    //System.out.println("x= " + xPos + ",\t z= " + zPos + " in tick()");


  }

  /**
   * Clears Data from previous Game
   */
  void dispose()
  {
    camera = null;
    light = null;
    boundingCircle = null;
  }

  void reset()
  {
    xPos = START_X;
    yPos = START_Y;
    zPos = START_Z;
    camera.setTranslateX(START_X);
    chainsaw.setTranslateX(START_X);
    camera.setTranslateZ(START_Z);
    chainsaw.setTranslateZ(START_Z);
    boundingCircle.setTranslateX(START_X);
    boundingCircle.setTranslateZ(START_Z);
    lives--;
    numDeaths++;
    fullHealth();
  }

  /**
   * Get the current GraphNode object that represents the tile that the player
   * is standing on.
   *
   * @return The GraphNode that represents the tile that the player is standing
   * on.
   */
  GraphNode getCurrentNode()
  {
    GraphNode currentNode = null;
    Tile currentTile = null;
    double currentX = boundingCircle.getTranslateX();
    double currentZ = boundingCircle.getTranslateZ();
    currentTile = entityManager.zombieHouse.getGameBoard()[(int) currentZ][(int) currentX];
    if (TileGraph.tileGraph.containsKey(currentTile))
    {
      currentNode = TileGraph.tileGraph.get(currentTile);
      return currentNode;
    }
    return currentNode;
  }

  /**
   * Get the current GraphNode object that represents the tile that the player
   * is standing on. This is the same as the previous method except that it is
   * called for the 2D board, not the 3D one.
   *
   * @return The GraphNode that represents the tile that the player is standing
   * on.
   */
  GraphNode getCurrent2dNode()
  {
    GraphNode currentNode = null;
    Tile currentTile = null;
    double currentX = xPos / ZombieBoardRenderer.cellSize;
    double currentY = yPos / ZombieBoardRenderer.cellSize;
    currentTile = ZombieBoardRenderer.gameBoard[(int) currentY][(int) currentX];
    if (TileGraph.tileGraph.containsKey(currentTile))
    {
      currentNode = TileGraph.tileGraph.get(currentTile);
      return currentNode;
    }
    return currentNode;
  }

  private void fullHealth()
  {
    health = MAX_HEALTH;
    healTime = 0;
    gotHit = false;
    light.setColor(Color.WHITE);
  }

  /**
   * @author Jeffrey McCall
   *         This class keeps track of player stamina. While the player
   *         is running, the stamina is decremented until it reaches 0. At that time,
   *         the player can't run until the stamina regenerates. This class takes care
   *         of decrementing and regenerating stamina.
   */
  private class PlayerStamina extends Thread
  {
    /**
     * Once every second, decrement stamina if shift is pressed.
     * If stamina reaches 0, regenerate stamina at a constant rate
     * once every second until stamina reaches max of 5. Exit thread if
     * program is closed.
     */
    @Override
    public void run()
    {
      while (gameIsRunning.get())
      {
        try
        {
          sleep(1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        if (shiftPressed.get() && !staminaOut.get())
        {
          stamina--;
          if (stamina == 0) staminaOut.set(true);

        }
        else if (!shiftPressed.get())
        {
          deltaTime++;
          if (((deltaTime * regen) + stamina) <= 5)
          {
            stamina += deltaTime * regen;
          }
          else
          {
            stamina = 5;
            deltaTime = 0;
            staminaOut.set(false);
          }
        }
      }
      System.exit(0);
    }
  }
}