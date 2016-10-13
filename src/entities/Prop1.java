package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

/**
 * Created by javier on 10/12/2016.
 */
public class Prop1
{
  private final double SCALE = .5;
  private final double Y_TRANSLATE = -1;
  private final double X_OFFSET = 38.7;
  private final int Z_OFFSET = 2;
  private final int ROTATE = 80;

  private ObjModelImporter o;
  private Node[] in;
  public Prop1(double xTranslate, double zTranslate)
  {
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    o.read("Resources/Meshes/Rock/Rock.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
      in[i].setScaleX(SCALE);
      in[i].setScaleY(SCALE);
      in[i].setScaleZ(SCALE);
//      in[i].setRotationAxis(Rotate.Y_AXIS);
//      in[i].setRotate(ROTATE);
      in[i].setTranslateX(xTranslate);
      in[i].setTranslateY(Y_TRANSLATE);
      in[i].setTranslateZ(zTranslate);
    }
    o.close();
  }

  public Node[] getMesh()
  {
    return this.in;
  }
}
