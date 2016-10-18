package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;

/**
 * Created by javier on 10/13/2016.
 */
public class Prop3
{
  private final double Y_TRANSLATE = 0;

  private ObjModelImporter o;
  private Node[] in;
  public Prop3(double xTranslate, double zTranslate, int x)
  {
    o = new ObjModelImporter();
    o.setOptions(ObjImportOption.NONE);
    if(x == 0) o.read("Resources/Meshes/Crawler/hdemon.obj");
    else o.read("Resources/Meshes/Crawler" + x + "/hdemon.obj");
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
