package entities;

import graphing.GraphNode;
import graphing.Heading;
import graphing.NodeComparator;
import graphing.TileGraph;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import levels.Tile;
import utilities.ZombieBoardRenderer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jeffrey McCall This class is used for zombie pathfinding to find
 *         the shortest distance from the zombie to the player.
 */
class CalculatePath
{
  Comparator<GraphNode> comparator = new NodeComparator();
  PriorityQueue<GraphNode> priorityQueue = new PriorityQueue<GraphNode>(1, comparator);
  LinkedHashMap<Tile, Tile> cameFrom = new LinkedHashMap<>();
  LinkedHashMap<Tile, Double> costSoFar = new LinkedHashMap<>();

  private EntityManager entityManager;
  private Zombie zombie;

  private double newCost;
  private double priority;
  private int lastPathSize = 0;
  ArrayList<Circle> oldPath = new ArrayList<>();
  int distanceToPlayer;
  LinkedList<Tile> path;
  Tile destination;
  Tile end;
  boolean twoD = false;
  AtomicBoolean findNewPath;

  CalculatePath(EntityManager entityManager, Zombie zombie)
  {
    this.entityManager = entityManager;
    this.zombie = zombie;
    findNewPath = zombie.findNewPath;
  }

  /**
   * This method implements the A* algorithm to find the shortest distance
   * between the zombie and the player. I based my implementation on Justin
   * Hall's A* pathfinding program posted on the CS 351 website,
   * https://www.cs.unm.edu/~joel/cs351/. His implementation was itself based
   * on the implementation found on the website
   * http://www.redblobgames.com/pathfinding/a-star/introduction.html.
   *
   * @param from       The tile the zombie is at.
   * @param to         The tile the player is at.
   * @param zombieNode The node on the graph that represents the location of the
   *                   zombie.
   */
  void findPath(Tile from, Tile to, GraphNode zombieNode)
  {
    if (from != null && to != null)
    {
      end = to;
      destination = to;
      priorityQueue.add(zombieNode);
      costSoFar.put(from, 0.0);
      cameFrom.put(from, null);
      while (!priorityQueue.isEmpty())
      {
        GraphNode currentNode = priorityQueue.peek();
        Tile current = priorityQueue.poll().nodeTile;
        if (current.equals(to))
        {
          break;
        }
        for (Tile neighbor : currentNode.neighbors)
        {
          if (costSoFar.get(current) != null)
          {
            newCost = costSoFar.get(current) + neighbor.movementCost;
            if ((!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) && !checkNeighbors(current, neighbor, currentNode))
            {
              costSoFar.put(neighbor, newCost);
              priority = newCost + findDistance(neighbor, to);
              GraphNode nextNode = TileGraph.getNode(neighbor);
              nextNode.priority = priority;
              priorityQueue.add(nextNode);
              cameFrom.put(neighbor, current);
            }
          }
        }
      }
    }
    distanceToPlayer = getPathLength(cameFrom, to);
    if (twoD)
    {
      drawPath();
    }
    cameFrom.clear();
    priorityQueue.clear();
    costSoFar.clear();
  }

  /**
   * This method is used with A* to check if a tile is next to a wall. If it
   * is, and there is the possibility of diagonal movement to either side of
   * that wall, then we want to make it so that the path doesn't go in the
   * diagonal direction, and can only go to the tiles on either side of the
   * current tile. We are doing this since movement in the diagonal direction
   * would mean that the zombie would be trying to move through a wall.
   *
   * @param current     The current tile we are evaluating for pathfinding.
   * @param neighbor    The neighboring tile of the current tile.
   * @param currentNode The current node in the tile graph that is being evaluated.
   * @return True if the neighbor is a diagonal tile and the current tile is
   * against a wall. False otherwise.
   */
  private boolean checkNeighbors(Tile current, Tile neighbor, GraphNode currentNode)
  {
    if (!twoD)
    {
      if (currentNode.wallToRight)
      {
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col - 1][current.row - 1]))
        {
          return true;
        }
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col + 1][current.row - 1]))
        {
          return true;
        }
      }
      if (currentNode.wallToLeft)
      {
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col - 1][current.row + 1]))
        {
          return true;
        }
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col + 1][current.row + 1]))
        {
          return true;
        }
      }
      if (currentNode.wallOnBottom)
      {
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col + 1][current.row - 1]))
        {
          return true;
        }
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col + 1][current.row + 1]))
        {
          return true;
        }
      }
      if (currentNode.wallOnTop)
      {
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col - 1][current.row + 1]))
        {
          return true;
        }
        if (neighbor.equals(entityManager.zombieHouse.getGameBoard()[current.col - 1][current.row - 1]))
        {
          return true;
        }
      }
    }
    else
    {
      if (currentNode.wallToRight)
      {
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col - 1][current.row + 1]))
        {
          return true;
        }
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col + 1][current.row + 1]))
        {
          return true;
        }
      }
      if (currentNode.wallToLeft)
      {
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col - 1][current.row - 1]))
        {
          return true;
        }
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col + 1][current.row - 1]))
        {
          return true;
        }
      }
      if (currentNode.wallOnBottom)
      {
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col + 1][current.row - 1]))
        {
          return true;
        }
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col + 1][current.row + 1]))
        {
          return true;
        }
      }
      if (currentNode.wallOnTop)
      {
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col - 1][current.row + 1]))
        {
          return true;
        }
        if (neighbor.equals(ZombieBoardRenderer.gameBoard[current.col - 1][current.row - 1]))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get length of path between player and zombie. We do this since the zombie
   * only goes after the player in the case of the shortest path length being
   * less than 15. The way I structured this was partially inspired by code from
   * Justin Hall's A* pathfinding program from his "printPath" method. This code is
   * from the CS 351 website:
   * https://www.cs.unm.edu/~joel/cs351/
   *
   * @param cameFrom The map that represents the shortest path that was found to get
   *                 to the player.
   * @param end      The ending tile in the path. This is where the player is.
   * @return The length of the shortest path to the player.
   */
  private int getPathLength(LinkedHashMap<Tile, Tile> cameFrom, Tile end)
  {
    int counter = 0;
    LinkedList<Tile> path = new LinkedList<>();
    Tile curr = end;
    while (curr != null)
    {
      path.addFirst(curr);
      curr = cameFrom.get(curr);
    }
    if (path.size() >= 2 && findNewPath.get())
    {
      calculateHeadings(path.get(0), path.get(1));
    }
    counter = path.size();
    return counter;
  }

  /**
   * When 2D board is being displayed, draw the paths from each zombie to the
   * player on the screen.
   */
  private void drawPath()
  {
    LinkedList<Tile> path = new LinkedList<>();
    ArrayList<Circle> circles = new ArrayList<>();
    Tile curr = end;
    while (curr != null)
    {
      path.addFirst(curr);
      curr = cameFrom.get(curr);
    }
    for (Tile n : path)
    {
      Circle pathCircle = new Circle(n.xPos * ZombieBoardRenderer.cellSize, n.zPos * ZombieBoardRenderer.cellSize, 2, Color.WHITE);
      circles.add(pathCircle);
    }
    if (lastPathSize != 0)
    {
      ZombieBoardRenderer.root.getChildren().removeAll(oldPath);
    }
    ZombieBoardRenderer.root.getChildren().addAll(circles);
    lastPathSize = circles.size();
    oldPath = circles;
  }

  /**
   * When the zombie is out of detection range of the player on the
   * 2D board, remove the visual representation of the path from
   * the screen.
   */
  void removePath()
  {
    ZombieBoardRenderer.root.getChildren().removeAll(oldPath);
  }

  /**
   * Finds the Manhattan distance between a certain location on the map and
   * the player's location. This is based on code from:
   * http://www.redblobgames.com/pathfinding/a-star/introduction.html
   *
   * @param tile1 The first location.
   * @param tile2 The location of the player.
   * @return The distance between the two locations.
   */
  int findDistance(Tile tile1, Tile tile2)
  {
    return (int) (Math.abs(tile1.xPos - tile2.xPos) + Math.abs(tile1.zPos - tile2.zPos));
  }

  /**
   * This method calculates the heading for the zombie to travel to go in the
   * direction of the player.
   *
   * @param tile1 The starting position of the zombie.
   * @param tile2 The next position on the path towards the player.
   */
  private void calculateHeadings(Tile tile1, Tile tile2)
  {
    Heading newHeading = new Heading(tile1, tile2);
    zombie.setZombieHeading(newHeading);
  }
}
