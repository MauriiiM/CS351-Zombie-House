package levels;

import game_engine.Attributes;
import javafx.scene.shape.Box;

/**
 * @author Mauricio Monsivais
 *
 */
public class Map
{
  private int difficulty;
  private Tile[][] gameBoard;
  private Box[][] floorBoard, ceilingBoard;

  public Map(int difficulty){
    this.difficulty = difficulty;

    gameBoard = newGameBoard();
    floorBoard = new Box[gameBoard[0].length][gameBoard.length];
    ceilingBoard = new Box[gameBoard[0].length][gameBoard.length];
  }

  public Tile[][] getGameBoard()
  {
    return gameBoard;
  }

  public Box[][] getFloorBoard()
  {
    return floorBoard;
  }

  public Box[][] getCeilingBoard()
  {
    return ceilingBoard;
  }

  private Tile[][] newGameBoard()
  {
    return ProceduralMap.generateMap(Attributes.Map_Width, Attributes.Map_Height, difficulty);
  }
}
