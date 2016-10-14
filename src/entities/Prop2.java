package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;

/**
 * Created by javier on 10/13/2016.
 */
public class Prop2
{
  private final double Y_TRANSLATE = 1;

  private ObjModelImporter o;
  private Node[] in;

  public Prop2(double xTranslate, double zTranslate)
  {
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    o.read("Resources/Meshes/DreamRunner/d_runner.obj");
    in = o.getImport();
    for(int i = 0; i < in.length; i++)
    {
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
