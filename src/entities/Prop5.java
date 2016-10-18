package entities;

import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;

/**
 * Created by javierz on 10/17/16.
 */
public class Prop5 {
    private final double Y_TRANSLATE = 0;

    private ObjModelImporter o;
    private Node[] in;
    public Prop5(double xTranslate, double zTranslate)
    {
        o = new ObjModelImporter();
        o.setOptions(ObjImportOption.NONE);
        o.read("Resources/Meshes/Crawler2/hdemon.obj");
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
