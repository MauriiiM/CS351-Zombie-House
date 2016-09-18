
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
 * This class handles the input from the keyboard that controls
 * player movement as well as some other actions.
 */
public class KeyboardEventHandler extends InputHandler implements EventHandler<KeyEvent>
{
/**
 * Constructor for the keyboard event handler.
 * @param camera
 *        The camera used by the player object.
 * @param player
 *        The player object.
 * @param zombieHouse3d
 *        The 3D game map renderer.
 */
  public KeyboardEventHandler(PerspectiveCamera camera, Player player, ZombieHouse3d zombieHouse3d)
  {
    super(camera, player,zombieHouse3d);
  }
  
  /**
   * Handles all key press events. If W is pressed, the player moves
   * forward. If S is pressed player moves backwards. A and D move the 
   * player from side to side. F turns light on and off. Left and right
   * movement keys move the camera either left or right. Space moves the
   * player above the map. The Esc key pauses the game.
   * @param event
   *        The key press event.
   */
  @Override
  public void handle(KeyEvent event)
  {
    if (event.getEventType() == KeyEvent.KEY_PRESSED)
    {
      if(event.isShiftDown()) 
      {
        player.shiftPressed.set(true);
      }
      if(event.getCode()==KeyCode.W && !pauseState)
      {
        player.wDown.set(true);
        player.velocity = Player.WALKINGSPEED;
      }
      if(event.getCode()==KeyCode.S && !pauseState)
      {
        player.sDown.set(true);
        player.velocity = -Player.WALKINGSPEED;
      }
      if(event.getCode()==KeyCode.A && !pauseState)
      {
        player.aDown.set(true);
        player.strafeVelocity = Player.WALKINGSPEED;
      }
      if(event.getCode()==KeyCode.D && !pauseState)
      {
        player.dDown.set(true);
        player.strafeVelocity = -Player.WALKINGSPEED;
      }
      if(event.getCode()==KeyCode.F && !pauseState)
      {
        player.light.setLightOn(!player.lightOn);
        player.lightOn = !player.lightOn;
      }
      if(event.getCode()==KeyCode.LEFT && !pauseState)
      {
        player.turnLeft = true;
      }
      if(event.getCode()==KeyCode.RIGHT && !pauseState)
      {
        player.turnRight = true;
      }
      if(event.getCode()==KeyCode.SPACE)
      {
        player.camera.setTranslateY(-2*Tile.tileSize);
      }
      if(event.getCode()==KeyCode.P)
      {
        pauseState = !pauseState;
        zombieHouse3d.setPaused(pauseState);
      }
    }
    else if (event.getEventType() == KeyEvent.KEY_RELEASED)
    {
      if(event.getCode()==KeyCode.W)
      {
        player.wDown.set(false);
        player.velocity = 0;
      }
      if(event.getCode()==KeyCode.S)
      {
        player.sDown.set(false);
        player.velocity = 0;
      }
      if(event.getCode()==KeyCode.A)
      {
        player.aDown.set(false);
        player.strafeVelocity = 0;
      }
      if(event.getCode()==KeyCode.D)
      {
        player.dDown.set(false);
        player.strafeVelocity = 0;
      }
      if(event.getCode()==KeyCode.SHIFT)
      {
        player.shiftPressed.set(false);
      }
      if(event.getCode()==KeyCode.SPACE)
      {
        player.camera.setTranslateY(0);
      }
      if(event.getCode()==KeyCode.RIGHT)
      {
        player.turnRight = false;
      }
      if(event.getCode()==KeyCode.LEFT)
      {
        player.turnLeft = false;
      }
    }
  }
}