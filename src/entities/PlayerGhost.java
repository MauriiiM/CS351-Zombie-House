package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Javier Zazueta
 * @author Mauricio Monsivais
 */
public class PlayerGhost extends Creature
{
  private ArrayList<CreaturePathInfo> path;
  private ObjModelImporter o;
  private Node[] in;
  private int position = 1;

  public PlayerGhost(ArrayList<CreaturePathInfo> path)
  {
    this.path = path;
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    o.read("Resources/Meshes/DreamRunner/d_runner.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setTranslateX(path.get(0).getX());
      in[i].setTranslateZ(path.get(0).getZ());
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setRotate(path.get(0).getAngle());
    }
    o.close();
  }

  public void tick()
  {
    if(path.size() < position)
    {
      for (int i = 0; i < in.length; i++)
      {
        in[i].setTranslateX(path.get(position).getX());
        in[i].setTranslateZ(path.get(position).getZ());
        in[i].setRotate(path.get(position).getAngle());
      }
      position++;
    }
    else
    {
      for (int i = 0; i < in.length; i++)
      {

      }
    }
  }

  public void stepSound()
  {

  }

  public double calculateDistance()
  {
    return 0;
  }

  public void reset()
  {
    position = 0;
  }
}
