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
  private final double X_OFFSET = 38.7;
  private final int Z_OFFSET = 2;
  private final int ROTATE = 80;

  private EntityManager entityManager;
  private ObjModelImporter o;
  private Node[] in;

  public boolean rotate;

  public Chainsaw(double xTranslate, double zTranslate)
  {
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
      in[i].setRotate(ROTATE);
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

  public void makeRotate()
  {
    in[0].setRotationAxis(Rotate.Z_AXIS);
    in[0].setRotate(30);
    in[1].setRotationAxis(Rotate.Z_AXIS);
    in[1].setRotate(30);

//    in[0].setRotationAxis(Rotate.Y_AXIS);
//    in[0].setRotate(ROTATE);
//    in[1].setRotationAxis(Rotate.Y_AXIS);
//    in[1].setRotate(ROTATE);
  }

  public void unRotate()
  {
    in[0].setRotationAxis(Rotate.Z_AXIS);
    in[0].setRotate(0);
    in[1].setRotationAxis(Rotate.Z_AXIS);
    in[1].setRotate(0);
    in[1].setRotationAxis(Rotate.Y_AXIS);
    in[0].setRotationAxis(Rotate.Y_AXIS);


    //    in[0].setRotationAxis(Rotate.Y_AXIS);
//    in[0].setRotate(ROTATE);
//    in[1].setRotationAxis(Rotate.Y_AXIS);
//    in[1].setRotate(ROTATE);
  }

  public void setTranslateX(double xOffset)
  {
    in[0].setTranslateX(xOffset + X_OFFSET);
    in[1].setTranslateX(xOffset + X_OFFSET);
  }

  public void setTranslateZ(double zOffset)
  {
    in[0].setTranslateZ(zOffset + Z_OFFSET);
    in[1].setTranslateZ(zOffset + Z_OFFSET);
  }

  public void setRotate(double angle)
  {
    in[0].setRotate(angle + ROTATE);
    in[1].setRotate(angle + ROTATE);
  }

  public void tick()
  {

  }
}
