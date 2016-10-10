package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

/**
 * Created by javier on 10/6/2016.
 */
public class Chainsaw extends Entity
{
  private EntityManager entityManager;
  private ObjModelImporter o;
  private Node[] in;

  public Chainsaw()
  {
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    //o.read("Resources/Meshes/Sofa/Sofa.obj");
    o.read("Resources/Meshes/Chainsaw/chainsaw_no_Modifiere.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setScaleX(.5);
      in[i].setScaleY(.5);
      in[i].setScaleZ(.5);
      in[i].setTranslateX(3);
      in[i].setTranslateY(0);
      in[i].setTranslateZ(4);
    }
    o.close();
  }

  public Node[] getMesh()
  {
    return this.in;
  }

  public void tick()
  {

  }
}
