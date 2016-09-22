package gui;

import game_engine.Scenes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sounds.SoundManager;

/**
 * @author Atle Olson
 *         Main launch point for the program
 */
public class Main extends Application
{
  private Stage primaryStage = new Stage();
  private SoundManager soundManager;
  private Scenes scenes = new Scenes(primaryStage, this);

  /**
   * @param primaryStage Start function to launch program
   */
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    soundManager = new SoundManager();
    scenes.setSoundManager(soundManager);
    soundManager.playTrack(0);
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Zombie House By: (Anacaren, Javier, & Mauricio) and (Atle, Ben, & Jeffrey)");
    primaryStage.setScene(scenes.mainMenu);
    primaryStage.show();

    primaryStage.setOnCloseRequest(e -> System.exit(0));
  }

  /**
   * @param scene Sets the stages' scene equal to scene
   */
  public void assignStage(Scene scene)
  {
    primaryStage.setScene(scene);
  }

  /**
   * Gets the current stage
   *
   * @return primaryStage
   */
  public Stage retrieveStage()
  {
    return primaryStage;
  }

  /**
   * Gets the current scene
   *
   * @return primaryStage.getScene()
   */
  public Scene retrieveScene()
  {
    return primaryStage.getScene();
  }

  /**
   * main, launches our program
   */
  public static void main(String[] args)
  {
    launch(args);
  }

}
