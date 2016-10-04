package game_engine;

import com.sun.glass.ui.Robot;
import entities.Player;
/**
 * @author Jeffrey McCall
 * This class handles all of the mouse input
 * into the game. When the mouse is moved, the camera
 * is rotated appropriately.
 */
import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.MouseEvent;

/**
 * @author Atle Olson
 *         Jeffrey McCall
 *         Mauricio Monsivais - added extends InputHandler
 *         <p>
 *         Handles mouse events in the game. Moves the player camera to the
 *         left and right.
 */
public class MouseEventHandler extends InputHandler implements EventHandler<MouseEvent>
{

  //The angle that the camera is rotated to.
//  static double angle = 0;
//  double lastY = 0;
  private double lastX = 0;

  //The arrayCounter is used to keep track of the view angle
  //for movement purposes.
  static double arrayCounter = 0;


  /**
   * Constructor for the program.
   *
   * @param camera A PerspectiveCamera object must be passed in.
   */
  MouseEventHandler(PerspectiveCamera camera, Player player, ZombieHouse3d zombieHouse3d)
  {
    super(camera, player, zombieHouse3d);
  }

  /**
   * Handles all of the mouse movement events.
   *
   * @param event All mouse motion events are automatically passed into
   *              this method.
   */
  @Override
  public void handle(MouseEvent event)
  {
    double rotationSpeed = Math.PI / 1.3;
    double x = event.getX();

    if (!gameIsPaused)
    {
      if(event.isPrimaryButtonDown())
      {
        player.attack();
      }
      if (x > lastX)
      {
        arrayCounter -= rotationSpeed;
        if (arrayCounter >= 2 * Math.PI)
        {
          arrayCounter = 0;
        }
        camera.setRotate(player.angle += rotationSpeed);
      }
      if (x < lastX)
      {
        arrayCounter += rotationSpeed;
        if (arrayCounter < 0)
        {
          arrayCounter = Math.PI - rotationSpeed;
        }
        camera.setRotate(player.angle -= rotationSpeed);
      }
      lastX = x;
    }
    if(mouseIsLocked) moveMouse();
  }

  private void moveMouse()
  {
    Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
    robot.mouseMove(1280, 800);
    robot.mousePress(0);
    robot.mouseRelease(1);
  }
}
