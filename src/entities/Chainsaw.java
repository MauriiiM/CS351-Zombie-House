package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

/**
 * @author Javier
 * @author Mauricio
 */
public class Chainsaw extends Entity
{
  private final double SCALE = .009;
  private final double Y_TRANSLATE = 11.95;
  private final double X_OFFSET = 41.7;
  private final int Z_OFFSET = 4;

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
      in[i].setRotate(80);
      in[i].setTranslateX(X_OFFSET + xTranslate);
      in[i].setTranslateY(Y_TRANSLATE);
      in[i].setTranslateZ(Z_OFFSET + zTranslate);
//      in[i].setRotationAxis(Rotate.Z_AXIS);
//      in[i].setRotate(15);
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
