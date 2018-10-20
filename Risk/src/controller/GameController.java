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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import service.GameService;

/**
 * This controller handles Game.fxml controls and implements game driver.
 * 
 * @author Yogesh
 *
 */
public class GameController {

	/**
	 * Variable for reference for GridPane in Game.fxml.
	 */
	@FXML
	private GridPane mapGrid;

	/**
	 * Variable for reference for ScrollPane for map in Game.fxml.
	 */
	@FXML
	private ScrollPane mapPane;

	/**
	 * Variable for reference for TextField to get armies number to place in a
	 * territory.
	 */
	@FXML
	private TextField numberOfArmiesPerTerritory;

	/**
	 * Variable for reference for Label to show armies which place have to place.
	 */
	@FXML
	private Label playerArmies;

	/**
	 * Variable for reference for Label to show player name on Game UI.
	 */
	@FXML
	private Label playerDetailsUI;

	/**
	 * Variable for reference for Label to show StartUp/Reinforcement phase section.
	 */
	@FXML
	private Label startUpAndReinforcementId;

	/**
	 * Variable for reference for ComboBox to hold player's territories in startUp
	 * and reinforcement phase.
	 */
	@FXML
	private ComboBox<Territory> playerTerritoryList;

	/**
	 * Variable for reference for reinforcement phase UI pane.
	 */
	@FXML
	private AnchorPane reinfoPhaseUI;

	/**
	 * Variable for reference for attack phase UI pane.
	 */
	@FXML
	private AnchorPane attackPhaseUI;

	/**
	 * Variable for reference for ComboBox to hold player's territories to attack
	 * from.
	 */
	@FXML
	private ComboBox<Territory> attackAttackerCB;

	/**
	 * Variable for reference for ComboBox to hold territories on which player can
	 * attack.
	 */
	@FXML
	private ComboBox<Territory> attackDefenderCB;

	/**
	 * Variable for reference for fortification phase UI pane.
	 */
	@FXML
	private AnchorPane fortiPhaseUI;

	/**
	 * Variable for reference for ComboBox where fortification from territories.
	 */
	@FXML
	private ComboBox<Territory> fortifyFromTerritoryCB;

	/**
	 * Variable for reference for ComboBox where fortification to territories.
	 */
	@FXML
	private ComboBox<Territory> fortifyToTerritoryCB;

	/**
	 * Variable for reference for TextField to get user input for number of armies
	 * to move in fortification phase.
	 */
	@FXML
	private TextField armiesNoToFortify;

	/**
	 * Game constant for CONTROL_VALUE_WITH_SEMICOLON string.
	 */
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";

	/**
	 * Game constant for CONTROL_VALUE string.
	 */
	private static final String CONTROL_VALUE = "Control Value";

	/**
	 * Game constant for TEXTFIELD_BORDER_COLOUR_DEFENDER_TERRITORY string.
	 */
	private static final String TEXTFIELD_BORDER_COLOUR_DEFENDER_TERRITORY = "-fx-text-box-border: red;";

	/**
	 * Game constant for TEXTFIELD_BORDER_COLOUR_OWN_TERRITORY string.
	 */
	private static final String TEXTFIELD_BORDER_COLOUR_OWN_TERRITORY = "-fx-text-box-border: darkgreen;";

	/**
	 * Map to hold mapping for territory objects and their corresponding textfields.
	 */
	private static Map<String, TextField> territoriesToTFMapping = new HashMap<>();

	/**
	 * Map to hold mapping for player objects and their corresponding labels.
	 */
	private static Map<String, Label> playersToStatLabelMapping = new HashMap<>();

	/**
	 * Game service object to call methods on.
	 */
	private static GameService gameService = new GameService();

	/**
	 * List holding data of all the players who are playing this game.
	 */
	private static List<Player> playersList = new ArrayList<>();

	/**
	 * Set for players who have no armies left to place in startup phase.
	 */
	private static Set<Player> playersWithZeroArmies = new HashSet<>();

	/**
	 * It represent current player who is having turn.
	 */
	private static Player currentPlayer;

	/**
	 * Count of total number of players.
	 */
	private static int playersCount;

	/**
	 * Flag to represent whether startup phase is complete or not.
	 */
	private static boolean ifStartUpIsComepleted = false;

	/**
	 * Variable to hold value for maximum number of territories in any continent.
	 */
	private int maxNumberOfTerritores = Integer.MIN_VALUE;

	/**
	 * This method forms the game map on UI, distributes territories randomly to the
	 * players, select the player randomly to start the startup phase of the game.
	 */
	public void startGame() {
		displayMap();
		disableComponents(attackPhaseUI);
		disableComponents(fortiPhaseUI);
		disableComponents(reinfoPhaseUI);
		while (getPlayersCount() == -1)
			;
		playersList = new ArrayList<>(playersCount);
		gameService.createPlayers(playersList, playersCount);
		gameService.assignTerritories(playersList);
		updateMapData();
		displayPlayerStats();
		currentPlayer = gameService.getNextPlayer(null, playersList);
		enableComponents(reinfoPhaseUI);
		startUpPhase();
	}

	/**
	 * This method handle {@link GameController#addArmies} button event. It adds
	 * number of armies entered by player to the territory of that player.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void addReinforcement(ActionEvent event) {
		Territory selectedTerritory = playerTerritoryList.getValue();
		String armyInput = numberOfArmiesPerTerritory.getText();
		List<String> errorList = new ArrayList<>();

		gameService.validateArmyInput(armyInput, currentPlayer, selectedTerritory, errorList);
		if (errorList.size() != 0) {
			String errors = "Resolve below errors:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
			return;
		}
		int numberOfArmies = Integer.parseInt(armyInput);
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + numberOfArmies);
		updateTerritoryFields(selectedTerritory);
		currentPlayer
				.setArmyCount(currentPlayer.getArmyCount() - Integer.parseInt(numberOfArmiesPerTerritory.getText()));

		if (!ifStartUpIsComepleted) {
			if (currentPlayer.getArmyCount() == 0) {
				playersWithZeroArmies.add(currentPlayer);
			}
			currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
			startUpPhase();
		} else {
			reinforcementPhase();
		}
	}

	/**
	 * This method handles {@link GameController#attackButton} button event. It
	 * perform attack from selected territory to another territory selected by user.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void attack(ActionEvent event) {
		Territory attackerTerritory = attackAttackerCB.getValue();
		Territory defenderTerritory = attackDefenderCB.getValue();

		if (attackerTerritory.getArmyCount() <= 1)
			showError("Can not attack with one Army.");
		else if (defenderTerritory == null) {
			showError("Select a territory to attack to. ");
		} else {
			gameService.attack(attackerTerritory, defenderTerritory);
			updatePlayersStats();
			updateTerritoryFields(attackerTerritory);
			updateTerritoryFields(defenderTerritory);

			if (currentPlayer.getTerritories().size() == MapController.territoriesSet.size()) {
				showInformation("Player " + currentPlayer.getName() + " won the game.");
				Platform.exit();
			} else {
				attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
				attackAttackerCB.setValue(attackAttackerCB.getItems().get(0));

				List<Territory> defenderTerritories = gameService
						.getAttackableTerritories(currentPlayer.getTerritories().get(0));
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

	/**
	 * This method handles {@link GameController#attackAttackerCB} comboBox value
	 * change event to update {@link GameController#attackDefenderCB} comboBox
	 * values.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void updateDefenderTerritories(ActionEvent event) {
		Territory selectedTerritory = attackAttackerCB.getValue();

		if (selectedTerritory == null)
			return;
		List<Territory> defenderTerritories = gameService.getAttackableTerritories(selectedTerritory);
		if (defenderTerritories.size() > 0) {
			attackDefenderCB.setDisable(false);
			attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
			attackDefenderCB.setValue(defenderTerritories.get(0));
		} else {
			attackDefenderCB.setValue(null);
			attackDefenderCB.setDisable(true);
		}
	}

	/**
	 * This method handles {@link GameController#attackFinishButton} button event.
	 * It finish current player's attack phase and setup fortification UI for
	 * current player.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void finishAttack(ActionEvent event) {
		disableComponents(attackPhaseUI);
		enableComponents(fortiPhaseUI);

		fortifyFromTerritoryCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
		fortifyFromTerritoryCB.setValue(currentPlayer.getTerritories().get(0));
		List<Territory> fortifiableTerritories = gameService
				.getFortifiableTerritories(currentPlayer.getTerritories().get(0));
		if (fortifiableTerritories.size() > 0) {
			fortifyToTerritoryCB.setItems(FXCollections.observableList(fortifiableTerritories));
			fortifyToTerritoryCB.setValue(fortifiableTerritories.get(0));
			fortifyToTerritoryCB.setDisable(false);
		} else {
			fortifyToTerritoryCB.setValue(null);
			fortifyToTerritoryCB.setDisable(true);
		}
	}

	/**
	 * This method handles {@link GameController#fortifyMoveButton} button event. It
	 * moves armies from one territory to another based on how many armies user want
	 * to move.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
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
		gameService.fortify(from, to, armiesToMove, errorList);
		if (errorList.size() > 0) {
			String errors = "Cannot fortify due to:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
		} else {

			updateTerritoryFields(from);
			updateTerritoryFields(to);
			finishFortification(event);
		}
	}

	/**
	 * This method handles {@link GameController#fortifyFromTerritoryCB} comboBox
	 * value change event to update {@link GameController#fortifyToTerritoryCB}
	 * comboBox values.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void updateFortifiableTerritories(ActionEvent event) {
		Territory selectedTerritory = fortifyFromTerritoryCB.getValue();

		if (selectedTerritory == null)
			return;
		List<Territory> fortifiableTerritories = gameService.getFortifiableTerritories(selectedTerritory);
		if (fortifiableTerritories.size() > 0) {
			fortifyToTerritoryCB.setItems(FXCollections.observableList(fortifiableTerritories));
			fortifyToTerritoryCB.setValue(fortifiableTerritories.get(0));
		} else {
			fortifyToTerritoryCB.setValue(null);
			fortifyToTerritoryCB.setDisable(true);
		}
	}

	/**
	 * This method handles {@link GameController#fortifyFinishButton} button event.
	 * It finish current player's fortification phase and setup reinforcement phase
	 * UI for next player turn.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void finishFortification(ActionEvent event) {
		disableComponents(fortiPhaseUI);
		enableComponents(reinfoPhaseUI);
		currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);

		while (currentPlayer.getTerritories().size() == 0)
			currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);

		gameService.calcArmiesForReinforcement(currentPlayer);
		displayPlayerInfo();
	}

	/**
	 * This method begins the startUp phase.
	 */
	private void startUpPhase() {
		if (endOfStartUpPhase()) {
			String state = "Start Up Phase Completed";
			showInformation(state);
			ifStartUpIsComepleted = true;
			currentPlayer = gameService.getNextPlayer(null, playersList);
			gameService.calcArmiesForReinforcement(currentPlayer);
			reinforcementPhase();
		} else if (currentPlayer.getArmyCount() == 0) {
			currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
			startUpPhase();
		}
		displayPlayerInfo();
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
		displayPlayerInfo();
	}

	/**
	 * This method display map in grid form on UI.
	 */
	private void displayMap() {
		Iterator<Continent> ite = MapController.continentsSet.iterator();
		int colCounter = 0;
		ColumnConstraints widthCol = new ColumnConstraints();
		widthCol.setHgrow(Priority.ALWAYS);
		while (ite.hasNext()) {
			mapGrid.getColumnConstraints().add(widthCol);
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
				territoryField.setMinWidth(150);
				territoryField.setOnMouseEntered(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						highlightNeighbours(territoryObj, true);
					}
				});
				territoryField.setOnMouseExited(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						highlightNeighbours(territoryObj, false);
					}
				});
				territoriesToTFMapping.put(territoryObj.getName(), territoryField);
				mapGrid.getChildren().add(territoryField);

			}
			colCounter++;
		}
		mapPane.setContent(mapGrid);
	}

	/**
	 * This method is responsible to get number of player playing this game from
	 * user.
	 * 
	 * @return int: number of players entered by user.
	 */
	private int getPlayersCount() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Number Of Players");
		dialog.setContentText("Enter Number Of Players");
		Optional<String> result = dialog.showAndWait();
		String error;
		if (result.isPresent()) {
			try {
				playersCount = Integer.parseInt(result.get());
				if (playersCount < 1) {
					error = "Number of Players cannot be less than 2";
					showError(error);
					return -1;
				}
			} catch (NumberFormatException e) {
				error = "Enter a valid Number";
				showError(error);
				return -1;
			}
			return playersCount;
		} else {
			error = "Enter a valid Number";
			return -1;
		}
	}

	/**
	 * This method is responsible for displaying player's status at bottom left side
	 * of game UI.
	 */
	private void displayPlayerStats() {
		Label statLabel = new Label("Game Stats");
		statLabel.setMinWidth(150);;
		statLabel.setUnderline(true);
		GridPane.setConstraints(statLabel, 0, maxNumberOfTerritores + 4);
		mapGrid.getChildren().addAll(statLabel);
		maxNumberOfTerritores+=2;
		
		Label playerName = new Label("Player Name");
		Label totalNumberOfTerritories = new Label("Territory Count");
		GridPane.setConstraints(playerName, 0, maxNumberOfTerritores + 4);
		GridPane.setConstraints(totalNumberOfTerritories, 1, maxNumberOfTerritores + 4);
		mapGrid.getChildren().addAll(playerName, totalNumberOfTerritories);
		for (int i = 0; i < playersList.size(); i++) {
			playerName = new Label(playersList.get(i).getName());
			totalNumberOfTerritories = new Label(String.valueOf(playersList.get(i).getTerritories().size()));
			playersToStatLabelMapping.put(playersList.get(i).getName(), totalNumberOfTerritories);
			GridPane.setConstraints(playerName, 0, maxNumberOfTerritores + i + 5);
			GridPane.setConstraints(totalNumberOfTerritories, 1, maxNumberOfTerritores + i + 5);
			mapGrid.getChildren().addAll(playerName, totalNumberOfTerritories);
		}
		mapPane.setContent(mapGrid);
	}

	/**
	 * This method is responsible for updating player's status at bottom left side
	 * of game UI.
	 */
	private void updatePlayersStats() {
		for (Player player : playersList) {
			playersToStatLabelMapping.get(player.getName()).setText(String.valueOf(player.getTerritories().size()));
		}
	}

	/**
	 * This method populate the current player information on UI.
	 */
	private void displayPlayerInfo() {
		playerDetailsUI.setText(currentPlayer.getName());
		playerArmies.setText(String.valueOf(currentPlayer.getArmyCount()));
		playerTerritoryList.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
		playerTerritoryList.setValue(currentPlayer.getTerritories().get(0));
		if (ifStartUpIsComepleted) {
			startUpAndReinforcementId.setText("Reinforcement Phase");
		}

	}

	/**
	 * This function is being used to help in highlighting the Neighboring
	 * territories depending on the number of clicks on the TextBox. If the TextBox
	 * is clicked twice it highlight the neighbouring territories' border by red and
	 * convert it back to the original colour if clicked otherwise
	 * 
	 * @param territoryObj:
	 *            This is the the territory on which mouse entered or exited..
	 * @param isEntered:
	 *            true if mouse is entered else false.
	 */
	private void highlightNeighbours(Territory territoryObj, boolean isEntered) {

		List<Territory> neighbouringCountries = territoryObj.getNeighbourTerritories();

		for (int i = 0; i < neighbouringCountries.size(); i++) {
			Territory terr = neighbouringCountries.get(i);
			TextField tf = territoriesToTFMapping.get(terr.getName());
			if (isEntered) {
				if (terr.getOwner() != territoryObj.getOwner()) {
					tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR_DEFENDER_TERRITORY);
				} else {
					tf.setStyle(GameController.TEXTFIELD_BORDER_COLOUR_OWN_TERRITORY);
				}
			} else
				tf.setStyle("");
		}

	}

	/**
	 * This method defines the condition to verify if startUpPhase has ended or not.
	 * 
	 * @return boolean: true if startUp phase is finished else false.
	 */
	private boolean endOfStartUpPhase() {
		if (playersWithZeroArmies.size() == playersList.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method defines the condition to verify if the Reinforcement phase has
	 * ended or not.
	 * 
	 * @param playerInFocus
	 *            : Player who is doing the reinforcement currently.
	 * @return boolean: true if reinforcement phase is finished for current player
	 *         else false;
	 */
	private boolean endOfReinforcementPhase(Player playerInFocus) {
		if (playerInFocus.getArmyCount() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method update the army values in each territory for whole map.
	 */
	private void updateMapData() {
		for (Player player : playersList) {
			for (Territory territory : player.getTerritories()) {
				updateTerritoryFields(territory);
			}
		}
	}

	/**
	 * This method update the army values for selected territory.
	 * 
	 * @param terrObj:
	 *            territory object for which values needs to be updated on UI.
	 */
	private void updateTerritoryFields(Territory terrObj) {
		String playerName = "";
		if (terrObj.getOwner() != null)
			playerName = terrObj.getOwner().getName();
		TextField territoryField = territoriesToTFMapping.get(terrObj.getName());
		territoryField.setText(terrObj.getName() + " : " + String.valueOf(terrObj.getArmyCount()) + " - ("+playerName+")");

	}

	/**
	 * This method will disable all the components on the provided pane.
	 * 
	 * @param pane:
	 *            pane on which components needs to be disabled.
	 */
	private void disableComponents(Pane pane) {
		List<Node> nodes = pane.getChildren();
		for (Node n : nodes) {
			n.setDisable(true);
		}
	}

	/**
	 * This method will enable all the components on the provided pane.
	 * 
	 * @param pane:
	 *            pane on which components needs to be enabled.
	 */
	private void enableComponents(Pane pane) {
		List<Node> nodes = pane.getChildren();
		for (Node n : nodes) {
			n.setDisable(false);
		}
	}

	/**
	 * This method is used to show am alert to user informing about various
	 * validation errors.
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
	 * This method is used to show information to user about various events.
	 * 
	 * @param state:
	 *            state to show to user.
	 */
	private void showInformation(String state) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Information");
		alert.setHeaderText(null);
		alert.setContentText(state);
		alert.showAndWait();
	}

}
