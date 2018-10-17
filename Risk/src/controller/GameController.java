package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import domain.Continent;
import domain.Player;
import domain.Territory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import service.GameService;

/**
 * This controller handles Game.fxml controls and implements game driver.
 * 
 * @author Yogesh
 *
 */
public class GameController {

	@FXML
	private GridPane mapGrid;

	@FXML
	private ScrollPane mapPane;

	@FXML
	private Label numberOfPlayers;

	@FXML
	private TextField countOfPlayers;

	@FXML
	private Button renderGame;

	@FXML
	private Label playerName;

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
	private Label playerArmies;

	@FXML
	private Label playerDetailsUI;

	@FXML
	private Label startUpAndReinforcementId;

	@FXML
	private ComboBox<Territory> playerTerritoryList;

	@FXML
	private Button addArmies;

	@FXML
	private AnchorPane reinfoPhaseUI;

	@FXML
	private AnchorPane attackPhaseUI;

	@FXML
	private ComboBox<Territory> attackAttackerCB;

	@FXML
	private ComboBox<Territory> attackDefenderCB;

	@FXML
	private Button attackButton;

	@FXML
	private Button attackFinishButton;

	@FXML
	private AnchorPane fortiPhaseUI;

	@FXML
	private ComboBox<Territory> fortifyFromTerritoryCB;

	@FXML
	private ComboBox<Territory> fortifyToTerritoryCB;

	@FXML
	private Button fortifyMoveButton;

	@FXML
	private Button fortifyFinishButton;

	@FXML
	private TextField armiesNoToFortify;

	public static Map<String, TextField> idToTextFieldMapping = new HashMap<>();
	public static Map<String, Label> idToLabelMapping = new HashMap<>();
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";
	public static final String CONTROL_VALUE = "Control Value";
	public static final String TEXTFIELD_BORDER_COLOUR_DEFENDER_TERRITORY = "-fx-text-box-border: red;";
	public static final String TEXTFIELD_BORDER_COLOUR_OWN_TERRITORY = "-fx-text-box-border: darkgreen;";
	private static GameService serviceObject = new GameService();
	private static List<Player> playerList = new ArrayList<>();
	private static Set<Player> playersWithZeroArmies = new HashSet<>();
	private static Player currentPlayer;
	private TextInputDialog playerDialog;
	private static int totalNumberOfPlayers;
	private static boolean ifStartUpIsComepleted = false;
	private int maxNumberOfTerritores = Integer.MIN_VALUE;

	/**
	 * This method forms the game map on UI, distributes territories randomly to the
	 * players, select the player randomly to start the startup phase of the game.
	 */
	public void startGame() {
		formMap();
		disableComponents(attackPhaseUI);
		disableComponents(fortiPhaseUI);
		disableComponents(reinfoPhaseUI);
		playerDialog = new TextInputDialog();
		playerDialog.setTitle("Enter Number Of Players");
		playerDialog.setContentText("Enter Number Of Players");
		while (getplayerNumber() == -1)
			;
		playerList = new ArrayList<>(totalNumberOfPlayers);
		serviceObject.setTerritoriesAndArmiesToPlayers(playerList, totalNumberOfPlayers);
		createPlayerToNumberOfTerritoryMapping();
		currentPlayer = serviceObject.getPlayer(null, playerList);
		enableComponents(reinfoPhaseUI);
		startUpPhase();

	}

	private int getplayerNumber() {

		Optional<String> result = playerDialog.showAndWait();
		String error;
		if (result.isPresent()) {
			try {
				totalNumberOfPlayers = Integer.parseInt(result.get());
				if (totalNumberOfPlayers < 1) {
					error = "Number of Players cannot be less than 2";
					showError(error);
					return -1;
				}
			} catch (NumberFormatException e) {
				error = "Enter a valid Number";
				showError(error);
				return -1;
			}
			return totalNumberOfPlayers;
		} else {
			error = "Enter a valid Number";
			return -1;
		}
	}

	/**
	 * This method begins the startUp phase.
	 */
	private void startUpPhase() {
		if (endOfStartUpPhase()) {
			String state = "Start Up Phase Completed";
			showGameState(state);
			ifStartUpIsComepleted = true;
			currentPlayer = serviceObject.getPlayer(null, playerList);
			serviceObject.addArmiesForReinforcementPhase(currentPlayer);
			reinforcementPhase();
		} else if (currentPlayer.getArmyCount() == 0) {
			currentPlayer = serviceObject.getPlayer(currentPlayer, playerList);
			startUpPhase();
		}
		setPlayerInfo();
	}

	/**
	 * This method populate the current player information on UI.
	 */
	public void setPlayerInfo() {
		playerDetailsUI.setText(currentPlayer.getName());
		playerArmies.setText(String.valueOf(currentPlayer.getArmyCount()));
		playerTerritoryList.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
		playerTerritoryList.setValue(currentPlayer.getTerritories().get(0));
		if (ifStartUpIsComepleted) {
			startUpAndReinforcementId.setText("Reinforcement Phase");
		}

	}

	/**
	 * This function is used to generate TextBoxes and Labels denoting Territories
	 * and Continents of the game respectively.
	 * 
	 */
	@FXML
	public void formMap() {

		Iterator<Continent> ite = MapController.continentsSet.iterator();
		int colCounter = 0;
		while (ite.hasNext()) {
			Continent obj = ite.next();
			String nameofTheContinent = obj.getName();
			List<Territory> territoryList = obj.getTerritories();
			Integer controlValue = obj.getContinentArmyValue();
			Label continentName = new Label(nameofTheContinent);
			Label controlValueLabel = new Label(GameController.CONTROL_VALUE_WITH_SEMICOLON + controlValue.toString());
			GridPane.setConstraints(continentName, colCounter, 1);
			GridPane.setConstraints(controlValueLabel, colCounter, 2);
			mapGrid.getChildren().addAll(continentName, controlValueLabel);
			if (maxNumberOfTerritores < territoryList.size()) {
				maxNumberOfTerritores = territoryList.size();
			}
			for (int i = 0; i < territoryList.size(); i++) {
				Territory territoryObj = territoryList.get(i);
				TextField territoryField = new TextField();
				GridPane.setConstraints(territoryField, colCounter, i + 3);
				territoryField.setPromptText(territoryObj.getName());
				territoryField.setEditable(false);
				territoryField.setOnMouseEntered(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub
						highlightNeighbouringTerritoriesHelper(territoryObj, true);
					}
				});
				territoryField.setOnMouseExited(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub
						highlightNeighbouringTerritoriesHelper(territoryObj, false);
					}
				});
				idToTextFieldMapping.put(territoryObj.getName(), territoryField);
				mapGrid.getChildren().add(territoryField);

			}
			colCounter++;
		}
		mapPane.setContent(mapGrid);
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
	private void highlightNeighbouringTerritoriesHelper(Territory territoryObj, boolean isEntered) {
		
		List<Territory> neighbouringCountries	=	territoryObj.getNeighbourTerritories();
		
		for (int i = 0; i < neighbouringCountries.size(); i++) {
			Territory terr = neighbouringCountries.get(i);
			TextField tf = idToTextFieldMapping.get(terr.getName());
			if (isEntered) {
				if (terr.getOwner()!=territoryObj.getOwner()) {
					tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR_DEFENDER_TERRITORY);
				} else {
					tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR_OWN_TERRITORY);
				}
			} else
				tf.setStyle("");
		}

	}

	/**
	 * This method handle {@link GameController#addArmies} button event. It adds
	 * number of armies entered by player to the territory of that player.
	 * 
	 * @param e:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	private void addArmiesToTerritories(ActionEvent e) {
		// TODO
		Territory selectedTerritory = playerTerritoryList.getValue();
		String armyInput = numberOfArmiesPerTerritory.getText();
		List<String> errorList = new ArrayList<>();

		serviceObject.validateArmyInput(armyInput, currentPlayer, selectedTerritory, errorList);
		if (errorList.size() != 0) {
			String errors = "Resolve below errors:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
			return;
		}
		int numberOfArmiesInput = Integer.parseInt(armyInput);
		selectedTerritory.setArmyOfTheTerritory(selectedTerritory.getArmyOfTheTerritory() + numberOfArmiesInput);
		TextField tf = idToTextFieldMapping.get(selectedTerritory.getName());
		tf.setText(selectedTerritory.getName() + " : " + String.valueOf(selectedTerritory.getArmyOfTheTerritory()));
		currentPlayer
				.setArmyCount(currentPlayer.getArmyCount() - Integer.parseInt(numberOfArmiesPerTerritory.getText()));

		if (!ifStartUpIsComepleted) {
			if (currentPlayer.getArmyCount() == 0) {
				playersWithZeroArmies.add(currentPlayer);
			}
			currentPlayer = serviceObject.getPlayer(currentPlayer, playerList);
			startUpPhase();
		} else {
			reinforcementPhase();
		}
	}

	/**
	 * This method defines the condition to verify if startUpPhase has ended or not.
	 * 
	 * @return
	 */
	private boolean endOfStartUpPhase() {
		if (playersWithZeroArmies.size() == playerList.size()) {
			return true;
		} else {
			return false;
		}
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

	private void showGameState(String state) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Information");
		alert.setHeaderText(null);
		alert.setContentText(state);
		alert.showAndWait();
	}

	/**
	 * This method begins the reinforcement phase.
	 */
	private void reinforcementPhase() {
		if (endOfReinforcementPhase(currentPlayer)) {
			disableComponents(reinfoPhaseUI);
			enableComponents(attackPhaseUI);
			attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
			attackAttackerCB.setValue(currentPlayer.getTerritories().get(0));
		}
		setPlayerInfo();
	}

	@FXML
	void updateDefenderTerritories(ActionEvent event) {
		Territory selectedTerritory = attackAttackerCB.getValue();
		// In case event is occured by setItems() method of comboBox.
		if (selectedTerritory == null)
			return;
		List<Territory> defenderTerritories = serviceObject.attackableTerritories(selectedTerritory);
		if (defenderTerritories.size() > 0) {
			attackDefenderCB.setDisable(false);
			attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
			attackDefenderCB.setValue(defenderTerritories.get(0));
		} else {
			attackDefenderCB.setValue(null);
			attackDefenderCB.setDisable(true);
		}
	}

	@FXML
	public void attack() {
		Territory attackerTerritory = attackAttackerCB.getValue();
		Territory defenderTerritory = attackDefenderCB.getValue();

		if (attackerTerritory.getArmyOfTheTerritory() <= 1)
			showError("Can not attack with one Army.");
		else if (defenderTerritory == null) {
			showError("Select a territory to attack to. ");
		} else {
			serviceObject.attack(attackerTerritory, defenderTerritory);

			TextField mapTF = idToTextFieldMapping.get(attackerTerritory.getName());
			mapTF.setText(
					attackerTerritory.getName() + " : " + String.valueOf(attackerTerritory.getArmyOfTheTerritory()));
			mapTF = idToTextFieldMapping.get(defenderTerritory.getName());
			mapTF.setText(
					defenderTerritory.getName() + " : " + String.valueOf(defenderTerritory.getArmyOfTheTerritory()));

			if (currentPlayer.getTerritories().size() == MapController.territoriesSet.size()) {
				showGameState("Player " + currentPlayer.getName() + " won the game.");
				Platform.exit();
			} else {
				attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
				attackAttackerCB.setValue(attackAttackerCB.getItems().get(0));

				List<Territory> defenderTerritories = serviceObject
						.attackableTerritories(currentPlayer.getTerritories().get(0));
				if (defenderTerritories.size() > 0) {
					attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
					attackDefenderCB.setValue(defenderTerritories.get(0));
				} else {
					attackDefenderCB.setDisable(true);
					attackDefenderCB.setValue(null);
				}
			}
		}
	}

	@FXML
	public void finishAttack(ActionEvent event) {
		disableComponents(attackPhaseUI);
		enableComponents(fortiPhaseUI);

		fortifyFromTerritoryCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
		fortifyFromTerritoryCB.setValue(currentPlayer.getTerritories().get(0));
		List<Territory> fortifiableTerritories = serviceObject
				.fortifiableTerritories(currentPlayer.getTerritories().get(0));
		if (fortifiableTerritories.size() > 0) {
			fortifyToTerritoryCB.setItems(FXCollections.observableList(fortifiableTerritories));
			fortifyToTerritoryCB.setValue(fortifiableTerritories.get(0));
			fortifyToTerritoryCB.setDisable(false);
		} else {
			fortifyToTerritoryCB.setValue(null);
			fortifyToTerritoryCB.setDisable(true);
		}
	}

	@FXML
	public void updatefortifiableTerritories() {
		Territory selectedTerritory = fortifyFromTerritoryCB.getValue();
		// In case event is occured by setItems() method of comboBox.
		if (selectedTerritory == null)
			return;
		List<Territory> fortifiableTerritories = serviceObject.fortifiableTerritories(selectedTerritory);
		if (fortifiableTerritories.size() > 0) {
			fortifyToTerritoryCB.setItems(FXCollections.observableList(fortifiableTerritories));
			fortifyToTerritoryCB.setValue(fortifiableTerritories.get(0));
		} else {
			fortifyToTerritoryCB.setValue(null);
			fortifyToTerritoryCB.setDisable(true);
		}
	}

	@FXML
	public void doFortification(ActionEvent event) {

		List<String> errorList = new ArrayList<>();
		Territory from = fortifyFromTerritoryCB.getValue();
		Territory to = fortifyToTerritoryCB.getValue();
		if (to == null) {
			showError("Select a territory to fortify.");
			return;
		}
		int armiesToMove;
		try {
			armiesToMove = Integer.parseInt(armiesNoToFortify.getText());
		} catch (Exception e) {
			showError("Enter valid number.");
			return;
		}
		serviceObject.fortification(from, to, armiesToMove, errorList);
		if (errorList.size() > 0) {
			String errors = "Cannot fortify due to:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
		} else {

			TextField mapTF = idToTextFieldMapping.get(from.getName());
			mapTF.setText(from.getName() + " : " + String.valueOf(from.getArmyOfTheTerritory()));
			mapTF = idToTextFieldMapping.get(to.getName());
			mapTF.setText(to.getName() + " : " + String.valueOf(to.getArmyOfTheTerritory()));

			finishFortification(event);
		}
	}

	@FXML
	public void finishFortification(ActionEvent event) {
		disableComponents(fortiPhaseUI);
		enableComponents(reinfoPhaseUI);
		currentPlayer = serviceObject.getPlayer(currentPlayer, playerList);

		// to avoid players turn who have no territories
		while (currentPlayer.getTerritories().size() == 0)
			currentPlayer = serviceObject.getPlayer(currentPlayer, playerList);

		serviceObject.addArmiesForReinforcementPhase(currentPlayer);
		setPlayerInfo();
	}

	/**
	 * This method defines the condition to verify if the Reinforcement phase has
	 * ended or not.
	 * 
	 * @param playerInFocus
	 *            : Player who is doing the reinforcement currently.
	 * @return
	 */
	private boolean endOfReinforcementPhase(Player playerInFocus) {
		if (playerInFocus.getArmyCount() == 0) {
			return true;
		} else {
			return false;
		}
	}

	private void disableComponents(Pane pane) {
		List<Node> nodes = pane.getChildren();
		for (Node n : nodes) {
			n.setDisable(true);
		}
	}

	private void enableComponents(Pane pane) {
		List<Node> nodes = pane.getChildren();
		for (Node n : nodes) {
			n.setDisable(false);
		}
	}

	private void createPlayerToNumberOfTerritoryMapping() {
		Label playerName = new Label("Player Name");
		Label totalNumberOfTerritories = new Label("Territory Count");
		GridPane.setConstraints(playerName, 0, maxNumberOfTerritores + 4);
		GridPane.setConstraints(totalNumberOfTerritories, 1, maxNumberOfTerritores + 4);
		mapGrid.getChildren().addAll(playerName, totalNumberOfTerritories);
		for (int i = 0; i < playerList.size(); i++) {
			playerName = new Label(playerList.get(i).getName());
			totalNumberOfTerritories = new Label(String.valueOf(playerList.get(i).getTerritories().size()));
			idToLabelMapping.put(playerList.get(i).getName(), totalNumberOfTerritories);
			GridPane.setConstraints(playerName, 0, maxNumberOfTerritores + i + 5);
			GridPane.setConstraints(totalNumberOfTerritories, 1, maxNumberOfTerritores + i + 5);
			mapGrid.getChildren().addAll(playerName, totalNumberOfTerritories);
		}
		mapPane.setContent(mapGrid);
	}
}
