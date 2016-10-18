package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsImportOption;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
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
  private Node[] in;
  private Group root;
  private boolean dead;
  private int position = 1;

  public PlayerGhost(ArrayList<CreaturePathInfo> path, Group root)
  {
    this.path = (ArrayList<CreaturePathInfo>) path.clone();
    this.root = root;
    ObjModelImporter ghostMesh = new ObjModelImporter();
    ghostMesh.setOptions(ObjImportOption.NONE);
    ghostMesh.read("Resources/Meshes/ghost/ghost_s.obj");
    in = ghostMesh.getImport();
    for (int i = 0; i < in.length; i++)
    {
//      in[i].setScaleY(.08);
//      in[i].setScaleZ(-.08);
      in[i].setTranslateX(path.get(0).getX());
      in[i].setTranslateY(.6);
      in[i].setTranslateZ(path.get(0).getZ());
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setRotate(path.get(0).getAngle());
    }
    ghostMesh.close();
    root.getChildren().addAll(in);
  }

  /**
   *
   */
  public void tick()
  {
    if(!dead)
    {
      if (path.size() > position)
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
          in[i].setVisible(false);
        }
        dead = true;
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
    dead = false;
    for (int i = 0; i < in.length; i++)
    {
      in[i].setVisible(true);
    }
  }
}
