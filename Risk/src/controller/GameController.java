package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Continent;
import domain.Player;
import domain.Territory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import service.GameService;

/**
 * GameController class have all the methods which handle different actions
 * which are performed by user while playing game.
 * 
 * @author Yogesh
 *
 */
public class GameController {

	@FXML
	private GridPane gamePane;

	@FXML
	private Label numberOfPlayers;

	@FXML
	private TextField countOfPlayers;

	@FXML
	private Button renderGame;

	@FXML
	private Label playerName;

	@FXML
	private Button startUpPhase;

	@FXML
	private Button reinforcementPhase;

	@FXML
	private Button attackPhase;

	@FXML
	private Button fortificationPhase;

	@FXML
	private ListView<Territory> playerTerritories;

	@FXML
	private TextField numberOfArmiesPerTerritory;

	@FXML
	private Button endTurn;

	@FXML
	private Button endPhase;

	@FXML
	private Label showNumberOfArmies;

	private Map<String, TextField> idTotextFieldMapping = new HashMap<>();
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";
	public static final String CONTROL_VALUE = "Control Value";
	public static final String TEXTFIELD_BORDER_COLOUR = "-fx-text-box-border: red;";
	private GameService serviceObject = new GameService();
	private int maxNumberOfTerritories = 0;
	private List<Player> playerList = new ArrayList<>();
	private static int playerCounter = 0;
	private Set<Player> playersWithZeroArmies = new HashSet<>();

	/**
	 * This method handles {@link GameController#renderGame} event. It loads the
	 * chosen map and helps in forming playable UI dynamically.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */

	@FXML

	public void startGameHandler(ActionEvent event) {
		int numberOfPlayers = 0;
		String error = new String();
		try {
			numberOfPlayers = Integer.parseInt(countOfPlayers.getText());
			if (numberOfPlayers <= 1) {
				error = "Atleast two players are required to play this game";
				showError(error);
				return;
			} else if (numberOfPlayers > MapController.territoriesSet.size()) {
				error = "Number of Players should be less than or equal to the number of territories";
				showError(error);
				return;
			}
		} catch (NumberFormatException e) {
			error = "Please, Enter a valid number";
			showError(error);
			return;
		}
		playerList = new ArrayList<>(numberOfPlayers);
		int armyCount = getArmyCount(numberOfPlayers);
		for (int i = 0; i < numberOfPlayers; i++) {
			Player playerObj = new Player();
			playerObj.setName("Player " + (i + 1));
			playerObj.setArmyCount(armyCount);
			playerObj.setTerritories(new ArrayList<Territory>());
			playerList.add(playerObj);
		}
		serviceObject.distributeTerritories(playerList);
		formMap();
		createPlayButtonsAndLabels();
		countOfPlayers.setEditable(false);

	}

	/**
	 * This function is used to generate TextBoxes and Labels denoting Territories
	 * and Continents of the game respectively. This is also adding the Mouse Click
	 * event on the text fields to show their NeighbouringTerritories by clicking
	 * the TextField twice and highlighting it's Neighbour's border by red.
	 * 
	 * @param gamePane
	 *            : This is the pane which will be seen on the stage and this
	 *            function is adding different nodes to this pane
	 */
	private void formMap() {

		Iterator<Continent> ite = MapController.continentsSet.iterator();
		int colCounter = 0;
		while (ite.hasNext()) {
			Continent obj = ite.next();
			String nameofTheContinent = obj.getName();
			List<Territory> territoryList = obj.getTerritories();
			if (maxNumberOfTerritories < territoryList.size()) {
				maxNumberOfTerritories = territoryList.size();
			}
			Integer controlValue = obj.getContinentArmyValue();
			Label continentName = new Label(nameofTheContinent);
			Label controlValueLabel = new Label(GameController.CONTROL_VALUE_WITH_SEMICOLON + controlValue.toString());
			gamePane.setConstraints(continentName, colCounter, 1);
			gamePane.setConstraints(controlValueLabel, colCounter, 2);
			gamePane.getChildren().addAll(continentName, controlValueLabel);
			for (int i = 0; i < territoryList.size(); i++) {
				Territory territoryObj = territoryList.get(i);
				TextField territoryField = new TextField();
				gamePane.setConstraints(territoryField, colCounter, i + 3);
				territoryField.setPromptText(territoryObj.getName());
				territoryField.setEditable(false);
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
				gamePane.getChildren().add(territoryField);
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
	private void highlightNeighbouringTerritoriesHelper(List<Territory> neighbouringCountries, int clickCount) {

		for (int i = 0; i < neighbouringCountries.size(); i++) {
			Territory t = neighbouringCountries.get(i);
			TextField tf = idTotextFieldMapping.get(t.getName());
			if (clickCount == 2) {
				tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR);
			} else {
				tf.setStyle("");
			}
		}

	}

	/**
	 * This method is used to form button for all the phases: StartUp,
	 * Reinforcement, Attack and Fortification
	 */
	private void createPlayButtonsAndLabels() {

		int colNumber = MapController.continentsSet.size();
		playerName = new Label(playerList.get(0).getName());
		startUpPhase = new Button("StartUp Phase");
		startUpPhase.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				startUpPhaseAction(event);

			}
		});
		reinforcementPhase = new Button("Reinforcement Phase");
		attackPhase = new Button("Atatck Phase");
		fortificationPhase = new Button("Fortification Phase");

		gamePane.setConstraints(playerName, colNumber, 3);
		gamePane.setConstraints(startUpPhase, colNumber, 4);
		gamePane.setConstraints(reinforcementPhase, colNumber, 5);
		gamePane.setConstraints(attackPhase, colNumber, 6);
		gamePane.setConstraints(fortificationPhase, colNumber, 7);

		reinforcementPhase.setVisible(false);
		attackPhase.setVisible(false);
		fortificationPhase.setVisible(false);

		gamePane.getChildren().addAll(playerName, startUpPhase, reinforcementPhase, attackPhase, fortificationPhase);

	}

	/**
	 * This method is used to form the UI of the StartUpPhase for the player who's
	 * turn it is to place army during startup phase
	 * 
	 * @param playerInFocus:
	 *            This is the player whose turn it is to place army on one of his
	 *            territory
	 */
	private void createStartUpPhase(Player playerInFocus) {
		cleanUp();
		int rowIndex = maxNumberOfTerritories + 3;
		playerTerritories = new ListView<>();
		numberOfArmiesPerTerritory = new TextField();
		endTurn = new Button("End Turn");
		// showNumberOfArmies = new TextField();
		showNumberOfArmies = new Label();

		playerName.setText(playerInFocus.getName());
		playerTerritories.getItems().addAll(playerInFocus.getTerritories());
		numberOfArmiesPerTerritory.setPromptText("Enter Army Value");
		// showNumberOfArmies.setEditable(false);

		showNumberOfArmies.setText(String.valueOf(playerInFocus.getArmyCount()));

		gamePane.setConstraints(playerTerritories, 0, rowIndex);
		gamePane.setConstraints(numberOfArmiesPerTerritory, 1, rowIndex);
		gamePane.setConstraints(showNumberOfArmies, 2, rowIndex);
		gamePane.setConstraints(endTurn, 3, rowIndex);
		endTurn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				// TODO Auto-generated method stub
				endTurnForStartUpPhase(e);
			}
		});

		gamePane.getChildren().addAll(playerTerritories, numberOfArmiesPerTerritory, showNumberOfArmies, endTurn);
		startUpPhase.setVisible(false);
	}

	/**
	 * This method provides the action for the StartupButton
	 * 
	 * @param e:
	 *            event when the user clicks the StartUp Button
	 */
	private void startUpPhaseAction(ActionEvent e) {

		Player playerInFocus = playerList.get(playerCounter);
		createStartUpPhase(playerInFocus);
	}

	private void endTurnForStartUpPhase(ActionEvent e) {
		// TODO

		Player playerInFocus = playerList.get(playerCounter);
		String error = new String();
		Territory obj = playerTerritories.getSelectionModel().getSelectedItem();
		int numberOfArmiesInput = 0;
		try {
			numberOfArmiesInput = Integer.parseInt(numberOfArmiesPerTerritory.getText());
			if(playersWithZeroArmies.size()!=playerList.size() && numberOfArmiesInput==0) {
				if (playerCounter < playerList.size() - 1)
					playerCounter++;
				else
					playerCounter = 0;
				createStartUpPhase(playerList.get(playerCounter));
				return;
			}
			if (numberOfArmiesInput > playerInFocus.getArmyCount()) {
				error	= "Number of armies cannot be more than what owner owns";	
				showError(error);
				return;
			}else if(numberOfArmiesInput<1){
				error	=	"Number of Armies cannot be less than 1";
				showError(error);
				return;
			}

		} catch (NumberFormatException exception) {
			error = "Please, Enter a valid number";
			showError(error);
			return;

		}
		if (obj == null) {
			error = "Please select at least one territory";
			showError(error);
			return;

		} else {

			obj.addArmyOfTheTerritory(numberOfArmiesInput);
			TextField tf	=	idTotextFieldMapping.get(obj.getName());
			tf.setPromptText(obj.getName() + " : " + String.valueOf(obj.getArmyOfTheTerritory()));
			
		}
		boolean answer = validatePlayerArmyNumber(playerInFocus);
		if (answer)
			playerInFocus.setArmyCount(
					playerInFocus.getArmyCount() - Integer.parseInt(numberOfArmiesPerTerritory.getText()));
		if (playerCounter < playerList.size() - 1)
			playerCounter++;
		else
			playerCounter = 0;

		if (playerInFocus.getArmyCount() == 0) {
			playersWithZeroArmies.add(playerInFocus);
			if (playersWithZeroArmies.size() == playerList.size()) {
				showNumberOfArmies.setText(String.valueOf(playerInFocus.getArmyCount()));
				List<String> errorList = new ArrayList<>();
				validateNumberOfArmiesInTerritory(errorList);
				if (errorList.size() == 0) {
					cleanUp();
					reinforcementPhase.setVisible(true);
				} else {
					String errors = "Resolve below errors:";
					for (String OneError : errorList)
						errors = errors.concat("\n-" + OneError);
					showError(errors);
					resetArmyCount();
					createStartUpPhase(playerList.get(playerCounter));
				}
			} else {
				createStartUpPhase(playerList.get(playerCounter));
			}
		} else {
			createStartUpPhase(playerList.get(playerCounter));
		}
	}

	/**
	 * This method is used to get the number of armies according to the number of
	 * players
	 * 
	 * @param playerCount:
	 *            Number of player playing the game
	 * @return
	 */
	private int getArmyCount(int playerCount) {

		switch (playerCount) {

		case 2:
			return 40;
		case 3:
			return 35;
		case 4:
			return 30;
		case 5:
			return 25;
		case 6:
			return 20;
		default:
			return 15;

		}
	}

	/**
	 * This function validates if the player in focus has valid number of armies as
	 * in if a player has 0 armies on his turn he won't be able to place any army
	 * 
	 * @param playerInFocus:
	 *            Player whose turn it is to place army on his territories
	 * @return
	 */
	private boolean validatePlayerArmyNumber(Player playerInFocus) {

		if (playerInFocus.getArmyCount() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * This method tells if all the territories have at least one army.
	 * 
	 * @param errorList:
	 *            this list will have name of all the territories that have 0 army.
	 */
	private void validateNumberOfArmiesInTerritory(List<String> errorList) {

		Iterator<Territory> iteTerritory = MapController.territoriesSet.iterator();
		String error = new String();
		while (iteTerritory.hasNext()) {
			Territory obj = iteTerritory.next();
			if (obj.getArmyOfTheTerritory() == 0) {
				error = obj.getName() + "does not have any army and it belongs to " + obj.getOwner().getName();
				errorList.add(error);
			}
		}
	}

	/**
	 * This method is used for cleaning up the startUp phase area on UI once the
	 * player turn is up.
	 */
	private void cleanUp() {

		gamePane.getChildren().removeAll(playerTerritories, showNumberOfArmies, numberOfArmiesPerTerritory, endTurn);
	}

	/**
	 * This is a helper method is used to show am alert to user informing about
	 * various validation errors.
	 * 
	 * @param error:
	 *            Error to show to user.
	 */
	private void showError(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.showAndWait();
	}

	/**
	 * This method is used to reset the army count in case by the end of the turn
	 * there are some territories with 0 armies.
	 */
	private void resetArmyCount() {
		Iterator<Territory> iteTerritory = MapController.territoriesSet.iterator();

		while (iteTerritory.hasNext()) {
			Territory obj = iteTerritory.next();
			obj.setArmyOfTheTerritory(0);
		}
		int armyCount = getArmyCount(playerList.size());
		for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).setArmyCount(armyCount);
		}
		playerCounter = 0;
		playersWithZeroArmies = new HashSet<>();
	}

}
