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

	private static Map<String, TextField> idToTextFieldMapping = new HashMap<>();
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";
	public static final String CONTROL_VALUE = "Control Value";
	public static final String TEXTFIELD_BORDER_COLOUR = "-fx-text-box-border: red;";
	private static GameService serviceObject = new GameService();
	private static List<Player> playerList = new ArrayList<>();
	private static Set<Player> playersWithZeroArmies = new HashSet<>();
	private static Player currentPlayer;
	private TextInputDialog playerDialog;
	private static int totalNumberOfPlayers;
	private static boolean ifStartUpIsComepleted = false;

	
	/**
	 * This method forms the game map on UI, distributes territories randomly to the
	 * players, select the player randomly to start the startup phase of the game.
	 */
	public void startGame() {
		disableComponents(attackPhaseUI);
		disableComponents(fortiPhaseUI);
		disableComponents(reinfoPhaseUI);
		playerDialog = new TextInputDialog();
		playerDialog.setTitle("Enter Number Of Players");
		playerDialog.setContentText("Enter Number Of Players");
		Optional<String> result = playerDialog.showAndWait();

		if (result.isPresent()) {
			totalNumberOfPlayers = Integer.parseInt(result.get());
			playerList = new ArrayList<>(totalNumberOfPlayers);
			serviceObject.setTerritoriesAndArmiesToPlayers(playerList, totalNumberOfPlayers);
			formMap();
			currentPlayer = serviceObject.getPlayer(null, playerList);
			enableComponents(reinfoPhaseUI);
			startUpPhase();
		}
	}

	/**
	 * This method begins the startUp phase.
	 */
	private void startUpPhase() {
		if (endOfStartUpPhase()) {
			String error = "Start Up Phase Completed";
			showError(error);
			ifStartUpIsComepleted = true;
			currentPlayer = serviceObject.getPlayer(null, playerList);
			serviceObject.addArmiesForReinforcementPhase(currentPlayer);
			reinforcementPhase();
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
	private void formMap() {

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
			setTerritoryFields(territoryList, colCounter, true);
			colCounter++;
		}
		mapPane.setContent(mapGrid);
	}

	/**
	 * This method set the text fields on the basis of territories.This is also
	 * adding the Mouse Click event on the text fields to show their
	 * NeighbouringTerritories by clicking the TextField twice and highlighting it's
	 * Neighbour's border by red.
	 * 
	 * 
	 * @param territoryList
	 *            : This is the territory list of a continent.
	 * @param colCounter:
	 *            This is the column in which text fields are to be place according
	 *            to the continent.
	 * @param ifSetUp:
	 *            This tells if this is being called while setting up the UI or not.
	 */
	private void setTerritoryFields(List<Territory> territoryList, int colCounter, boolean ifSetUp) {

		if (ifSetUp) {
			for (int i = 0; i < territoryList.size(); i++) {
				Territory territoryObj = territoryList.get(i);
				TextField territoryField = new TextField();
				GridPane.setConstraints(territoryField, colCounter, i + 3);
				territoryField.setPromptText(territoryObj.getName());
				territoryField.setEditable(false);
				territoryField.setPromptText(
						territoryObj.getName() + " : " + String.valueOf(territoryObj.getArmyOfTheTerritory()));
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

				idToTextFieldMapping.put(territoryObj.getName(), territoryField);
				mapGrid.getChildren().add(territoryField);
			}
		} else {
			// To add the generic addition, why? can't we use idToTextFieldMapping for all
			// the updates??
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
			TextField tf = idToTextFieldMapping.get(t.getName());
			if (clickCount == 2) {
				tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR);
			} else {
				tf.setStyle("");
			}
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

		try {
			serviceObject.validateArmyInput(armyInput, currentPlayer, selectedTerritory);
		} catch (Exception exception) {
			showError(exception.getMessage());
			return;
		}
		int numberOfArmiesInput = Integer.parseInt(armyInput);
		selectedTerritory.addArmyOfTheTerritory(numberOfArmiesInput);
		TextField tf = idToTextFieldMapping.get(selectedTerritory.getName());
		tf.setPromptText(
				selectedTerritory.getName() + " : " + String.valueOf(selectedTerritory.getArmyOfTheTerritory()));

		boolean answer = serviceObject.validatePlayerArmyNumber(currentPlayer);
		if (answer)
			currentPlayer.setArmyCount(
					currentPlayer.getArmyCount() - Integer.parseInt(numberOfArmiesPerTerritory.getText()));

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
			mapTF.setPromptText(
					attackerTerritory.getName() + " : " + String.valueOf(attackerTerritory.getArmyOfTheTerritory()));
			mapTF = idToTextFieldMapping.get(defenderTerritory.getName());
			mapTF.setPromptText(
					defenderTerritory.getName() + " : " + String.valueOf(defenderTerritory.getArmyOfTheTerritory()));

			if (currentPlayer.getTerritories().size() == MapController.territoriesSet.size()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Player " + currentPlayer.getName() + " won the game.");
				alert.showAndWait();
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
			mapTF.setPromptText(from.getName() + " : " + String.valueOf(from.getArmyOfTheTerritory()));
			mapTF = idToTextFieldMapping.get(to.getName());
			mapTF.setPromptText(to.getName() + " : " + String.valueOf(to.getArmyOfTheTerritory()));

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

}
