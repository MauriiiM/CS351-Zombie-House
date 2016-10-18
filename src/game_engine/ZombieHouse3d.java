package game_engine;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import entities.*;
import graphing.GraphNode;
import graphing.TileGraph;
import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import levels.TextureMaps;
import levels.Tile;
import levels.Tile.TileType;

/**
 * @author Atle Olson
 * @author Jeffrey McCall
 *         This class will create a 3d representation of our game
 */
public class ZombieHouse3d
{
  private PerspectiveCamera camera;
  private Tile playerTile;
  private PointLight light = new PointLight();
  private AnimationTimer gameLoop;

  private boolean paused = false;

  public int boardWidth;
  public int boardHeight;
  private Tile[][] gameBoard;
  private Box[][] floorDrawingBoard;
  private Box[][] roofDrawingBoard;

  public ArrayList<Box> exits = new ArrayList<>();

  private Group root;

  // The list of walls used for collision detection.
  public ArrayList<Box> walls = new ArrayList<>();

  // list of props used to decorate zombie house
  private ArrayList<Prop> props = new ArrayList<>();

  boolean initZombieMovement = true;
  public int tileSize = Tile.getTileSize();

  int difficulty;
  public Scene scene;

  private EntityManager entityManager;
  private Scenes scenes;

  private String Feral_Ghoul = "Resources/Meshes/Feral_Ghoul/Feral_Ghoul.obj";
  private String Lambent_Female = "Resources/Meshes/Lambent_Female/Lambent_Female.obj";
  private String Desk = "Resources/Meshes/Desk/Desk.obj";
  private String Sofa = "Resources/Meshes/Sofa/Sofa.obj";

  private final URL chainsawURL = getClass().getResource("chainsawAttack.mp3");
  private final Media chainMedia = new Media(chainsawURL.toString());
  private final MediaPlayer chainsawPlayer = new MediaPlayer(chainMedia);

  /**
   * Constructor for ZombieHouse3d object
   *
   * @param difficulty The difficulty setting
   * @param scenes     Scenes object
   */
  ZombieHouse3d(int difficulty, Scenes scenes)
  {
    this.difficulty = difficulty;
    this.scenes = scenes;
  }

  public void stopGameLoop()
  {
    gameLoop.stop();
  }

  /**
   * Delete game data after game has ended. Used when going from
   * one level to another, or restarting a level.
   *
   * @todo instead of disposing when same map, reset everything
   */
  public void dispose()
  {
    gameLoop.stop();
    entityManager = null;
    scene = null;
    camera = null;
    light.setColor(Color.GRAY);
    gameBoard = null;
    walls.clear();
    exits.clear();
    root.getChildren().clear();
    entityManager = null;
  }

  /**
   * @param gameStage The stage into which all of the attributes of the game
   *                  are being placed and rendered.
   * @return scene
   * Returns the scene that is our game
   */
  Scene zombieHouse3d(Stage gameStage, Tile[][] gameBoard) throws Exception
  {
    //Stage gameStage = new Stage();
    // gameBoard = MapLoader.loadLevel("/Maps/testmap.txt");
    this.gameBoard = gameBoard;
    boardWidth = gameBoard[0].length;
    boardHeight = gameBoard.length;
    floorDrawingBoard = new Box[boardWidth][boardHeight];
    roofDrawingBoard = new Box[boardWidth][boardHeight];

    scene = new Scene(createContent());

    scene.addEventHandler(KeyEvent.KEY_PRESSED, new KeyboardEventHandler(camera, entityManager.player, this));
    scene.addEventHandler(KeyEvent.KEY_RELEASED, new KeyboardEventHandler(camera, entityManager.player, this));

    // Initialize stage
    gameStage.setTitle("Zombie House 3D");
    gameStage.setResizable(false);
    gameStage.setScene(scene);
    gameStage.setOnCloseRequest(event ->
    {
      entityManager.player.gameIsRunning.set(false);
      entityManager.gameIsRunning.set(false);
    });

    gameLoop = new MainGameLoop();
    gameLoop.start();
    return scene;
  }

  public Tile[][] getGameBoard()
  {
    return gameBoard;
  }

  /**
   * called when "Try Again" is clicked and returns same scene and starts loop
   * @return
   */
  public Scene resetScene()
  {
    entityManager.reset();
    gameLoop.start();
    return scene;
  }

  boolean getPaused()
  {
    return paused;
  }

  public void playChainsaw()
  {
    chainsawPlayer.setCycleCount(AudioClip.INDEFINITE);
    chainsawPlayer.play();
  }

  public void pauseChainsaw()
  {
    chainsawPlayer.pause();
  }

  void setPaused(boolean setPause)
  {
    paused = setPause;
  }

  /**
   * @return group
   * the Group that is used by zombieHouse3d to initialize content
   */
  private Parent createContent() throws Exception
  {
    boolean isWall;
    int numZombies = 0;
    PointLight exitLight;

    root = new Group();
    root.setCache(true);
    root.setCacheHint(CacheHint.SPEED);

    // initialize entity manager
    entityManager = new EntityManager(scenes, root);
    entityManager.setZombieHouse3d(this);
    entityManager.createProps();
    entityManager.createZombies(gameBoard, boardHeight, boardWidth);
    numZombies = entityManager.zombies.size();

    // Initialize camera
    camera = new PerspectiveCamera(true);
    camera.getTransforms().addAll(new Rotate(0, Rotate.Y_AXIS), new Rotate(0, Rotate.X_AXIS), new Translate(0, -.5, 0));
    camera.setFieldOfView(60);
    camera.setFarClip(15);
    camera.setRotationAxis(Rotate.Y_AXIS);

    // Initialize player
    if (entityManager.player == null)
    {
      entityManager.chainsaw = new Chainsaw(3,2);
      //entityManager.prop1 = new Prop1(3, 2);
      light.setColor(Color.GRAY);
      entityManager.player = new Player(camera, entityManager, light, entityManager.chainsaw);
    }

    // Lighting
    root.getChildren().add(entityManager.player.light);

    // Materials
    TextureMaps.initializeMaps();

    // Build the Scene Graph
    for (int col = 0; col < boardHeight; col++)
    {
      for (int row = 0; row < boardWidth; row++)
      {
        floorDrawingBoard[col][row] = new Box(1, 0, 1);
        roofDrawingBoard[col][row] = new Box(1, 0, 1);
        switch (gameBoard[col][row].type)
        {
          case wall:
            floorDrawingBoard[col][row] = new Box(1, 2, 1);
            floorDrawingBoard[col][row].setMaterial(TextureMaps.brickMaterial);
            break;
          case region1:
            floorDrawingBoard[col][row].setMaterial(TextureMaps.redMaterial);
            roofDrawingBoard[col][row].setMaterial(TextureMaps.redMaterial);
            break;
          case region2:
            floorDrawingBoard[col][row].setMaterial(TextureMaps.yellowMaterial);
            roofDrawingBoard[col][row].setMaterial(TextureMaps.yellowMaterial);
            break;
          case region3:
            floorDrawingBoard[col][row].setMaterial(TextureMaps.blueMaterial);
            roofDrawingBoard[col][row].setMaterial(TextureMaps.blueMaterial);
            break;
          case region4:
            floorDrawingBoard[col][row].setMaterial(TextureMaps.blackMaterial);
            roofDrawingBoard[col][row].setMaterial(TextureMaps.blackMaterial);
            break;
          case exit:
            floorDrawingBoard[col][row].setMaterial(TextureMaps.ironMaterial);
            roofDrawingBoard[col][row].setMaterial(TextureMaps.ironMaterial);
            Box box = new Box(1, 2, 1);
            box.setTranslateX(gameBoard[col][row].zPos);
            box.setTranslateZ(gameBoard[col][row].xPos);
            box.setMaterial(TextureMaps.glowMaterial);
            exits.add(box);

            break;
        }
        if (col == 0 || col == boardHeight - 1 || row == 0 || row == boardWidth - 1)
        {
          floorDrawingBoard[col][row].setTranslateX(row + .5);
          floorDrawingBoard[col][row].setTranslateZ(col + .5);
          roofDrawingBoard[col][row].setTranslateX(row + .5);
          roofDrawingBoard[col][row].setTranslateZ(col + .5);
        }
        else
        {
          floorDrawingBoard[col][row].setTranslateX(gameBoard[col][row].xPos);
          floorDrawingBoard[col][row].setTranslateZ(gameBoard[col][row].zPos);
          roofDrawingBoard[col][row].setTranslateX(gameBoard[col][row].xPos);
          roofDrawingBoard[col][row].setTranslateZ(gameBoard[col][row].zPos);
        }
        if (!gameBoard[col][row].type.equals(TileType.wall))
        {
          floorDrawingBoard[col][row].setTranslateY(-1);
          roofDrawingBoard[col][row].setTranslateY(1);
        }

        root.getChildren().add(floorDrawingBoard[col][row]);
        root.getChildren().add(roofDrawingBoard[col][row]);
      }
    }
    region1Prop();
    region2Prop();
    region3Prop();
    region4Prop();
    dreamRunner();
    // Spawn zombies on board and create list of wall tiles for
    // purposes of collision detection.
    for (int col = 0; col < boardHeight; col++)
    {
      for (int row = 0; row < boardWidth; row++)
      {
        if (gameBoard[col][row].getType().equals("wall"))
        {
          walls.add(floorDrawingBoard[col][row]);
          entityManager.numTiles++;
          isWall = true;
        }
        else
        {
          isWall = false;
        }
        // The following code calls the appropriate methods to build the graph
        // to be used in zombie pathfinding.
        if (col == 0 && row == 0)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col][row + 1], gameBoard[col + 1][row + 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col == 0 && row == boardWidth - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col][row - 1], gameBoard[col + 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col == boardHeight - 1 && row == 0)
        {
          GraphNode newNode = new GraphNode(gameBoard[col - 1][row], gameBoard[col][row + 1], gameBoard[col - 1][row + 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col == boardHeight - 1 && row == boardWidth - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col - 1][row], gameBoard[col][row - 1], gameBoard[col - 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (row == 0 && col != 0 && col != boardHeight - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col - 1][row], gameBoard[col][row + 1], gameBoard[col + 1][row + 1], gameBoard[col - 1][row + 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (row == boardWidth - 1 && col != 0 && col != boardHeight - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col - 1][row], gameBoard[col][row - 1], gameBoard[col + 1][row - 1], gameBoard[col - 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col == 0 && row != 0 && row != boardWidth - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col][row + 1], gameBoard[col][row - 1], gameBoard[col + 1][row + 1], gameBoard[col + 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col == boardHeight - 1 && row != 0 && row != boardWidth - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col][row + 1], gameBoard[col - 1][row], gameBoard[col][row - 1], gameBoard[col - 1][row + 1], gameBoard[col - 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
        if (col >= 1 && col < boardHeight - 1 && row >= 1 && row < boardWidth - 1)
        {
          GraphNode newNode = new GraphNode(gameBoard[col + 1][row], gameBoard[col - 1][row], gameBoard[col][row + 1], gameBoard[col][row - 1], gameBoard[col + 1][row + 1], gameBoard[col + 1][row - 1], gameBoard[col - 1][row + 1], gameBoard[col - 1][row - 1], row, col, isWall, gameBoard[col][row]);
          TileGraph.createGraph(newNode);
        }
      }
    }

    System.out.println("Number of Zombies: " + entityManager.zombies.size());
    for (Zombie zombie : entityManager.zombies)
    {
      if (zombie.isMasterZombie)
      {
        zombie.setMesh(loadMeshViews(Lambent_Female));
      }
      else
      {
        zombie.setMesh(loadMeshViews(Feral_Ghoul));
      }
      root.getChildren().addAll(zombie.getMesh());
    }

    root.getChildren().addAll(entityManager.player.chainsaw.getMesh());
//    root.getChildren().addAll(entityManager.prop1.getMesh());
    root.getChildren().addAll(entityManager.prop2.getMesh());
    root.getChildren().addAll(entityManager.prop3.getMesh());
    root.getChildren().addAll(entityManager.prop4.getMesh());
    root.getChildren().addAll(entityManager.prop5.getMesh());
    root.getChildren().addAll(entityManager.prop6.getMesh());

    exitLight = new PointLight();
    exitLight.setTranslateX(exits.get(0).getTranslateX());
    exitLight.setTranslateZ(exits.get(0).getTranslateZ());
    root.getChildren().addAll(exits);
    root.getChildren().add(exitLight);

    // Use a SubScene
    SubScene subScene = new SubScene(root, 1280, 800, true, SceneAntialiasing.BALANCED);
    subScene.setFill(Color.BLACK);
    subScene.setCamera(camera);
    subScene.setCursor(Cursor.DISAPPEAR);

    Group group = new Group();
    group.getChildren().add(subScene);
//    group.addEventFilter(MouseEvent.ANY, new MouseEventHandler(camera, entityManager.player, this));

    return group;
  }

  //spawns the crawler
  private void region1Prop()
  {
    Random rand = new Random();
    int x;
    while(true)
    {
      x = 1 + rand.nextInt(boardHeight-1);
      if(!gameBoard[x][1].getType().equals("wall") && !gameBoard[x][0].getType().equals("exit"))
      {
        gameBoard[x][1].setType(TileType.wall);
        entityManager.prop3 = new Prop3(x*tileSize,1, 0);
        break;
      }
    }
  }

  private void region2Prop()
  {
    Random rand = new Random();
    int z;
    while(true)
    {
      z = 1 + rand.nextInt(boardWidth-1);
      if(!gameBoard[1][z].getType().equals("wall") && !gameBoard[0][z].getType().equals("exit"))
      {
        gameBoard[1][z].setType(TileType.wall);
        entityManager.prop4 = new Prop3(1,z*tileSize, 1);
        break;
      }
    }
  }

  private void region3Prop()
  {
    Random rand = new Random();
    int x;
    while(true)
    {
      x = 1 + rand.nextInt(boardWidth-1);
      if(!gameBoard[x][boardWidth-2].getType().equals("wall") && !gameBoard[x][boardWidth-1].getType().equals("exit"))
      {
        gameBoard[x][boardWidth-2].setType(TileType.wall);
        entityManager.prop5 = new Prop3(x*tileSize,boardWidth-1, 2);
        break;
      }
    }
  }

  private void region4Prop()
  {
    Random rand = new Random();
    int z;
    while(true)
    {
      z = 1 + rand.nextInt(boardWidth-1);
      if(!gameBoard[boardHeight-2][z].getType().equals("wall") && !gameBoard[boardHeight-1][z].getType().equals("exit"))
      {
        gameBoard[boardHeight-2][z].setType(TileType.wall);
        entityManager.prop6 = new Prop3(boardHeight-1,z, 3);
        break;
      }
    }
  }

  //spawn dream runner
  private void dreamRunner()
  {
    Random rand = new Random();
    int x;
    int z;
    while(true)
    {
      x = 1 + rand.nextInt(boardWidth-2);
      z = 1 + rand.nextInt(boardWidth-2);
      if(!gameBoard[x][z].getType().equals("wall") && gameBoard[x][z+1].getType().equals("wall"))
      {
        gameBoard[x][z].setType(TileType.wall);
        entityManager.prop2 = new Prop2(x+.5,z);
        break;
      }
    }
  }

  /**
   * @param input The filepath to the mesh (.obj)
   * @return mesh
   * The Node[] that contains the model
   */
  public static Node[] loadMeshViews(String input)
  {
    ObjModelImporter importer = new ObjModelImporter();
    importer.setOptions(ObjImportOption.NONE);
    importer.read(input);
    Node[] mesh = importer.getImport();
    for (int i = 0; i < mesh.length; i++)
    {
      mesh[i].setTranslateY(2);
      mesh[i].setScaleX(0.4);
      mesh[i].setScaleY(0.4);
      mesh[i].setScaleZ(0.4);
      mesh[i].setCache(true);
      mesh[i].setCacheHint(CacheHint.SPEED);
    }
    importer.close();
    return mesh;
  }

  /**
   * The animation timer used in running the game.
   */
  private class MainGameLoop extends AnimationTimer
  {
    /**
     * Call the appropriate method to update the attributes of the
     * entities in the game.
     */
    public void handle(long now)
    {
      if (!paused)
      {
        entityManager.tick();
      }
    }
  }
}