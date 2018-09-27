package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main entry point of application.
 * 
 * @author Naresh
 *
 */
public class Main extends Application {

	/**
	 * start() is an overridden method from Application abstract class which sets up
	 * the UI for user to interact and create/modify map or start game.
	 * 
	 * @param primaryStage:
	 *            primaryStage is a JavaFx container which provide base for creating
	 *            JavaFx application.
	 * @throws Exception:
	 *             for exception handling.
	 *
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Risk.fxml"));
		primaryStage.setTitle("RISK");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * main() method is used as an entry point for JavaFx application.
	 * 
	 * @param args:
	 *            command line arguments.
	 *
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
