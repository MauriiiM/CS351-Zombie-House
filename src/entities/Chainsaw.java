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
  private final double SCALE = .009;
  private final double Y_TRANSLATE = 11.95;
  private final int xOffset = 42;
  private final int zOffset = 4;

  private EntityManager entityManager;
  private ObjModelImporter o;
  private Node[] in;
  private double xTranslate;
  private double zTranslate;


  public Chainsaw(double xTranslate, double zTranslate)
  {
    this.xTranslate = xTranslate;
    this.zTranslate = zTranslate;
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    //o.read("Resources/Meshes/HarleyQuinn/Harley.obj");
    o.read("Resources/Meshes/LollipopChainsaw/chainsaw.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setScaleX(SCALE);
      in[i].setScaleY(SCALE);
      in[i].setScaleZ(SCALE);
      in[i].setRotationAxis(Rotate.Y_AXIS);
      in[i].setRotate(40);
      in[i].setTranslateX(xOffset + xTranslate);
      in[i].setTranslateY(Y_TRANSLATE);
      in[i].setTranslateZ(zOffset + zTranslate);
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
