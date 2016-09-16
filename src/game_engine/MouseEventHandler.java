package game_engine;

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
 * 
 * @author Atle Olson 
 *        Jeffrey McCall
 *          
 * Handles mouse events in the game. Moves the player camera to the 
 * left and right. 
 */
public class MouseEventHandler implements EventHandler<MouseEvent>
{
  private Player player;
  private PerspectiveCamera camera;
  
  //The angle that the camera is rotated to.
//  static double angle = 0;
  double lastX = 0;
  double lastY = 0;
  double rotationSpeed = Math.PI/2;
  
  //The arrayCounter is used to keep track of the view angle
  //for movement purposes.
  static double arrayCounter=0;
  
  
  /**
   * Constructor for the program.
   * @param camera
   * A PerspectiveCamera object must be passed in.
   */
  public MouseEventHandler(PerspectiveCamera camera, Player player)
  {
    this.camera = camera;
    this.player = player;
  }
  
  /**
   * Handles all of the mouse movement events. 
   * @param event
   * All mouse motion events are automatically passed into
   * this method.
   */
  @Override
  public void handle(MouseEvent event)
  {
    double x = event.getX();
    if (x > lastX)
    {
      arrayCounter-=rotationSpeed;
      if(arrayCounter>=2*Math.PI)
      {
        arrayCounter=0;
      }
      camera.setRotate(player.angle += rotationSpeed);
    }
    if (x < lastX)
    {
      arrayCounter += rotationSpeed;
      if(arrayCounter<0)
      {
        arrayCounter=Math.PI-rotationSpeed;
      }
      camera.setRotate(player.angle -= rotationSpeed);
    }
    lastX = x;
  }
}
