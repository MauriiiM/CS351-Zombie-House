package game_engine;

import com.sun.glass.ui.Robot;
import entities.Player;
import javafx.scene.PerspectiveCamera;

/**
 * @author Mauricio Monsivais
 */
class InputHandler
{
  ZombieHouse3d zombieHouse3d;
  Player player;
  PerspectiveCamera camera;
  static boolean gameIsPaused;
  static boolean mouseIsLocked;
  static Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
  static double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
  static double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();

  InputHandler(PerspectiveCamera camera, Player player, ZombieHouse3d zombieHouse3d)
  {
    this.zombieHouse3d = zombieHouse3d;
    this.camera = player.camera = camera;
    this.player = player;
    gameIsPaused = zombieHouse3d.getPaused();
    mouseIsLocked = false;
  }
}
