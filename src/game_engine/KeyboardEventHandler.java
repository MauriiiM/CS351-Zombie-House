package game_engine;

import entities.Player;
import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import levels.Tile;

/**
 * @author Atle Olson
 *         Jeffrey McCall
 *         This class handles the input from the keyboard that controls
 *         player movement as well as some other actions.
 */
class KeyboardEventHandler extends InputHandler implements EventHandler<KeyEvent>
{
  /**
   * Constructor for the keyboard event handler.
   *
   * @param camera        The camera used by the player object.
   * @param player        The player object.
   * @param zombieHouse3d The 3D game map renderer.
   */
  KeyboardEventHandler(PerspectiveCamera camera, Player player, ZombieHouse3d zombieHouse3d)
  {
    super(camera, player, zombieHouse3d);
  }

  /**
   * Handles all key press events. If W is pressed, the player moves
   * forward. If S is pressed player moves backwards. A and D move the
   * player from side to side. Space allows the player to attack. F turns light on and off. Left and right
   * movement keys move the camera either left or right. Up arrow moves the
   * player above the map. The Esc key pauses the game.
   *
   * @param event The key press event.
   */
  @Override
  public void handle(KeyEvent event)
  {
    if (event.getEventType() == KeyEvent.KEY_PRESSED && !player.isDead())
    {
      if (event.getCode() == KeyCode.SHIFT && !gameIsPaused)
      {
        player.shiftPressed.set(true);
      }
      if (event.getCode() == KeyCode.W && !gameIsPaused)
      {
        player.wDown.set(true);
        player.velocity = Player.WALKING_SPEED;
      }
      if (event.getCode() == KeyCode.S && !gameIsPaused)
      {
        player.sDown.set(true);
        player.velocity = -Player.WALKING_SPEED;
      }
      if (event.getCode() == KeyCode.A && !gameIsPaused)
      {
        player.aDown.set(true);
        player.strafeVelocity = Player.WALKING_SPEED;
      }
      if (event.getCode() == KeyCode.D && !gameIsPaused)
      {
        player.dDown.set(true);
        player.strafeVelocity = -Player.WALKING_SPEED;
      }
      if (event.getCode() == KeyCode.F && !gameIsPaused)
      {
        player.light.setLightOn(!player.lightOn);
        player.lightOn = !player.lightOn;
      }
      if (event.getCode() == KeyCode.UP && !gameIsPaused)
      {
        player.camera.setTranslateY(-2 * Tile.tileSize);
//        player.camera.setRotate(90);
      }
      if (event.getCode() == KeyCode.LEFT && !gameIsPaused)
      {
        player.turnLeft = true;
      }
      if (event.getCode() == KeyCode.RIGHT && !gameIsPaused)
      {
        player.turnRight = true;
      }
      if (event.getCode() == KeyCode.SPACE && !gameIsPaused)
      {
        player.attack();
        player.attacking = true;
        player.chainsaw.makeRotate();
        zombieHouse3d.playChainsaw();
      }
      if (event.getCode() == KeyCode.P)
      {
        gameIsPaused = !gameIsPaused;
        zombieHouse3d.setPaused(gameIsPaused);
      }
      if (event.getCode() == KeyCode.ESCAPE)
      {
        mouseIsLocked = !mouseIsLocked;
      }
    }
    else if (event.getEventType() == KeyEvent.KEY_RELEASED)
    {
      if (event.getCode() == KeyCode.SPACE)
      {
        player.notAttack();
        player.attacking = false;
        player.chainsaw.unRotate();
        zombieHouse3d.pauseChainsaw();
      }
      if (event.getCode() == KeyCode.W)
      {
        player.wDown.set(false);
        player.velocity = 0;
      }
      if (event.getCode() == KeyCode.S)
      {
        player.sDown.set(false);
        player.velocity = 0;
      }
      if (event.getCode() == KeyCode.A)
      {
        player.aDown.set(false);
        player.strafeVelocity = 0;
      }
      if (event.getCode() == KeyCode.D)
      {
        player.dDown.set(false);
        player.strafeVelocity = 0;
      }
      if (event.getCode() == KeyCode.SHIFT)
      {
        player.shiftPressed.set(false);
      }
      if (event.getCode() == KeyCode.UP)
      {
        player.camera.setTranslateY(0);
      }
//      if (event.getCode() == KeyCode.SPACE)
//      {
//        player.camera.setTranslateY(0);
//      }
      if (event.getCode() == KeyCode.RIGHT)
      {
        player.turnRight = false;
      }
      if (event.getCode() == KeyCode.LEFT)
      {
        player.turnLeft = false;
      }
    }
  }
}