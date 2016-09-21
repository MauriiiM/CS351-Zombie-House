package entities;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

/**
 * Created by anruiz110 on 9/18/16.
 * @author Anacaren Ruiz
 * Prop object used to place furniture
 * or other prop objects in Zombie House
 * --extends Entity to manage location attributes
 */
public class Prop extends Entity
{

    //entityManager
    EntityManager entityManager;
    private int twoDSize = 3;
    public Circle propCirc = null;
    public boolean twoDBoard = false;
    public Node[] propMesh = null;
    public String propName;


    public Prop(String name, double xPos, double yPos, double zPos, EntityManager em){
        this.propName = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.entityManager = em;
    }

    public String getPropName(){
        return propName;
    }


    /**
     * Gets prop mesh associated with this prop object.
     * @return
     *      The prop mesh.
     */
    public Node[] getMesh()
    {
        return this.propMesh;
    }

    /**
     *
     * Sets prop mesh to specified mesh
     *
     * @param propMesh
     *        Node array contains all parts of mesh
     *
     */
    public void setMesh(Node[] propMesh)
    {
        this.propMesh = propMesh;
        for (int i = 0; i < propMesh.length; i++)
        {
            propMesh[i].setRotationAxis(Rotate.Y_AXIS);
        }
    }

    /**
    * @author Anacaren Ruiz
    * Create circle object to represent where props are located in 2D game map
    * Given initial x and y coords to spawn poin on game map
    * @param row           The row of the 2D game map.
    * @param col           The column of the 2D game map.
    * @param props         The list of Prop objects.
    * @param cellSize      The size of cells on the game map.
    */
    public void create2DProp(int row, int col, ArrayList<Prop> props, int cellSize)
    {
        Circle prop = null;
        for ( Prop p: props )
        {
            double xPos = p.xPos;
            double yPos = p.zPos;
            prop = new Circle((xPos * cellSize), (yPos * cellSize), twoDSize, Color.PURPLE);
            propCirc = prop;
        }
    }



    /**
 * NOTE: might not need this method since props should
 * be the constant & stagnant every game
 *
 * Gets rid of values from the last game before we start a
 * new one.
 *
 */
public void dispose()
{
    propMesh = null;

}

    public void tick()
    {

    }
}
