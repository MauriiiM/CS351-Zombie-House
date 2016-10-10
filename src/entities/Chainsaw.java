package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.sun.org.apache.xml.internal.dtm.Axis;
import javafx.geometry.Point3D;
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
    //o.read("Resources/Meshes/HarleyQuinn/Harley.obj");
    o.read("Resources/Meshes/LollipopChainsaw/chainsaw.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setScaleX(.009);
      in[i].setScaleY(.009);
      in[i].setScaleZ(.009);
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setTranslateX(42);
      in[i].setTranslateY(11.95);
      in[i].setTranslateZ(4);
      in[i].setRotationAxis(Rotate.Z_AXIS);
      in[i].setRotate(15);
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
