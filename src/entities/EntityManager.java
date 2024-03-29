package entities;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import game_engine.Attributes;
import game_engine.Scenes;
import game_engine.ZombieHouse3d;
import gui.Main;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape3D;
import levels.Tile;
import sounds.Sound;
import sounds.SoundManager;
import utilities.ZombieBoardRenderer;

/**
 * 09/18/16 added arraylist of props
 *
 * @author Jeffrey McCall
 *         Ben Matthews
 *         Atle Olson
 *         This class handles many different functions for all of the entities in the
 *         game, which are the player and the zombies. Values are updated for the entities
 *         every time the animation timer is called. Various other functions are performed
 *         here such as calculating the sound balance as well as collision detection.
 */
public class EntityManager
{
  //creatures
  public Player player;
  private Zombie masterZombie;
  private PlayerGhost ghost;

  public ArrayList<Zombie> zombies;
  private ArrayList<Zombie> deadZombies;
  private ArrayList<Zombie> bifurcatedZombies;
  private PlayerGhost[] ghosts = new PlayerGhost[player.MAX_LIVES];

  public Chainsaw chainsaw;
  public Prop1 prop1;
  public Prop2 prop2;
  public Prop3 prop3;
  public Prop3 prop4;
  public Prop3 prop5;
  public Prop3 prop6;
  public ArrayList<Prop> props;
  public SoundManager soundManager;
  public ZombieHouse3d zombieHouse;
  public Scenes scenes;
  private Group root;
  public Main main;

  public boolean masterZombieSpawn = false;
  public AtomicBoolean gameIsRunning = new AtomicBoolean(true);
  private Tile[][] gameBoard;

  private MasterZombieDecision masterDecision;
  private ZombieDecision zombieDecision;
  private int zombiePathIndex = 0;
  private int collisionTicks = 59;
  private long timeToBifurcate = 0;

  /**
   * Constructor for EntityManager.
   *
   * @param scenes The various screens that are seen throughout playing the game, such as
   *               the main menu, the settings menu, the win screen, etc.
   */
  public EntityManager(Scenes scenes, Group root)
  {
    this.scenes = scenes;
    this.root = root;
    main = scenes.getMain();
    soundManager = scenes.getSoundManager();
    zombies = new ArrayList<>();
    deadZombies = new ArrayList<>();
    bifurcatedZombies = new ArrayList<>();
    props = new ArrayList<>();
    zombieDecision = new ZombieDecision();
    zombieDecision.setDaemon(true);
    zombieDecision.start();
  }

  // The number of wall tiles on the map. Used to check for collisions.
  public int numTiles = 0;

  /**
   * Checks if the zombie is colliding with anything.
   *
   * @return False if no collision detected. True if there is a collision.
   */
  public boolean checkTwoD(Circle zombieCirc)
  {
    for (int i = 0; i < numTiles; i++)
    {
      if (zombieCirc.getLayoutBounds().intersects(ZombieBoardRenderer.walls.get(i).getLayoutBounds()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Collision detection for 3D zombie objects.
   *
   * @return True if there is a collision. False if there isn't.
   */
  public Box getWallCollision(Shape3D shape)
  {
    for (int i = 0; i < numTiles; i++)
    {
      if (shape.getBoundsInParent().intersects(zombieHouse.walls.get(i).getBoundsInParent()))
      {
        return zombieHouse.walls.get(i);
      }
    }
    return null;
  }

  /**
   * Collision detection for 3D player objects.
   *
   * @param player The shape that represents the player.
   * @return True if there is a collision. False if there isn't.
   */
  boolean checkPlayerCollision(Shape3D player, Shape3D chainsaw)
  {
    for (Zombie zombie : zombies)
    {
      // if at the last index of zombie's path he is dead, remove. (Used for ghost killing zombie)
      if (zombiePathIndex == zombie.pathTaken.size() - 1 && zombie.pathTaken.get(zombiePathIndex).getIsDead() == 1)
      {
        zombie.setDead(true);
        deadZombies.add(zombie);
        zombies.remove(zombie);
        root.getChildren().removeAll(zombie.getMesh());
        return false;

      }
      //if chainsaw and zombie are colliding
      else if (chainsaw.getBoundsInParent().intersects(zombie.ZOMBIE_HITBOX.getBoundsInParent()))
      {
        //if player is attacking and facing zombie
        if (this.player.attacking && (this.player.angle - zombie.angle > -300 && this.player.angle - zombie.angle < 300))
        {
          if (!zombie.hasPath())
          {
            zombie.takeHealth();
          }
          else
          {
            collisionTicks++;
            if (collisionTicks == 60)
            {
              bifurcate(zombie);
              zombie.setTimeToBifurcate(timeToBifurcate);
              collisionTicks = 0;
            }
          }
        }
        if (zombie.getHealth() <= 0)
        {
          byte dead = 1;
          //need to blow up the zombie
          zombie.pathTaken.get(zombie.pathTaken.size() - 1).setIsDead(dead);
          zombie.setDead(true);
          deadZombies.add(zombie);
          zombies.remove(zombie);
          root.getChildren().removeAll(zombie.getMesh());
        }
        if (zombie.hasPath()) return false;
      }
      //if player and zombie are colliding
      if (player.getBoundsInParent().intersects(zombie.ZOMBIE_HITBOX.getBoundsInParent())) return true;

    }
    return false;
  }

  private void bifurcate(Zombie zombie)
  {
    Zombie newZombie = new Zombie(gameBoard[zombie.getRow()][zombie.getCol()], this);
    zombies.add(newZombie);
    newZombie.setMesh(ZombieHouse3d.loadMeshViews("Resources/Meshes/Feral_Ghoul/Feral_Ghoul.obj"));
    root.getChildren().addAll(newZombie.getMesh());
    bifurcatedZombies.add(newZombie);
  }

  /**
   * calculate the distance between two entities
   *
   * @param zombie The zombie object that we are checking.
   * @return The distance between the zombie and the player.
   */
  public double calculateDistanceFromPlayer(Zombie zombie)
  {
    double xDist = player.xPos - zombie.xPos;
    double zDist = player.zPos - zombie.zPos;

    return Math.sqrt(xDist * xDist + zDist * zDist);
  }

  /**
   * calculate the sound balance based on the player angle and
   * the zombie position
   *
   * @param zombie
   * @return a number from -1 to 1 that represents the sound
   * balance
   */
  public double calculateSoundBalance(Zombie zombie)
  {
    double angle = player.boundingCircle.getRotate() * (180 / Math.PI);

    double xDiff = player.xPos - zombie.xPos;
    double zDiff = player.zPos - zombie.zPos;
    double theta = Math.atan(xDiff / zDiff);

    angle -= theta;
    if (angle < -Math.PI) angle += 2 * Math.PI;

    return angle / Math.PI;
  }

  /**
   * Creates list of props that will be
   * rendered into 3D ZombieHouse
   */
  public void createProps()
  {

        /*  possible code if placing props using tiles
        Prop newProp = new Prop(gameBoard[col][row], row, col,
                gameBoard[col][row].xPos, gameBoard[col][row].zPos, this);
        newProp.create3DProp(row, col, Tile.tileSize);
        props.add(newProp);

        */
//    props.add(new Prop("Sofa", 5, 0, 5, this));
//    props.add(new Prop("Desk", 5, 0, 5, this));

  }


  /**
   * Creates list of all of the zombies that will spawn
   * on the board.
   */
  public void createZombies(Tile[][] gameBoard, int zHeight, int xWidth)
  {
    this.gameBoard = gameBoard;
    int counter = 0;
    label:
    for (int col = 0; col < zHeight; col++)
    {
      for (int row = 0; row < xWidth; row++)
      {
        if (gameBoard[col][row].hasZombie && !gameBoard[col][row].isHallway)
        {
          counter++;
          Zombie newZombie = new Zombie(gameBoard[col][row], this);
          zombies.add(newZombie);
          if (counter == Attributes.Max_Zombies) break label;
        }
      }
    }
    //Designate one zombie as the master zombie.
    int masterSpawnChance = masterZombieSpawnChance();
    int zombieListCounter = 0;
    for (Zombie zombie : zombies)
    {
      if (zombieListCounter == masterSpawnChance)
      {
        zombie.isMasterZombie = true;
        masterZombie = zombie;
        masterDecision = new MasterZombieDecision();
        masterDecision.setDaemon(true);
        masterDecision.start();
      }
      zombieListCounter++;
    }

    for (Zombie zombie : zombies)
    {
      zombie.startZombie();
    }
  }

  /**
   * When a zombie detects the player, the master zombie also detects the player
   * and goes after the player.
   */
  public void startMasterZombie()
  {
    for (Zombie zombie : zombies)
    {
      if (zombie.isMasterZombie)
      {
        zombie.masterZombieChasePlayer.set(true);
        zombie.setMasterHealth();
      }
    }
  }

  /**
   * This method returns the number that represents which zombie in the zombie
   * list should be the master zombie.
   *
   * @return The index of the zombie in the zombie list that should be the
   * master zombie.
   */
  private int masterZombieSpawnChance()
  {
    Random masterSpawnChance = new Random();
    int numZombies = zombies.size();
    int spawnChance = masterSpawnChance.nextInt(numZombies);
    return spawnChance;
  }

  /**
   * This Method updates all the values of all entities
   */
  public void tick()
  {
    zombiePathIndex++;
    player.tick();
    for (int i = 0; i < player.getNumDeaths(); i++)
    {
      if (ghosts[i] != null) ghosts[i].tick();
    }

    for (Zombie zombie : zombies)
    {
      zombie.tick();
      if (zombie.goingAfterPlayer.get() && !zombie.isMasterZombie)
      {
        startMasterZombie();
      }
    }

    if (player.isDead.get())
    {
      zombieHouse.stopGameLoop();
      gameIsRunning.set(false);
      soundManager.stopTrack();
      soundManager.playSoundClip(Sound.death);
      player.subtractLife();
      HBox hBox = new HBox();
      if (player.getLives() > 0)
      {
        hBox.getChildren().addAll(scenes.tryAgainButton, scenes.returnButtonDeath);
        scenes.gameOverRoot.setTop(hBox);
        main.assignStage(scenes.gameOver);
      }
      else
      {
        hBox.getChildren().addAll(scenes.returnButtonDeath);
        scenes.gameOverRoot.setTop(hBox);
        main.assignStage(scenes.gameOver);
      }
    }

    if (player != null && player.foundExit.get())
    {
      soundManager.stopTrack();
      soundManager.playSoundClip(Sound.achieve);
      dispose();
      HBox hBox = new HBox();
      scenes.updateWinScreen();
      hBox.getChildren().addAll(scenes.returnButton, scenes.goTo3dGameNextLevel);
      scenes.winRoot.setTop(hBox);
      main.assignStage(scenes.win);
    }
  }

  /**
   * @author Jeffrey McCall This is a class that extends Thread and is used to
   *         keep track of the decision rate of the zombies, which is 2 seconds.
   */
  private class ZombieDecision extends Thread
  {
    /**
     * Every two seconds, if the zombie is a random walk zombie, a new angle for
     * the zombie to walk in is chosen. If the zombie has hit an obstacle, then
     * the angleAdjusted boolean flag will be on, to indicate that the angle was
     * adjusted when the zombie hit an obstacle. In this case, the
     * "makeDecision()" method is called to determine the new angle for the
     * zombie to travel in, and start it moving again. If the zombie is chasing after
     * the player, then the "findNewPath" boolean is set to on to indicate that a new
     * direction towards the player needs to be set.
     */
    @Override
    public void run()
    {
      while (gameIsRunning.get())
      {
        try
        {
          sleep(2000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        for (Zombie zombie : zombies)
        {
          if (!zombie.isMasterZombie)
          {
            if (zombie.goingAfterPlayer.get())
            {
              zombie.findNewPath.set(true);
            }
            if (zombie.randomWalk && !zombie.goingAfterPlayer.get())
            {
              zombie.angle = zombie.rand.nextInt(360);
            }
            if (zombie.angleAdjusted.get())
            {
              zombie.makeDecision();
            }
          }
        }
        for (Zombie zombie : bifurcatedZombies)
        {
          zombie.goingAfterPlayer.set(true);
        }
      }
    }
  }

  /**
   * @author Jeffrey McCall
   *         Thread for the decision rate of the master zombie. It has a
   *         faster decision rate than the regular zombies. The same operations
   *         are performed on the master zombie that are performed on the
   *         other zombies.
   */
  private class MasterZombieDecision extends Thread
  {
    /**
     * While the game is running, perform the same operations on
     * the master zombie that would be performed on the regular zombies.
     */
    @Override
    public void run()
    {
      while (gameIsRunning.get())
      {
        try
        {
          sleep(500);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        if (masterZombie.masterZombieChasePlayer.get())
        {
          masterZombie.findNewPath.set(true);
        }
        if (masterZombie.randomWalk && !masterZombie.masterZombieChasePlayer.get())
        {
          masterZombie.angle = masterZombie.rand.nextInt(360);
        }
        if (masterZombie.angleAdjusted.get())
        {
          masterZombie.makeDecision();
        }
      }
    }
  }

  /**
   * @param zombieHouse ZombieHouse3d Object
   *                    <p>
   *                    This Method sets the the current instance of zombieHouse3d with the parameter
   *                    zombieHouse
   */
  public void setZombieHouse3d(ZombieHouse3d zombieHouse)
  {
    this.zombieHouse = zombieHouse;
  }

  /**
   * this is called every Try Again (once for each life), and reset everything back to starting position and
   * creates a new ghost
   */
  public void reset()
  {
    zombiePathIndex = 0;
    player.reset();
    for (PlayerGhost ghost : ghosts)
    {
      if (ghost != null) ghost.reset();
    }
    for (Zombie zombie : deadZombies)
    {
      if (!bifurcatedZombies.contains(zombie))
      {
        zombies.add(zombie);
        root.getChildren().addAll(zombie.getMesh());
      }
    }
    deadZombies.clear();

    for (Zombie zombie : zombies)
    {
      zombie.reset();
      if (bifurcatedZombies.contains(zombie))
      {
        zombies.remove(zombie);
      }
    }

    ghost = new PlayerGhost(player.getCurrentPath()[player.getNumDeaths() - 1], root);
    ghosts[player.getNumDeaths() - 1] = ghost;

    gameIsRunning.set(false);
  }

  /**
   * Clears game data
   */
  private void dispose()
  {
    for (Zombie zombie : zombies)
    {
      zombie.dispose();
    }
    zombies.clear();
    gameIsRunning.set(false);
    player.dispose();
    player = null;
    zombieHouse.dispose();
  }
}
