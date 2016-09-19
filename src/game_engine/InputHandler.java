package game_engine;

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

  InputHandler(PerspectiveCamera camera, Player player, ZombieHouse3d zombieHouse3d)
  {
    this.zombieHouse3d = zombieHouse3d;
    this.camera = player.camera = camera;
    this.player = player;
    gameIsPaused = zombieHouse3d.getPaused();
    mouseIsLocked = true;
  }
}
