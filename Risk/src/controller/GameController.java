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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import service.GameService;

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

	private static Map<String, TextField> idToTextFieldMapping = new HashMap<>();
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";
	public static final String CONTROL_VALUE = "Control Value";
	public static final String TEXTFIELD_BORDER_COLOUR = "-fx-text-box-border: red;";
	private static GameService serviceObject = new GameService();
	private static List<Player> playerList = new ArrayList<>();
	private static Set<Player> playersWithZeroArmies = new HashSet<>();
	private static Player currentPlayer;
	private TextInputDialog playerDialog = new TextInputDialog();
	private static int totalNumberOfPlayers;
	private static boolean ifStartUpIsComepleted = false;

	// private final Button okButton =
	// (Button)playerDialog.getDialogPane().lookupButton(ButtonType.OK); Might use
	// it for Validation of Input Text of Dialog box

	/**
	 * This method forms the game map on UI, distributes territories randomly to the
	 * players, select the player randomly to start the startup phase of the game.
	 */
	public void startGame() {
		playerDialog.initModality(Modality.APPLICATION_MODAL);
		playerDialog.initStyle(StageStyle.UNDECORATED);
		playerDialog.setTitle("Enter Number Of Players");
		playerDialog.setContentText("Enter Number Of Players");
		Optional<String> result = playerDialog.showAndWait();

		// Might use it for Validation
		// okButton.addEventFilter(ActionEvent.ACTION, event->{
		// if(result.isPresent() && Integer.parseInt(result.get())>0){
		// event.consume();
		//
		// }
		// });

		if (result.isPresent()) {
			totalNumberOfPlayers = Integer.parseInt(result.get());
			playerList = new ArrayList<>(totalNumberOfPlayers);
			serviceObject.setTerritoriesAndArmiesToPlayers(playerList, totalNumberOfPlayers);
			formMap();
			currentPlayer = serviceObject.getPlayer(null, playerList);
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
			mapGrid.setConstraints(continentName, colCounter, 1);
			mapGrid.setConstraints(controlValueLabel, colCounter, 2);
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
				mapGrid.setConstraints(territoryField, colCounter, i + 3);
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
			String error = "Reinforcement Phase Completed";
			showError(error);
			//TODO
			//Add attack phase to this.
		}
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

}
