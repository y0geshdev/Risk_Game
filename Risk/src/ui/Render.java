package ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Continent;
import domain.GameConstants;
import domain.Territory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * 
 * This class is using the objects of the Continent and Territory Classes to
 * render the relevant map on UI.
 * 
 * @author Shivam
 * @version 1.0.0
 * 
 */
public class Render extends Application {

	private static Set<Continent> continentSetObj;
	private static Set<Territory> territorySetObj;
	private static Map<String, TextField> idTotextFieldMapping = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle(GameConstants.GAME_TITLE);
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(20, 20, 20, 20));
		rootPane.setVgap(8);
		rootPane.setHgap(10);
		TextField numberOfPlayerCount = new TextField();
		Label numberOfPlayer = new Label();
		Button startGameButton = new Button();

		GridPane.setConstraints(numberOfPlayer, 0, 0);
		GridPane.setConstraints(numberOfPlayerCount, 1, 0);
		GridPane.setConstraints(startGameButton, 2, 0);

		numberOfPlayer.setText(GameConstants.NO_OF_PLAYER_TEXT);
		numberOfPlayerCount.setPromptText(GameConstants.COUNT_OF_PLAYERS);
		startGameButton.setText(GameConstants.START_TEXT);
		startGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				formMap(rootPane);

			}
		});
		rootPane.getChildren().addAll(numberOfPlayer, numberOfPlayerCount, startGameButton);
		primaryStage.setScene(new Scene(rootPane, 800, 400));
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	/**
	 * This function is used to generate TextBoxes and Labels denoting Territories
	 * and Continents of the game respectively. This is also adding the Mouse Click
	 * event on the text fields to show their NeighbouringTerritories by clicking
	 * the TextField twice and highlighting it's Neighbour's border by red.
	 * 
	 * @param pane
	 *            : This is the pane which will be seen on the stage and this
	 *            function is adding different nodes to this pane
	 */
	private static void formMap(GridPane pane) {

		Iterator<Continent> ite = continentSetObj.iterator();
		int colCounter = 0;
		while (ite.hasNext()) {
			Continent obj = ite.next();
			String nameofTheContinent = obj.getName();
			List<Territory> territoryList = obj.getTerritories();
			Integer controlValue = obj.getContinentArmyValue();
			Label continentName = new Label(nameofTheContinent);
			Label controlValueLabel = new Label(GameConstants.CONTROL_VALUE_WITH_SEMICOLON + controlValue.toString());
			pane.setConstraints(continentName, colCounter, 1);
			pane.setConstraints(controlValueLabel, colCounter, 2);
			pane.getChildren().addAll(continentName, controlValueLabel);
			for (int i = 0; i < territoryList.size(); i++) {
				Territory territoryObj = territoryList.get(i);
				TextField territoryField = new TextField();
				pane.setConstraints(territoryField, colCounter, i + 3);
				territoryField.setPromptText(territoryObj.getName());
				territoryField.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if (event.getClickCount() == 2)
							highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories(),
									event.getClickCount());
						highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories(),
								event.getClickCount());
					}
				});

				idTotextFieldMapping.put(territoryObj.getName(), territoryField);
				pane.getChildren().add(territoryField);
			}
			colCounter++;
		}

	}

	/**
	 * This function is being used to help in highlighting the Neighbouring
	 * territories depending on the number of clicks on the TextBox. If the TextBox
	 * is clicked twice it highlight the neighbouring territories' border by red and
	 * convert it back to the original colour if clicked otherwise
	 * 
	 * @param neighbouringCountries
	 *            : This list gives the neighbouring territories of a territory
	 * @param clickCount
	 *            : This gives the count of the clicks on the textfield.
	 */
	private static void highlightNeighbouringTerritoriesHelper(List<Territory> neighbouringCountries, int clickCount) {

		for (int i = 0; i < neighbouringCountries.size(); i++) {
			Territory t = neighbouringCountries.get(i);
			TextField tf = idTotextFieldMapping.get(t.getName());
			if (clickCount == 2) {
				tf.setStyle(GameConstants.TEXTFIELD_BORDER_COLOUR);
			} else {
				tf.setStyle("");
			}
		}

	}

	/**
	 * This function is used by the MapFileParser class to call this class. This
	 * function is calling main to render the primaryStage.
	 * 
	 * @param continentObjectSet
	 *            : this contains the Continent Objects in a set which were created
	 *            while parsing the file.
	 * @param territoryObjectSet
	 *            : this contains the Territory Objects in a set which were created
	 *            while parsing the file.
	 */
	public void startGame(Set<Continent> continentObjectSet, Set<Territory> territoryObjectSet) {
		String[] args = new String[2];
		continentSetObj = continentObjectSet;
		territorySetObj = territoryObjectSet;

		main(args);
	}

	/**
	 * This is used to run the application from calling javafx's launch function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
