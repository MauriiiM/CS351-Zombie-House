package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

/**
 * @author Javier Zazueta
 * @author Mauricio Monsivais
 */
public class PlayerGhost extends Creature
{
  private ArrayList<CreaturePathInfo> path;
  private ObjModelImporter ghostMesh;
  private Node[] in;
  private int position = 1;

  public PlayerGhost(ArrayList<CreaturePathInfo> path, Group root)
  {
    this.path = path;
    ghostMesh = new ObjModelImporter();
    ghostMesh.setOptions(ObjImportOption.NONE);
    ghostMesh.read("Resources/Meshes/DreamRunner/d_runner.obj");
    in = ghostMesh.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setTranslateX(path.get(0).getX());
      in[i].setTranslateZ(path.get(0).getZ());
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setRotate(path.get(0).getAngle());
    }
    ghostMesh.close();
    root.getChildren().addAll(in);
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
