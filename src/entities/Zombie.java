package entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import game_engine.Attributes;
import graphing.GraphNode;
import graphing.Heading;
import graphing.NodeComparator;
import graphing.TileGraph;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import levels.Tile;
import sounds.Sound;
import utilities.ZombieBoardRenderer;

/**
 * @author Jeffrey McCall
 *         Atle Olson
 *         Ben Matthews
 *         Sets and contains all of the attributes of zombies in
 *         the game.
 */
public class Zombie extends Creature
{
  private final double START_X;
  private final double START_Z;
  private final int START_ROW;
  private final int START_COL;
  final Cylinder ZOMBIE_HITBOX;

  private EntityManager entityManager;
  private CalculatePath calcPath;

  boolean randomWalk = false;
  Random rand = new Random();
  double zombieWalkingSpeed = .01;
  double masterZombieSpeed = .05;
  double masterZombie2dSpeed = .3;
  double zombieSmell = 15.0;
  double twoDSpeed = (.5 / 60) * ZombieBoardRenderer.cellSize;
  private int twoDSize = 3;
  public boolean isMasterZombie = false;
  public Circle zombieCirc = null;
  public boolean twoDBoard = false;

  /*
   * Booleans used to keep track of events in the animation timer and the thread
   * in EntityManager that governs the decision rate of each zombie.
   */
  public AtomicBoolean gameIsRunning = new AtomicBoolean(true);
  private AtomicBoolean collisionDetected = new AtomicBoolean(false);
  AtomicBoolean angleAdjusted = new AtomicBoolean(false);
  private AtomicBoolean collisionJustDetected = new AtomicBoolean(false);
  public AtomicBoolean playerDetected = new AtomicBoolean(false);
  public AtomicBoolean goingAfterPlayer = new AtomicBoolean(false);
  AtomicBoolean findNewPath = new AtomicBoolean(false);
  AtomicBoolean masterZombieChasePlayer = new AtomicBoolean(false);
  private Tile tile;
  private int col;
  private int row;
  public Cylinder zombie = null;
  private Node[] zombieMesh = null;
  private double prevAngle = 0;
  private double lastAngle = 0;
  private Heading zombieHeading;
  private double lastX;
  private double lastZ;

  private boolean engaged = false;
  private int engagedCurrentGame = 0;
  private boolean dead = false;
  private byte isDeadInPath = 0;
  private byte didAttack = 0;
  private int fullHealth = 100;

  private int locationOnPath = 0;
  private byte takeHealth = 0;


  /**
   * Constructor that sets whether this zombie is a random walk zombie or a line
   * walk zombie. Also sets the values for the location of initial spawning
   * point of the zombie.
   */
  Zombie(Tile tile, EntityManager entityManager)
  {
    stepDistance = 1;

    this.entityManager = entityManager;
    calcPath  = new CalculatePath(entityManager, this);

    // 50% chance that the zombie is either a random
    // walk zombie or a line walk zombie.
    if (rand.nextInt(2) == 0)
    {
      randomWalk = true;
    }
    this.tile = tile;//for A* purposes i believe
    row = tile.row;
    col = tile.col;
    xPos = tile.xPos;
    zPos = tile.zPos;

    START_X = xPos;
    START_Z = zPos;
    START_ROW = row;
    START_COL = col;

    pathTaken = new ArrayList<>();
    health = fullHealth; // initialize the zombie health, they will not heal
    ZOMBIE_HITBOX = createZombieHitbox(tile.tileSize);
  }

  boolean isDead()
  {
    return dead;
  }

  void setDead(boolean dead)
  {
    isDeadInPath = 1;
    this.dead = dead;
  }

  public void setTakeHealth(byte takeHealth)
  {
    this.takeHealth = takeHealth;
  }

  public byte getTakeHealth()
  {
    return takeHealth;
  }


  void setMasterHealth()
  {
    health = 300;
  }

  double getHealth()
  {
    return health;
  }

  public int getCol()
  {
    return col;
  }

  public int getRow()
  {
    return row;
  }

  void setZombieHeading(Heading zombieHeading)
  {
    this.zombieHeading = zombieHeading;
  }

  void takeHealth()
  {
    health -= 8;
  }

  /**
   * @param newEngaged
   * @todo setEnganged(!getEngaged()) when damged, not setEngaged(true) because can be returned to false if that zombie killed player.
   */
  void setEngaged(boolean newEngaged)
  {
    engaged = newEngaged;
    engagedCurrentGame++;
  }

  boolean isEngaged()
  {
    return engaged;
  }

  /**
   * Creates a circle object that represents a zombie drawn on a 2D board. It is
   * given the initial x and y coordinates of the spawn point on the game map.
   *
   * @param zombieCounter The number of zombies to spawn.
   * @param row           The row of the 2D game map.
   * @param col           The column of the 2D game map.
   * @param zombies       The list of Zombie objects.
   * @param cellSize      The size of cells on the game map.
   */
  public void twoDZombie(int zombieCounter, int row, int col, ArrayList<Zombie> zombies, int cellSize)
  {
    Circle zombie = null;
    if (zombies.get(zombieCounter).col == col && zombies.get(zombieCounter).row == row)
    {
      double xPos = zombies.get(zombieCounter).xPos;
      double yPos = zombies.get(zombieCounter).zPos;
      zombie = new Circle((xPos * cellSize), (yPos * cellSize), twoDSize, Color.GREENYELLOW);
      zombieCirc = zombie;
    }
  }

  /**
   * Creates a cylinder that is placed around the zombie mesh. This is
   * used for collision detection. It is given
   * the initial x and z coordinates of the spawn point on the game map.
   *
   * @param cellSize The size of cells on the game map.
   */
  private Cylinder createZombieHitbox(int cellSize)
  {
    Cylinder cylinder = new Cylinder(.2, 1);
    cylinder.setTranslateX(xPos * cellSize);
    cylinder.setTranslateZ(zPos * cellSize);
    return cylinder;
  }

  /**
   * This method is called every frame by the animation timer to move the zombie
   * forward in the current direction it's traveling which is determined by the
   * current angle value. It is not called when the zombie is stopped against a
   * wall or other obstacle.
   */
  private void moveTwoDZombie(double angle, double zombieWalkingSpeed, Circle zombieCirc)
  {
    double cosTransform = Math.cos(angle * (Math.PI / 180));
    double sinTransform = Math.sin(angle * (Math.PI / 180));
    double movementAmountY = zombieCirc.getCenterY() + (zombieWalkingSpeed * (cosTransform));
    double movementAmountX = zombieCirc.getCenterX() + (zombieWalkingSpeed * (sinTransform));
    if (movementAmountX > 0 && movementAmountY > 0 && movementAmountX < ZombieBoardRenderer.boardWidth * ZombieBoardRenderer.cellSize && movementAmountY < ZombieBoardRenderer.boardWidth * ZombieBoardRenderer.cellSize)
    {
      zombieCirc.setCenterY(movementAmountY);
      zombieCirc.setCenterX(movementAmountX);
    }
  }

  /**
   * Moves the zombie forward in a direction determined by the current angle in
   * a 3D environment.
   */
  private void moveThreeDZombie(double angle, double zombieWalkingSpeed, Cylinder zombieCylinder, boolean engaged)
  {
    lastX = zombieCylinder.getTranslateX();
    lastZ = zombieCylinder.getTranslateZ();

    double cosTransform = Math.cos(angle * (Math.PI / 180));
    double sinTransform = Math.sin(angle * (Math.PI / 180));
    double movementAmountZ = zombieCylinder.getTranslateZ() + (zombieWalkingSpeed * (cosTransform));
    double movementAmountX = zombieCylinder.getTranslateX() + (zombieWalkingSpeed * (sinTransform));
    if (movementAmountX > 0 && movementAmountZ > 0 && movementAmountX < entityManager.zombieHouse.boardWidth && movementAmountZ < entityManager.zombieHouse.boardHeight)
    {
      zombieCylinder.setTranslateZ(movementAmountZ);
      zombieCylinder.setTranslateX(movementAmountX);
      double angleToPlayer = getAngleToPlayer();
      for (int i = 0; i < zombieMesh.length; i++)
      {
        zombieMesh[i].setTranslateZ(movementAmountZ);
        zombieMesh[i].setTranslateX(movementAmountX);
        if(!engaged) zombieMesh[i].setRotate(angleToPlayer);
        else zombieMesh[i].setRotate(angle + 180);
      }
    }
    xPos = zombieCylinder.getTranslateX();
    zPos = zombieCylinder.getTranslateZ();
  }

  /**
   * Gets the angle that the zombie is moving in towards the player. This is
   * used to rotate the zombie to face the player.
   *
   * @return The angle that the zombie is going in towards the player.
   */
  private double getAngleToPlayer()
  {

    double xDiff = entityManager.player.boundingCircle.getTranslateX() - ZOMBIE_HITBOX.getTranslateX();
    double zDiff = entityManager.player.boundingCircle.getTranslateZ() - ZOMBIE_HITBOX.getTranslateZ();

    if (zDiff < 0)
    {
      return (Math.atan(xDiff / zDiff) - Math.PI) * (180 / Math.PI) - 180;
    }

    return (Math.atan(xDiff / zDiff)) * (180 / Math.PI) - 180;
  }

  /**
   * Selects a random angle as the direction for the zombie to start moving.
   */
  public void startZombie()
  {
    angle = rand.nextInt(360);
  }

  /**
   * Stops the zombie on the 3D game map when it has hit an obstacle.
   */
  private void stopThreeDZombie()
  {
    ZOMBIE_HITBOX.setTranslateZ(ZOMBIE_HITBOX.getTranslateZ());
    ZOMBIE_HITBOX.setTranslateX(ZOMBIE_HITBOX.getTranslateX());
  }

  /**
   * When the zombie hits an obstacle, this is called to reverse the direction
   * of the angle. This is done since in the animation timer there is a piece of
   * code that moves the zombie out of the obstacle a very small amount in the
   * reverse direction, and then a random angle is selected for the zombie to
   * travel in.
   */
  private void adjustAngle()
  {
    prevAngle = angle;
    angle = prevAngle - 180;
    angleAdjusted.set(true);
  }

  /**
   * Pick a random direction for the zombie to travel in, then set the boolean
   * flags off so that the timer will call the code that moves the zombie
   * forward.
   */
  void makeDecision()
  {
    pickRandomAngle();
    angleAdjusted.set(false);
    collisionDetected.set(false);
    collisionJustDetected.set(false);
  }

  /**
   * Pick a new random angle for the zombie after it has collided with an
   * obstacle. If the random angle chosen equals the previous angle, do not
   * choose that one again, but pick a new one. If the zombie detects the player,
   * select the angle towards the player to travel in.
   */
  private void pickRandomAngle()
  {
    if (!goingAfterPlayer.get())
    {
      int newAngle = rand.nextInt(360);
      if (newAngle != prevAngle)
      {
        angle = newAngle;
      }
      else
      {
        while (newAngle == prevAngle)
        {
          newAngle = rand.nextInt(360);
        }
        angle = newAngle;
      }
    }
    else
    {
      findAngle();
    }
  }

  /**
   * When the zombie has detected the player and is moving toward the player,
   * this method is called to move the zombie in the appropriate direction.
   */
  private void moveTowardPlayer(double zombieWalkingSpeed)
  {
    findAngle();
    moveThreeDZombie(angle, zombieWalkingSpeed, ZOMBIE_HITBOX, false);
  }

  /**
   *  helper method to find the correct angle to move
   */
  private void findAngle()
  {
    if (zombieHeading != null)
    {
      angle = zombieHeading.direction;
    }
    if (lastAngle != angle)
    {
      findNewPath.set(false);
    }
    lastAngle = angle;
  }

  /**
   * This method does the same thing as the moveTowardPlayer() method, but it
   * does it specifically for the zombie on the 2d board.
   */
  private void moveTowardPlayerTwoD(double zombieWalkingSpeed)
  {
    if (zombieHeading != null)
    {
      angle = zombieHeading.direction;
      if (lastAngle != angle)
      {
        findNewPath.set(false);
      }
      lastAngle = angle;
      moveTwoDZombie(angle, zombieWalkingSpeed, zombieCirc);
    }
  }

  /**
   * Get the zombie mesh associated with this zombie object.
   *
   * @return The zombie mesh.
   */
  public Node[] getMesh()
  {
    return this.zombieMesh;
  }

  /**
   * Sets the zombie mesh to a specified mesh
   *
   * @param zombieMesh The Node array that contains all parts of the mesh
   */
  public void setMesh(Node[] zombieMesh)
  {
    this.zombieMesh = zombieMesh;
    for (int i = 0; i < zombieMesh.length; i++)
    {
      zombieMesh[i].setRotationAxis(Rotate.Y_AXIS);
    }
  }

  /**
   * This method is called every time the animation time is called. A collision
   * is checked for. If the zombie has collided with an obstacle, while the
   * zombie is collided with that obstacle, move the zombie in the opposite
   * direction out of that obstacle. If there is no collision, simply keep
   * moving the zombie in the appropriate direction. Also, get the current
   * position of the zombie for purposes of pathfinding. Check to see where the
   * zombie is in relation to the center of the tile, and adjust accordingly to
   * keep the zombie centered as it moves toward the player. This is to ensure
   * that the zombie moves in the right directions at the right times. Without
   * doing these checks, the zombie might move in a direction prematurely and
   * needlessly hit obstacles. After these checks are done, the findPathToPlayer
   * method is called to find the shortest path to the player.
   *
   * @todo it seems zombie will find path to player regardless of its position in the map, change it to only find path
   * if player is "engaged" (i.e. if it's in the near vicinity)
   */
  @Override
  public void tick()
  {
    //if the zombie does not have a path then it has to make decisions
    if(entityManager.player.getNumDeaths() == 0 || locationOnPath >= pathTaken.size() || !engaged)
    {
      takeHealth = 0;
      if (entityManager.getWallCollision(ZOMBIE_HITBOX) != null && !angleAdjusted.get())
      {
        if (!collisionJustDetected.get())
        {
          collisionDetected.set(true);
          collisionJustDetected.set(true);
          stopThreeDZombie();
          adjustAngle();
          // Move the zombie out of the bounds of the obstacle.
          if (goingAfterPlayer.get())
          {
            while (entityManager.getWallCollision(ZOMBIE_HITBOX) != null)
            {
              moveThreeDZombie(angle, zombieWalkingSpeed, ZOMBIE_HITBOX, false);
            }
            double currentX = ZOMBIE_HITBOX.getTranslateX();
            double currentZ = ZOMBIE_HITBOX.getTranslateZ();
            checkForCornerTile(entityManager.zombieHouse.getGameBoard()[(int) Math.floor(currentZ)][(int) Math.floor(currentX)]);
          } else
          {
            while (entityManager.getWallCollision(ZOMBIE_HITBOX) != null)
            {
              moveThreeDZombie(angle, zombieWalkingSpeed, ZOMBIE_HITBOX, false);
            }
          }
        }
      } else if (!collisionDetected.get())
      {
        if (!goingAfterPlayer.get() && !isMasterZombie)
        {
          moveThreeDZombie(angle, zombieWalkingSpeed, ZOMBIE_HITBOX, false);
        } else if (!isMasterZombie && goingAfterPlayer.get())
        {
          moveTowardPlayer(zombieWalkingSpeed);
        } else if (isMasterZombie && !goingAfterPlayer.get())
        {
          moveThreeDZombie(angle, masterZombieSpeed, ZOMBIE_HITBOX, false);
        } else if (isMasterZombie && goingAfterPlayer.get())
        {
          moveTowardPlayer(masterZombieSpeed);
        }
      }
      double currentX = ZOMBIE_HITBOX.getTranslateX();
      double currentZ = ZOMBIE_HITBOX.getTranslateZ();
      if (angle == 180)
      {
        if (currentZ > (Math.floor(currentZ) + .5))
        {
          currentZ++;
        }
      }
      if (angle == 90)
      {
        if (currentX < (Math.floor(currentX) + .5))
        {
          currentX--;
        }
      }
      if (angle == 0)
      {
        if (currentZ < (Math.floor(currentZ) + .5))
        {
          currentZ--;
        }
      }
      if (angle == 270)
      {
        if (currentX > (Math.floor(currentX) + .5))
        {
          currentX++;
        }
      }
      if (angle > 90 && angle < 180)
      {
        if (currentX < (Math.floor(currentX) + .5))
        {
          currentX--;
        }
        if (currentZ > (Math.floor(currentZ) + .5))
        {
          currentZ++;
        }
      }
      if (angle > 0 && angle < 90)
      {
        if (currentX < (Math.floor(currentX) + .5))
        {
          currentX--;
        }
        if (currentZ < (Math.floor(currentZ) + .5))
        {
          currentZ--;
        }
      }
      if (angle < 360 && angle > 270)
      {
        if (currentX > (Math.floor(currentX) + .5))
        {
          currentX++;
        }
        if (currentZ < (Math.floor(currentZ) + .5))
        {
          currentZ--;
        }
      }
      if (angle > 180 && angle < 270)
      {
        if (currentX > (Math.floor(currentX) + .5))
        {
          currentX++;
        }
        if (currentZ > (Math.floor(currentZ) + .5))
        {
          currentZ++;
        }
      }
      if (currentX >= entityManager.zombieHouse.getGameBoard().length)
      {
        currentX--;
      }
      if (currentZ >= entityManager.zombieHouse.getGameBoard().length)
      {
        currentZ--;
      }
      Tile currentTile = entityManager.zombieHouse.getGameBoard()[(int) currentZ][(int) currentX];
      findPathToPlayer(currentTile);/** @todo look here*/
      updateDistance();
      //adds EVERY step taken to path. There'll be many repeats because it records how long player stays there
      pathTaken.add(new CreaturePathInfo((float) xPos, (float) zPos, (float) angle, didAttack, isDeadInPath));
    }
    //the zombie has a path and it needs to follow it
    else
    {
      if(!isMasterZombie)
      {
        moveThreeDZombie(pathTaken.get(locationOnPath).getAngle(), zombieWalkingSpeed, ZOMBIE_HITBOX, true);
      }
      else
        moveThreeDZombie(pathTaken.get(locationOnPath).getAngle(), masterZombieSpeed, ZOMBIE_HITBOX, true);
    }
    locationOnPath++;
  }

  /**
   * This method does the same things that tick() does, but it is called for
   * zombies that are being rendered on a 2D board.
   */
  public void tick2d()
  {
    if (entityManager.checkTwoD(zombieCirc) && !angleAdjusted.get())
    {
      if (!collisionJustDetected.get())
      {
        collisionDetected.set(true);
        collisionJustDetected.set(true);
        stopThreeDZombie();
        adjustAngle();
        // Move the zombie out of the bounds of the obstacle.
        if (goingAfterPlayer.get())
        {
          while (entityManager.checkTwoD(zombieCirc))
          {
            moveTwoDZombie(angle, twoDSpeed, zombieCirc);
          }
          double currentXVal = zombieCirc.getCenterX() / ZombieBoardRenderer.cellSize;
          double currentZVal = zombieCirc.getCenterY() / ZombieBoardRenderer.cellSize;
          checkForCornerTile(ZombieBoardRenderer.gameBoard[(int) currentZVal][(int) currentXVal]);
        }
        else
        {
          while (entityManager.checkTwoD(zombieCirc))
          {
            moveTwoDZombie(angle, twoDSpeed, zombieCirc);
          }
        }
      }
    }
    else if (!collisionDetected.get())
    {
      if (!goingAfterPlayer.get() && !isMasterZombie)
      {
        moveTwoDZombie(angle, twoDSpeed, zombieCirc);
      }
      else if (!isMasterZombie && goingAfterPlayer.get())
      {
        moveTowardPlayerTwoD(twoDSpeed);
      }
      else if (isMasterZombie && !goingAfterPlayer.get())
      {
        moveTwoDZombie(angle, masterZombie2dSpeed, zombieCirc);
      }
      else if (isMasterZombie && goingAfterPlayer.get())
      {
        moveTowardPlayerTwoD(masterZombie2dSpeed);
      }
    }
    double currentX = zombieCirc.getCenterX() / ZombieBoardRenderer.cellSize;
    double currentY = zombieCirc.getCenterY() / ZombieBoardRenderer.cellSize;
    if (!collisionDetected.get() && Math.abs(angle) == 180)
    {
      if (currentY > (Math.floor(currentY) + .5))
      {
        currentY++;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) == 90)
    {
      if (currentX < (Math.floor(currentX) + .5))
      {
        currentX--;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) == 0)
    {
      if (currentY < (Math.floor(currentY) + .5))
      {
        currentY--;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) == 270)
    {
      if (currentX > (Math.floor(currentX) + .5))
      {
        currentX++;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) > 90 && Math.abs(angle) < 180)
    {
      if (currentX < (Math.floor(currentX) + .5))
      {
        currentX--;
      }
      if (currentY > (Math.floor(currentY) + .5))
      {
        currentY++;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) > 0 && Math.abs(angle) < 90)
    {
      if (currentX < (Math.floor(currentX) + .5))
      {
        currentX--;
      }
      if (currentY < (Math.floor(currentY) + .5))
      {
        currentY--;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) < 360 && Math.abs(angle) > 270)
    {
      if (currentX > (Math.floor(currentX) + .5))
      {
        currentX++;
      }
      if (currentY < (Math.floor(currentY) + .5))
      {
        currentY--;
      }
    }
    if (!collisionDetected.get() && Math.abs(angle) > 180 && Math.abs(angle) < 270)
    {
      if (currentX > (Math.floor(currentX) + .5))
      {
        currentX++;
      }
      if (currentY > (Math.floor(currentY) + .5))
      {
        currentY++;
      }
    }
    if (currentX >= ZombieBoardRenderer.gameBoard.length)
    {
      currentX--;
    }
    if (currentY >= ZombieBoardRenderer.gameBoard.length)
    {
      currentY--;
    }
    Tile currentTile = ZombieBoardRenderer.gameBoard[(int) currentY][(int) currentX];
    findPathToPlayer(currentTile);
  }

  /**
   * This method checks to see that the current tile where the zombie is located
   * is in the tile graph. If so, the player position is gotten, and the
   * appropriate methods are called to find the shortest path to the player.
   * Only the zombies that are within a Manhattan distance of 20 to the player
   * call the pathfinding method.
   *
   * @param currentTile The current tile where the zombie is.
   */
  private void findPathToPlayer(Tile currentTile)
  {
    if (TileGraph.tileGraph.containsKey(currentTile))
    {
      GraphNode zombieNode = TileGraph.tileGraph.get(currentTile);
      Tile zombieTile = zombieNode.nodeTile;
      GraphNode playerNode = null;
      if (!twoDBoard)
      {
        playerNode = entityManager.player.getCurrentNode();
      }
      else if (twoDBoard)
      {
        playerNode = entityManager.player.getCurrent2dNode();
        calcPath.twoD = true;
      }
      Tile playerTile = playerNode.nodeTile;
      if (calcPath.findDistance(zombieTile, playerTile) <= 20 || (isMasterZombie && masterZombieChasePlayer.get()))
      {
        if (!zombieTile.isWall)
        {
          calcPath.findPath(zombieTile, playerTile, zombieNode);
        }
        if (zombieTile.isWall)
        {
          calcPath.distanceToPlayer = 30;
        }
      }
      else if (calcPath.findDistance(zombieTile, playerTile) > 20)
      {
        goingAfterPlayer.set(false);
        calcPath.distanceToPlayer = 30;
        if (twoDBoard && calcPath.oldPath.size() >= 1)
        {
          calcPath.removePath();
        }
      }
      if (calcPath.distanceToPlayer <= zombieSmell || (isMasterZombie && masterZombieChasePlayer.get()))
      {
        goingAfterPlayer.set(true);
      }
      else
      {
        goingAfterPlayer.set(false);
      }
    }
  }

  /**
   * Checks if the zombie is standing on a corner tile. If so, the zombie is
   * centered on that tile. This is done to deal with an occasional issue where
   * zombies will continue to walk into a corner tile and get stuck there if
   * they are walking into it at a 90 degree angle.
   *
   * @param currentTile The current tile that we are checking.
   */
  private void checkForCornerTile(Tile currentTile)
  {
    if (twoDBoard)
    {
      if (currentTile.wallNE || currentTile.wallNW || currentTile.wallSW || currentTile.wallSE)
      {
        zombieCirc.setCenterX(currentTile.xPos * ZombieBoardRenderer.cellSize);
        zombieCirc.setCenterY(currentTile.zPos * ZombieBoardRenderer.cellSize);
      }
    }
    else
    {
      if (currentTile.wallNE || currentTile.wallNW || currentTile.wallSW || currentTile.wallSE)
      {
        ZOMBIE_HITBOX.setTranslateZ(currentTile.zPos);
        ZOMBIE_HITBOX.setTranslateX(currentTile.xPos);
      }
    }
  }

  /**
   * If zombie is in range of player, play appropriate sounds.
   */
  @Override
  public void stepSound()
  {
    double distance = entityManager.calculateDistanceFromPlayer(this);
    if (distance < Attributes.Player_Hearing)
    {
      double balance = entityManager.calculateSoundBalance(this);

      entityManager.soundManager.playSoundClip(Sound.shuffle, distance, balance);
      if (Math.random() < .03)
      {
        entityManager.soundManager.playSoundClip(Sound.groan, distance, balance);
      }
    }
  }

  /**
   * Calculates Distance for zombies.
   *
   * @return The distance between lastX/Z and ZOMBIE_HITBOX.getTranslateX/Z
   */
  @Override
  public double calculateDistance()
  {
    double xDist = ZOMBIE_HITBOX.getTranslateX() - lastX;
    double zDist = ZOMBIE_HITBOX.getTranslateZ() - lastZ;
    return Math.sqrt((xDist * xDist) + (zDist * zDist));
  }

  void reset()
  {
    if (engagedCurrentGame == 0) pathTaken.clear(); //player never interacted with zombie
    health = fullHealth;
    goingAfterPlayer.set(false);//idk yet
    xPos = START_X;
    zPos = START_Z;
    row = START_ROW;
    col = START_COL;
    tile.xPos = xPos;
    tile.zPos = zPos;
    ZOMBIE_HITBOX.setTranslateX(xPos);
    ZOMBIE_HITBOX.setTranslateZ(zPos);
    locationOnPath = 0;
    takeHealth = 1;
    for (int i = 0; i < zombieMesh.length; i++)
    {
      zombieMesh[i].setTranslateX(xPos);
      zombieMesh[i].setTranslateZ(zPos);
      //zombieMesh[i].setRotate(angleToPlayer);
    }
    findPathToPlayer(tile);
  }

  /**
   * Gets rid of values from the last game before we start a
   * new one.
   */
  void dispose()
  {
    zombie = null;
    zombieMesh = null;
  }
}
