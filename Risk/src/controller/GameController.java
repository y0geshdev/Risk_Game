package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import domain.CardExchangeViewModel;
import domain.Continent;
import domain.GameObjectClass;
import domain.PhaseViewModel;
import domain.Player;
import domain.PlayerStrategyEnum;
import domain.Territory;
import domain.WorldDominationModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import service.GameService;
import service.MapService;

/**
 * This controller handles Game.fxml controls and implements game driver.
 * 
 * @author Yogesh
 *
 */
public class GameController {

	/**
	 * AnchorPane for phase view.
	 */
	@FXML
	private AnchorPane phaseViewUI;

	/**
	 * AnchorPane for World Domination view.
	 */
	@FXML
	private AnchorPane worldDominationViewUI;

	/**
	 * Variable for reference for GridPane in Game.fxml.
	 */
	@FXML
	private GridPane mapGrid;

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
	 * Variable to hold user's choice for attack with All-Out mode.
	 */
	@FXML
	private RadioButton allOutModeRB;

	/**
	 * Variable to hold user's choice for attack with Normal mode.
	 */
	@FXML
	private RadioButton normalModeRB;

	/**
	 * Variable to point to group of allOutModeRB and normalModeRB.
	 */
	@FXML
	private ToggleGroup attackMode;

	/**
	 * Variable to point TextField to user to enter attacker number of dice if
	 * normalMode is selected.
	 */
	@FXML
	private TextField attackerTotalDiceTF;

	/**
	 * Variable to point TextField to user to enter defender number of dice if
	 * normalMode is selected.
	 */
	@FXML
	private TextField defenderTotalDiceTF;

	/**
	 * Game constant for CONTROL_VALUE_WITH_SEMICOLON string.
	 */
	private static final String CONTROL_VALUE_WITH_SEMICOLON = "Control Value :";

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
	 * Reference variable for PhaseViewModel
	 */
	private PhaseViewModel phaseViewModel;

	/**
	 * Reference variable for WorldDominationModel
	 */
	private WorldDominationModel worldDominationModel;

	/**
	 * Reference to Set of continents.
	 */
	private HashSet<Continent> continentsSet;

	/**
	 * Reference to Set of Territories.
	 */
	private HashSet<Territory> territoriesSet;

	/**
	 * Reference variable for the CardExchangeViewModel
	 */
	private CardExchangeViewModel cardExchangeViewModel;

	/**
	 * Reference to stage of cardExchangeView.
	 */
	private Stage cardExchangeViewStage;
	
	
	/**
	 * String holding the name of current phase
	 */
	private String currentPhase;

	/**
	 * Reference to stage of game.
	 */
	private Stage gameStage = null;
	/**
	 * Game constant for STARTUP_PHASE string
	 */
	final private static String STARTUP_PHASE = "startUpPhase";
	
	/**
	 * Game constant for REINFORCEMENT_PHASE string
	 */
	final private static String REINFORCEMENT_PHASE = "reinforcementPhase";
	
	/**
	 * Game constant for ATTACK_PHASE string
	 */
	final private static String ATTACK_PHASE = "attackPhase";
	
	/**
	 * Game constant for FORTIFICATION_PHASE string
	 */
	final private static String FORTIFICATION_PHASE = "fortificationPhase";
	 
	/**
	 * Reference to file named savedfile initiated with null
	 */
	private static File savedFile = null;

	Map<Player, PlayerStrategyEnum> playerStrategyMapping;

	String statForTournamentMode = null;

	boolean ifTournamentMode = false;

	Integer movesToDefineDraw = 0;

	Map<String, List<String>> tournamentModeResult = new LinkedHashMap<>();

	Thread mainGameThread = new Thread();

	/**
	 * 
	 * 
	 * @param continentsSet
	 * @param territoriesSet
	 */

	/**
	 * This method forms the game map on UI, distributes territories randomly to the
	 * players, select the player randomly to start the startup phase of the game.
	 * 
	 * @param continentsSet:
	 *            Set of all the continents in game.
	 * @param territoriesSet:
	 *            Set of all the territories in game.
	 * @param playersList:
	 *            List of players.
	 * @param playerStrategyMapping:
	 *            Mapping of players and their strategies.
	 */
	public void startGame(HashSet<Continent> continentsSet, HashSet<Territory> territoriesSet, List<Player> playersList,
			Map<Player, PlayerStrategyEnum> playerStrategyMapping) {
		this.continentsSet = continentsSet;
		this.territoriesSet = territoriesSet;
		this.playersList = playersList;
		this.playerStrategyMapping = playerStrategyMapping;
		//Registers an event of window close request to this window of this scene of game stage 
		 // and handles that event with actionOnClosingWindow method
		 		
		gameStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
				new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {

						actionOnClosingWindow(event);

					}
				});

		// SetUp UI and game context.
		displayMap();
		disableComponents(attackPhaseUI);
		disableComponents(fortiPhaseUI);
		disableComponents(reinfoPhaseUI);

		/*
		 * while (getPlayersCount() == -1) ;
		 */
		// testing purpose
		/*
		 * playersCount = 2;
		 * 
		 * playersList = new ArrayList<>(playersCount);
		 * gameService.createPlayers(playersList, playersCount);
		 * 
		 * // testing purpose playerStrategyMapping.put(playersList.get(0),
		 * PlayerStrategyEnum.HUMAN); playerStrategyMapping.put(playersList.get(1),
		 * PlayerStrategyEnum.AGGRESSIVE);
		 */
		// playerStrategyMapping.put(playersList.get(2), PlayerStrategyEnum.AGGRESSIVE);

		gameService.assignTerritories(playersList, territoriesSet);
		updateMapData();

		currentPlayer = gameService.getNextPlayer(null, playersList);
		enableComponents(reinfoPhaseUI);
		List<String> errorList = new ArrayList<>();

		// setup Phase view, World domination view and Card exchange view.
		setUpPhaseAndWorldDominationViews(errorList);
		setUpCardExchangeView(errorList);

		if (errorList.size() > 0) {
			showError(errorList.get(0));
			Platform.exit();
		} else {
			worldDominationModel.updateState(continentsSet, territoriesSet);
			startUpPhase();
		}

	}
	
	/**
	 * This method handles the window closing event on the scene of game stage
	 * @param event :
	 * 			Handling the event of window closing of game stage
	 */
	private void actionOnClosingWindow(WindowEvent event) {
		savedFile = null;
		territoriesSet = null;
		continentsSet = null;
		currentPhase = null;
		currentPlayer = null;
		playersList = null;
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
		numberOfArmiesPerTerritory.setText("");
		List<String> errorList = new ArrayList<>();

		// validate entered string as army number and show errors if there are any.
		gameService.validateArmyInput(armyInput, currentPlayer, selectedTerritory, errorList);
		if (errorList.size() != 0) {
			String errors = "Resolve below errors:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
			return;
		}
		int numberOfArmies = Integer.parseInt(armyInput);
		// perform reinforcement
		gameService.addReinforcement(selectedTerritory.getOwner(), selectedTerritory, numberOfArmies, phaseViewModel);

		// update UI.
		updateTerritoryFields(selectedTerritory);
		worldDominationModel.updateState(continentsSet, territoriesSet);
		sleep(1);
		Runnable task = new Runnable() {
			@Override
			public void run() {
				sleep(50);
				// perform action here.
				// check if startUp phase is completed or not.
				if (!ifStartUpIsComepleted) {
					if (currentPlayer.getArmyCount() == 0) {
						playersWithZeroArmies.add(currentPlayer);
					}
					currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
					while (currentPlayer.getArmyCount() == 0 && playersWithZeroArmies.size() != playersList.size()) {
						playersWithZeroArmies.add(currentPlayer);
						currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
					}
					if (playersWithZeroArmies.size() == playersList.size()
							|| playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
						enableComponents(reinfoPhaseUI);
						startUpPhase();
					} else {
						disableComponents(reinfoPhaseUI);
						reinforcementForNonHumanPlayer();
					}

				} else {
					reinforcementPhase();
				}
			}
		};
		Platform.runLater(task);
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

		// validate selected attacker an defender territory.
		if (attackerTerritory.getArmyCount() <= 1)
			showError("Can not attack with one Army.");
		else if (defenderTerritory == null) {
			showError("Select a territory to attack to. ");
		} else {
			// check to identify which attack mode is selected.
			boolean isAllOutMode = (RadioButton) (attackMode.getSelectedToggle()) == allOutModeRB;
			int totalAttackerDice = 0, totalDefenderDice = 0;

			// if its normal attack mode.
			if (!isAllOutMode) {
				List<String> errorList = new ArrayList<>();

				totalDefenderDice = defenderTerritory.getArmyCount() >= 2 ? 2 : 1;
				defenderTotalDiceTF.setText(String.valueOf(totalDefenderDice));
				// validate number of dice entered by user for attacker and defender for normal
				// mode.
				gameService.validateSelectedDiceNumber(attackerTerritory, defenderTerritory,
						attackerTotalDiceTF.getText(), String.valueOf(totalDefenderDice), errorList);

				// show if there are any validation errors.
				if (errorList.size() > 0) {
					String errors = "Issue with entered dice numbers:";
					for (String error : errorList) {
						errors = errors.concat("\n" + error);
					}
					showError(errors);
					return;
				} else {
					totalAttackerDice = Integer.parseInt(attackerTotalDiceTF.getText());
					// totalDefenderDice = Integer.parseInt(defenderTotalDiceTF.getText());
				}
			}

			// update UI regarding which territory attack which territory.
			updatePhaseInfo(null, null, attackerTerritory.getName() + " attacked on " + defenderTerritory.getName());

			// perform attack
			Pair<Boolean, Integer> attackResult = gameService.attack(attackerTerritory.getOwner(),
					defenderTerritory.getOwner(), attackerTerritory, defenderTerritory, isAllOutMode, totalAttackerDice,
					totalDefenderDice, phaseViewModel);

			// update UI
			updateTerritoryFields(attackerTerritory);
			updateTerritoryFields(defenderTerritory);

			boolean ifWon = attackResult.getKey();

			// if attacker Won then do fortification of won territory.
			if (ifWon) {
				int armiesToMove = -1;
				while (armiesToMove == -1) {
					armiesToMove = getNumberOfArmiesToMove(attackResult.getValue(),
							attackerTerritory.getArmyCount() - 1);
				}
				gameService.fortify(currentPlayer, attackerTerritory, defenderTerritory, armiesToMove, phaseViewModel);
				updatePhaseInfo(null, "Attack Phase", "Moved " + armiesToMove + " armies from "
						+ attackerTerritory.getName() + " to " + defenderTerritory.getName() + " after conquering it.");
			}

			// update UI
			worldDominationModel.updateState(continentsSet, territoriesSet);
			updateTerritoryFields(attackerTerritory);
			updateTerritoryFields(defenderTerritory);

			// card exchange state change
			if (!cardExchangeViewModel.getIfPlayerGetsCard() && ifWon)
				cardExchangeViewModel.setIfPlayerGetsCard(ifWon);

			sleep(1);
			Runnable task = new Runnable() {
				@Override
				public void run() {
					sleep(50);
					// perform action here.
					// check if current player won whole game.
					if (gameService.isGameEnded(currentPlayer, territoriesSet.size())) {
						showInformation("Player " + currentPlayer.getName() + " won the game.");
						Platform.exit();
					} else {

						// logic to automatically end the attack if current user can't attack anymore.
						boolean furtherAttackPossible = gameService.canPlayerAttackFurther(currentPlayer);

						// If there are territories for current player from which he can attack
						if (furtherAttackPossible) {
							attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
							attackAttackerCB.setValue(attackAttackerCB.getItems().get(0));

							List<Territory> defenderTerritories = gameService
									.getAttackableTerritories(currentPlayer.getTerritories().get(0));
							if (defenderTerritories.size() > 0) {
								attackDefenderCB.setDisable(false);
								attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
								attackDefenderCB.setValue(defenderTerritories.get(0));
							} else {
								attackDefenderCB.setDisable(true);
								attackDefenderCB.setValue(null);
							}
						}
						// else finish attack automatically.
						else {
							updatePhaseInfo(null, null, "Cannot attack any further.");
							sleep(1);
							Runnable task = new Runnable() {
								@Override
								public void run() {
									sleep(50);
									// perform action here.
									finishAttack(event);
								}
							};
							Platform.runLater(task);
						}
					}
				}
			};
			Platform.runLater(task);
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

		// get defender territory according to selected attacker territory.
		List<Territory> defenderTerritories = gameService.getAttackableTerritories(selectedTerritory);

		// update drop down with fetched defender territories.
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

		// to check if current player is entitled to draw a card from a deck or not.

		if (cardExchangeViewModel.getIfPlayerGetsCard()) {
			if (cardExchangeViewModel.getAllCards().size() != 0) {
				cardExchangeViewModel.assignCardToAPlayer(currentPlayer);
			} else {
				String info = "Player Won't be able to get a card as inital deck doesn't have enough cards";
				showInformation(info);
			}
			cardExchangeViewModel.setIfPlayerGetsCard(false);
		}
		currentPhase	=	GameController.FORTIFICATION_PHASE;
		// update UI
		updatePhaseInfo(null, "Fortification Phase", "Fortification Phase started.");
		attackerTotalDiceTF.disableProperty().unbind();
		defenderTotalDiceTF.disableProperty().unbind();
		disableComponents(attackPhaseUI);
		enableComponents(fortiPhaseUI);

		// prepare fortification phase drop downs
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

		// validate user inputs
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

		// validate fortification params
		gameService.validatefortifcationParameters(from, to, armiesToMove, errorList);

		// check for errors after validation
		if (errorList.size() > 0) {
			String errors = "Cannot fortify due to:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
		} else {
			// fortify and update UI
			gameService.fortify(currentPlayer, from, to, armiesToMove, phaseViewModel);
			updatePhaseInfo(null, null, phaseViewModel.getPhaseInfo() + "\nMoved " + armiesToMove + " armies from "
					+ from.getName() + " to " + to.getName());
			updateTerritoryFields(from);
			updateTerritoryFields(to);
			sleep(1);
			Runnable task = new Runnable() {
				@Override
				public void run() {
					sleep(50);
					finishFortification(event);
				}
			};
			Platform.runLater(task);

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

		// fetch fortifiable territory according to selected territory.
		List<Territory> fortifiableTerritories = gameService.getFortifiableTerritories(selectedTerritory);

		// update fortification phase drop downs.
		if (fortifiableTerritories.size() > 0) {
			fortifyToTerritoryCB.setDisable(false);
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
		armiesNoToFortify.setText("");
		disableComponents(fortiPhaseUI);

		// find next player in turn who is still playing
		currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
		while (currentPlayer.getTerritories().size() == 0)
			currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
		updatePhaseInfo(currentPlayer.getName(), "Reinforcement Phase",
				"Place reinforcement for " + currentPlayer.getName() + " territories.");

		if (playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
			// display exchange view every time after the fortification phase is finished.
			cardExchangeViewModel.setViewForCurrentPlayer(currentPlayer);
			cardExchangeViewStage.showAndWait();

			// keep exchanging card till player have cards less than 5
			while (cardExchangeViewModel.getCurrentPlayerCards(currentPlayer).size() >= 5) {
				cardExchangeViewStage.showAndWait();
			}

			// update UI
			cardExchangeViewStage.hide();
			setArmiesOnPlayerOwnedCardTerritory();
			enableComponents(reinfoPhaseUI);
			currentPhase	=	GameController.REINFORCEMENT_PHASE;
			// calculated armies for reinforcement phase.
			gameService.calcArmiesForReinforcement(currentPlayer);
			displayPlayerInfo();
		} else {
			gameService.calcArmiesForReinforcement(currentPlayer);
			reinforcementForNonHumanPlayer();
		}
	}

	/**
	 * This method is used to set armies on the territory owned by the player and
	 * the card exchanged by the player was of this territory
	 */
	public void setArmiesOnPlayerOwnedCardTerritory() {
		if (cardExchangeViewModel.getCardAndOwnedTerritory() != null) {
			Territory cardAndOwnedTerritory = cardExchangeViewModel.getCardAndOwnedTerritory();
			String state = "Placing 2 armies on " + cardAndOwnedTerritory.getName()
					+ " as one of the card Exchanged was of this territory and it is owned by the "
					+ currentPlayer.getName();

			// updating UI based on if player exchange a card corresponding to territory he
			// already owns.
			showInformation(state);
			cardAndOwnedTerritory.setArmyCount(cardAndOwnedTerritory.getArmyCount() + 2);
			updateTerritoryFields(cardAndOwnedTerritory);
			currentPlayer.setArmyCount(currentPlayer.getArmyCount() - 2);
			updatePhaseInfo(null, null, String.valueOf(2) + " armies moved to " + cardAndOwnedTerritory.getName());
			worldDominationModel.updateState(continentsSet, territoriesSet);
			cardExchangeViewModel.setCardAndOwnedTerritory(null);

		}
	}

	/**
	 * This method is used to set the card exchange view pop up.
	 */
	private void setUpCardExchangeView(List<String> errorList) {
		Parent root;
		try {
			// load fxml and add its controller to corresponding model.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/CardExchange.fxml"));
			root = loader.load();
			cardExchangeViewModel = new CardExchangeViewModel(territoriesSet);
			cardExchangeViewModel.addObserver(loader.getController());
		} catch (Exception e) {
			errorList.add("Issue setting up Card exchange view.");
			e.printStackTrace();
			return;
		}

		// setup stage
		cardExchangeViewModel.setViewForCurrentPlayer(currentPlayer);
		cardExchangeViewStage = new Stage();
		cardExchangeViewStage.setTitle("Risk Game");
		cardExchangeViewStage.setScene(new Scene(root));

	}

	/**
	 * This method begins the startUp phase.
	 */
	private void startUpPhase() {

		// if startUp phase is ended then prepare for reinforcement phase.
		if (gameService.endOfStartUpPhase(playersWithZeroArmies, playersList)) {
			ifStartUpIsComepleted = true;
			currentPlayer = gameService.getNextPlayer(null, playersList);
			updatePhaseInfo(currentPlayer.getName(), "Reinforcement Phase", "Reinforcement Phase started.");
			sleep(1);
			Runnable task = new Runnable() {
				@Override
				public void run() {
					sleep(20);
					// perform action here.
					if (playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
						gameService.calcArmiesForReinforcement(currentPlayer);
						cardExchangeViewModel.setViewForCurrentPlayer(currentPlayer);
						cardExchangeViewStage.showAndWait();
						enableComponents(reinfoPhaseUI);
						currentPhase	=	GameController.REINFORCEMENT_PHASE;
						reinforcementPhase();
					} else {
						disableComponents(reinfoPhaseUI);
						// need to add logic for card exchange of player is non human.
						gameService.calcArmiesForReinforcement(currentPlayer);
						reinforcementForNonHumanPlayer();
					}
				}
			};
			Platform.runLater(task);

		} else {
			updatePhaseInfo(currentPlayer.getName(), "StartUp Phase", "Place armies for " + currentPlayer.getName());
			if (playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
				enableComponents(reinfoPhaseUI);
				currentPhase	=	GameController.STARTUP_PHASE;
				displayPlayerInfo();
			} else {
				disableComponents(reinfoPhaseUI);
				reinforcementForNonHumanPlayer();
			}
		}
	}

	/**
	 * This method begins the reinforcement phase.
	 */
	private void reinforcementPhase() {
		if (gameService.endOfReinforcementPhase(currentPlayer, cardExchangeViewModel)) {

			disableComponents(reinfoPhaseUI);
			currentPhase	=	GameController.ATTACK_PHASE;
			// logic to automatically end the attack if current user can't attack anymore.
			boolean furtherAttackPossible = gameService.canPlayerAttackFurther(currentPlayer);

			// If there are territories for current player from which he can attack
			if (furtherAttackPossible) {
				updatePhaseInfo(currentPlayer.getName(), "Attack Phase", "Attack Phase Started.");

				// prepare UI and reinforcement phase drop downs
				enableComponents(attackPhaseUI);
				attackerTotalDiceTF.disableProperty().bind(Bindings.not(normalModeRB.selectedProperty()));
				defenderTotalDiceTF.disableProperty().bind(Bindings.not(normalModeRB.selectedProperty()));

				attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
				attackAttackerCB.setValue(currentPlayer.getTerritories().get(0));

				List<Territory> defenderTerritories = gameService
						.getAttackableTerritories(currentPlayer.getTerritories().get(0));
				if (defenderTerritories.size() > 0) {
					attackDefenderCB.setDisable(false);
					attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
					attackDefenderCB.setValue(defenderTerritories.get(0));
				} else {
					attackDefenderCB.setDisable(true);
					attackDefenderCB.setValue(null);
				}
			}
			// else finish attack automatically.
			else {
				finishAttack(null);
			}

		}
		displayPlayerInfo();
	}

	/**
	 * This method display map in grid form on UI.
	 */
	private void displayMap() {
		Iterator<Continent> ite = continentsSet.iterator();
		int colCounter = 0;
		ColumnConstraints widthCol = new ColumnConstraints();
		widthCol.setHgrow(Priority.ALWAYS);

		// iterate over continents of map
		while (ite.hasNext()) {
			mapGrid.getColumnConstraints().add(widthCol);

			Continent obj = ite.next();
			String nameofTheContinent = obj.getName();
			List<Territory> territoryList = obj.getTerritories();
			Integer controlValue = obj.getContinentArmyValue();

			// setup UI for current continent
			Label continentName = new Label(nameofTheContinent);
			Label controlValueLabel = new Label(GameController.CONTROL_VALUE_WITH_SEMICOLON + controlValue.toString());
			GridPane.setConstraints(continentName, colCounter, 1);
			GridPane.setConstraints(controlValueLabel, colCounter, 2);

			mapGrid.getChildren().addAll(continentName, controlValueLabel);
			if (maxNumberOfTerritores < territoryList.size()) {
				maxNumberOfTerritores = territoryList.size();
			}

			// iterate over selected continents territories
			for (int i = 0; i < territoryList.size(); i++) {

				// setup UI for current territory.
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

	}

	/**
	 * This method is responsible to get number of player playing this game from
	 * user.
	 * 
	 * @return int: number of players entered by user.
	 */
	private int getPlayersCount() {
		// setup dialog box
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Number Of Players");
		dialog.setContentText("Enter Number Of Players");
		Optional<String> result = dialog.showAndWait();
		String error;
		// check if user enter valid input or not.
		if (result.isPresent()) {
			try {
				playersCount = Integer.parseInt(result.get());
				if (playersCount <= 1) {
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
	 * This method populate the current player information on UI.
	 */
	private void displayPlayerInfo() {
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
	 * is clicked twice it highlight the neighboring territories' border by red and
	 * convert it back to the original color if clicked otherwise
	 * 
	 * @param territoryObj:
	 *            This is the the territory on which mouse entered or exited..
	 * @param isEntered:
	 *            true if mouse is entered else false.
	 */
	private void highlightNeighbours(Territory territoryObj, boolean isEntered) {

		List<Territory> neighbouringCountries = territoryObj.getNeighbourTerritories();

		// iterate over selected territoryObj's neighbor territory to highlight their
		// connectivity.
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
		territoryField.setText(
				terrObj.getName() + " : " + String.valueOf(terrObj.getArmyCount()) + " - (" + playerName + ")");

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

	/**
	 * This method is used to get the number of armies player want to move from
	 * attacker territory to one which he just conquered after attack.
	 * 
	 * @param minArmy:
	 *            minimum number of armies which attacker is allowed to move.
	 * @param maxArmy:
	 *            maximum number of armies which attacker is allowed to move.
	 * @return an integer as armies to move.
	 */
	private int getNumberOfArmiesToMove(int minArmy, int maxArmy) {
		int armiesToMove = -1;

		// prepare dialog box
		TextInputDialog dialog = new TextInputDialog(String.valueOf(minArmy));
		dialog.setTitle("Risk Game Dialog");
		dialog.setHeaderText("You can move between " + minArmy + "-" + maxArmy + " armies.");
		dialog.setContentText("Total armies to move:");

		// get input from user and validate
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				armiesToMove = Integer.parseInt(result.get());
			} catch (NumberFormatException e) {
				showError("Enter a valid number.");
				return -1;
			}
			if (armiesToMove < minArmy || armiesToMove > maxArmy) {
				showError("You can only move armies between " + minArmy + "-" + maxArmy);
				return -1;
			}
		} else {
			showError("You have to enter a valid number to capture territory.");
			return -1;
		}
		return armiesToMove;
	}

	/**
	 * A centralized method which is called from all over the code to update the
	 * phase information model state.
	 * 
	 * @param playerName:
	 *            Player name for which this information is updated.
	 * @param phase:
	 *            Phase which is currently player.
	 * @param info:
	 *            Information in that phase to be displayed to phase information
	 *            view.
	 */
	private void updatePhaseInfo(String playerName, String phase, String info) {
		if (playerName != null)
			phaseViewModel.setCurrentPlayer(playerName);
		if (phase != null)
			phaseViewModel.setCurrentPhase(phase);
		if (info != null)
			phaseViewModel.setPhaseInfo(info);
	}

	/**
	 * This method setup the PhaseInformationView and WorldDominationView using
	 * observer pattern.
	 * 
	 * @param errorList:
	 *            a list which holds errors if there is some issue with this method.
	 */
	private void setUpPhaseAndWorldDominationViews(List<String> errorList) {

		FXMLLoader loader;
		Pane phaseViewPane = null;
		PhaseViewController phaseViewController = null;
		try {

			// load fxml and add its controller to corresponding model.
			loader = new FXMLLoader(getClass().getResource("/ui/PhaseView.fxml"));
			phaseViewPane = loader.load();
			phaseViewController = loader.getController();

		} catch (Exception e) {
			errorList.add("Issue setting up Phase view.");
			e.printStackTrace();
			return;
		}

		// setup view
		phaseViewUI.getChildren().add(phaseViewPane);
		phaseViewPane.prefHeightProperty().bind(phaseViewUI.heightProperty());
		phaseViewPane.prefWidthProperty().bind(phaseViewUI.widthProperty());
		phaseViewModel = new PhaseViewModel();
		phaseViewModel.addObserver(phaseViewController);

		Pane worldDominationViewPane = null;
		WorldDominationViewController worldDominationViewController = null;
		try {

			// load fxml and add its controller to corresponding model.
			loader = new FXMLLoader(getClass().getResource("/ui/WorldDominationView.fxml"));
			worldDominationViewPane = loader.load();
			worldDominationViewController = loader.getController();

		} catch (Exception e) {
			errorList.add("Issue setting up World Domination view.");
			e.printStackTrace();
			return;
		}

		// setup view
		worldDominationViewUI.getChildren().add(worldDominationViewPane);
		worldDominationModel = new WorldDominationModel(playersList);
		worldDominationModel.addObserver(worldDominationViewController);
		worldDominationViewController.setUpWorldDominationView(playersList);
	}
	
	/**
	 * This method handle {@link GameController#saveGame} button event and save the game
	 * by serializing required objects to a file. It also lets the user to save again and again
	 * and all changes are update in same file until user uses the button Save and Quit.
	 * @param event:
	 * 			ActionEvent instance which is generated by the user.
	 */
	public void saveGameState(ActionEvent event) {
		if (savedFile == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new ExtensionFilter(".ser files", "*.ser"));
			savedFile = fileChooser.showSaveDialog(null);
				}
		FileOutputStream fileOutput;
		ObjectOutputStream out = null;

		try {
			fileOutput = new FileOutputStream(savedFile);
			out = new ObjectOutputStream(fileOutput);
			GameObjectClass gameState = new GameObjectClass(continentsSet, territoriesSet, playersList, currentPlayer,
					currentPhase,ifStartUpIsComepleted, playerStrategyMapping);

			out.writeObject(gameState);
			String info = "Game Saved";
			showInformation(info);
			out.close();
		} catch (Exception e) {
			String error = "Game Cannot be saved";
			showError(error);
			e.printStackTrace();
			return;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * This method handle {@link GameController#saveGame} button event and save the game
	 * by serializing required objects to a file and also exits the game
	 * @param event:
	 * 			ActionEvent instance which is generated by the user.
	 */
	public void saveAndExitGame(ActionEvent event) {
		if (savedFile == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new ExtensionFilter(".ser files", "*.ser"));
			savedFile = fileChooser.showSaveDialog(null);			
		}
		serialize(savedFile, continentsSet, territoriesSet, playersList, currentPlayer,
				currentPhase,ifStartUpIsComepleted, playerStrategyMapping);
	}
	
	/**
	 * This method serializes the game state to a file
	 * 
	 * @param fileToSave:
	 * 				file in which game state is saved
	 * @param continentSet:
	 * 				Set of Continents at that game state
	 * @param territorySet:
	 * 				Set of Territories at that game state
	 * @param playerList:
	 * 				List of players playing the game at that game state
	 * @param currentPlayer:
	 * 				Player playing at that instance of game state
	 * @param currentPhase:
	 * 				Phase at that game state
	 * @param ifStartUpIsComepleted:
	 * 				boolean parameter true if start up phase is completed else false
	 * @return true if game state is serialized
	 */
	public boolean serialize(File fileToSave,HashSet<Continent> continentSet, HashSet<Territory> territorySet, List<Player> playerList,
			Player currentPlayer,String currentPhase,boolean ifStartUpIsComepleted, Map<Player,PlayerStrategyEnum> playerStrategyMapping) {
		
		FileOutputStream fileOutput;
		ObjectOutputStream out = null;

		try {
			fileOutput = new FileOutputStream(fileToSave);
			out = new ObjectOutputStream(fileOutput);
			GameObjectClass gameState = new GameObjectClass(continentSet, territorySet, playersList, currentPlayer,
					currentPhase,ifStartUpIsComepleted, playerStrategyMapping);

			out.writeObject(gameState);
			out.close();
			fileToSave = null;
			Platform.exit();
		} catch (Exception e) {
			String error = "Game Cannot be saved";
			showError(error);
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				fileToSave = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
     return true;
	}

	/**
	 * This method resumes the game to state where game was saved
	 * 
	 * @param continentsSet2:
	 * 				Set of continent at last saved state of the game
	 * @param territoriesSet2:
	 * 				Set of Territories at last saved state of the game
	 * @param playersList2:
	 * 				List of players at last saved state of the game
	 * @param currentPlayer2:
	 * 				Player playing at last saved state of the game
	 * @param currentPhase2:
	 * 				Phase at last saved state of the game
	 * @param ifStartUpIsComepleted2:
	 * 				boolean parameter true if start up phase is completed else false
	 * @param file:
	 * 				File from which information of game state to be retrieved 
	 */
	public void resumeGame(HashSet<Continent> continentsSet2, HashSet<Territory> territoriesSet2,
			List<Player> playersList2, Player currentPlayer2, String currentPhase2,boolean ifStartUpIsComepleted2, Map<Player,PlayerStrategyEnum> playerStrategyMapping2,File file) {

		gameStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
				new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {

						actionOnClosingWindow(event);

					}
				});
		this.continentsSet = continentsSet2;
		this.territoriesSet = territoriesSet2;
		playersList = playersList2;
		currentPlayer = currentPlayer2;
		savedFile = file;
		ifStartUpIsComepleted	=	ifStartUpIsComepleted2;
		this.playerStrategyMapping = playerStrategyMapping2;

		displayMap();
		updateMapData();
		disableComponents(attackPhaseUI);
		disableComponents(fortiPhaseUI);
		disableComponents(reinfoPhaseUI);
		List<String> errorList = new ArrayList<>();

		// setup Phase view, World domination view and Card exchange view.
		setUpPhaseAndWorldDominationViews(errorList);
		setUpCardExchangeView(errorList);
		worldDominationModel.updateState(continentsSet2, territoriesSet2);
		if (errorList.size() > 0) {
			Platform.exit();
		} else {

			if (currentPhase2.equalsIgnoreCase(GameController.STARTUP_PHASE)) {

				enableComponents(reinfoPhaseUI);
				startUpPhase();
			} else if (currentPhase2.equalsIgnoreCase(GameController.REINFORCEMENT_PHASE)) {
				updatePhaseInfo(currentPlayer.getName(), "Reinforcement Phase", "Reinforcement Phase started.");
				cardExchangeViewStage.showAndWait();
				ifStartUpIsComepleted = true;
				startUpAndReinforcementId.setText("Reinforcement Phase");
				enableComponents(reinfoPhaseUI);
				reinforcementPhase();
			} else if (currentPhase2.equalsIgnoreCase(GameController.ATTACK_PHASE)) {

				updatePhaseInfo(currentPlayer.getName(), "Attack Phase", "Attack Phase Started.");
				// prepare UI and reinforcement phase drop downs
				currentPhase = GameController.ATTACK_PHASE;
				enableComponents(attackPhaseUI);
				attackerTotalDiceTF.disableProperty().bind(Bindings.not(normalModeRB.selectedProperty()));
				defenderTotalDiceTF.disableProperty().bind(Bindings.not(normalModeRB.selectedProperty()));

				attackAttackerCB.setItems(FXCollections.observableList(currentPlayer.getTerritories()));
				attackAttackerCB.setValue(currentPlayer.getTerritories().get(0));

				List<Territory> defenderTerritories = gameService
						.getAttackableTerritories(currentPlayer.getTerritories().get(0));
				if (defenderTerritories.size() > 0) {
					attackDefenderCB.setDisable(false);
					attackDefenderCB.setItems(FXCollections.observableList(defenderTerritories));
					attackDefenderCB.setValue(defenderTerritories.get(0));
				} else {
					attackDefenderCB.setDisable(true);
					attackDefenderCB.setValue(null);
				}

			} else {
				updatePhaseInfo(null, "Fortification Phase", "Fortification Phase started.");
				currentPhase = GameController.FORTIFICATION_PHASE;
				attackerTotalDiceTF.disableProperty().unbind();
				defenderTotalDiceTF.disableProperty().unbind();
				disableComponents(attackPhaseUI);
				enableComponents(fortiPhaseUI);

				// prepare fortification phase drop downs
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
		}
	}
	/**
	 * Setter for gameStage
	 * @param gameStage:
	 * 			Represents stage of the game.
	 */
	public void setGameStage(Stage gameStage) {
		this.gameStage = gameStage;
	}

	public void playTournament(List<Player> playerList, int movesForDraw, int noOfGames, List<File> mapFiles,
			Map<Player, PlayerStrategyEnum> playerStrategyMapping) {

		List<String> errorList = new ArrayList<>();
		ifTournamentMode = true;
		this.playerStrategyMapping = playerStrategyMapping;
		MapService mapService = new MapService();
		for (int i = 0; i < noOfGames; i++) {
			for (int j = 0; j < mapFiles.size();j++) {
				movesToDefineDraw = movesForDraw;
				File curFile = mapFiles.get(j);
				mapService.parseFile(curFile, errorList);
				mapService.validateMap(MapController.continentsSet, MapController.territoriesSet, errorList);

				if (errorList.size() == 0) {
					this.continentsSet = MapController.continentsSet;
					this.territoriesSet = MapController.territoriesSet;

					cardExchangeViewModel = new CardExchangeViewModel(territoriesSet);
					displayMap();
					disableComponents(reinfoPhaseUI);
					disableComponents(attackPhaseUI);
					disableComponents(fortiPhaseUI);
					playersList = playerList;
					gameService.assignTerritories(playersList, this.territoriesSet);
					updateMapData();
					currentPlayer = gameService.getNextPlayer(null, playersList);
					setUpPhaseAndWorldDominationViews(errorList);
					if (errorList.size() > 0) {
						showError(errorList.get(0));
						Platform.exit();
					} else {
						worldDominationModel.updateState(continentsSet, territoriesSet);

						startUpPhase();

						if (tournamentModeResult.get(curFile.getName()) == null) {
							List<String> tmResult = new ArrayList<>();
							tmResult.add(statForTournamentMode);
							tournamentModeResult.put(curFile.getName(), tmResult);
						} else {

							tournamentModeResult.get(curFile.getName()).add(statForTournamentMode);
						}

					}
				} else {
					showError("Not able to load file " + curFile.getName());
				}
			}

		}
		Iterator<String> keys = tournamentModeResult.keySet().iterator();
		while (keys.hasNext()) {
			String mapName = keys.next();
			for (int i = 0; i < tournamentModeResult.get(mapName).size(); i++) {
				System.out.println(mapName + " Result is " + tournamentModeResult.get(mapName).get(i));
			}
		}
	}

	private void reinforcementForNonHumanPlayer() {

		// do reinforcement or startup and update UI
		playerArmies.setText(String.valueOf(currentPlayer.getArmyCount()));
		if (ifStartUpIsComepleted) {
			startUpAndReinforcementId.setText("Reinforcement Phase");
		}

		gameService.addReinforcement(currentPlayer, null, 0, phaseViewModel);
		for (Territory territory : currentPlayer.getTerritories()) {
			updateTerritoryFields(territory);
		}
		worldDominationModel.updateState(continentsSet, territoriesSet);

		sleep(1);
		Runnable task = new Runnable() {

			@Override
			public void run() {
				sleep(50);
				// check if startUp phase is completed or not.
				if (!ifStartUpIsComepleted) {

					// if current player have no armies to place then add it to playersWithZeroAmies
					// set.
					if (currentPlayer.getArmyCount() == 0) {
						playersWithZeroArmies.add(currentPlayer);
					}
					// keep fetching next player till you find a player with non zero armies or all
					// the players are added to playersWithZeroArmies set.
					currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
					while (currentPlayer.getArmyCount() == 0 && playersWithZeroArmies.size() != playersList.size()) {
						playersWithZeroArmies.add(currentPlayer);
						currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
					}
					if (playersWithZeroArmies.size() == playersList.size()
							&& playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
						startUpPhase();
					} else {
						if (playersWithZeroArmies.size() == playersList.size()) {
							ifStartUpIsComepleted = true;
						}
						reinforcementForNonHumanPlayer();
					}

				} else {
					// if reinforcement is completed then prepare for attack phase and call for
					// attack for non human player.
					if (gameService.endOfReinforcementPhase(currentPlayer, cardExchangeViewModel)) {
						playerArmies.setText(null);
						playerTerritoryList.setValue(null);
						attackForNonHumanPlayer();
					} else {
						reinforcementForNonHumanPlayer();
					}
				}
			}
		};
		Platform.runLater(task);
	}

	private void attackForNonHumanPlayer() {

		updatePhaseInfo(currentPlayer.getName(), "Attack Phase", "Attack Phase Started.");

		// perform attack
		Pair<Boolean, Integer> attackResult = gameService.attack(currentPlayer, null, null, null, true, 0, 0,
				phaseViewModel);

		// update UI
		updateMapData();
		worldDominationModel.updateState(continentsSet, territoriesSet);

		boolean ifWon = attackResult.getKey();

		// card exchange state change
		if (!cardExchangeViewModel.getIfPlayerGetsCard() && ifWon)
			cardExchangeViewModel.setIfPlayerGetsCard(ifWon);

		// to check if current player is entitled to draw a card from a deck or not.
		if (cardExchangeViewModel.getIfPlayerGetsCard()) {
			if (cardExchangeViewModel.getAllCards().size() != 0) {
				cardExchangeViewModel.assignCardToAPlayer(currentPlayer);
			} else {
				phaseViewModel.setPhaseInfo(phaseViewModel.getPhaseInfo()
						+ "\nPlayer Won't be able to get a card as inital deck doesn't have enough cards.");
			}
			cardExchangeViewModel.setIfPlayerGetsCard(false);
		}

		// check if current player won whole game.
		if (gameService.isGameEnded(currentPlayer, territoriesSet.size())) {
			showInformation("Player " + currentPlayer.getName() + " won the game.");
			if (!ifTournamentMode) {
				Platform.exit();
			} else {
				statForTournamentMode = currentPlayer.getName();
				return;
			}
		} else {
			// to end attack phase for non human player and start fortification phase.
			sleep(1);
			Runnable task = new Runnable() {
				@Override
				public void run() {
					sleep(70);
					fortificationForNonHumanPlayer();
				}
			};
			Platform.runLater(task);
		}
	}

	private void fortificationForNonHumanPlayer() {
		updatePhaseInfo(null, "Fortification Phase", "Fortification Phase started.");
		gameService.fortify(currentPlayer, null, null, 0, phaseViewModel);
		for (Territory territory : currentPlayer.getTerritories()) {
			updateTerritoryFields(territory);
		}
		worldDominationModel.updateState(continentsSet, territoriesSet);
		// finishFortification(event);
		if (ifTournamentMode) {
			movesToDefineDraw--;
		}
		if (movesToDefineDraw == 0 && ifTournamentMode) {
			statForTournamentMode = "Draw";
			return;
		}
		sleep(1);
		Runnable task = new Runnable() {
			@Override
			public void run() {
				sleep(50);
				// perform action here.
				armiesNoToFortify.setText("");
				disableComponents(fortiPhaseUI);

				// find next player in turn who is still playing
				currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
				while (currentPlayer.getTerritories().size() == 0)
					currentPlayer = gameService.getNextPlayer(currentPlayer, playersList);
				updatePhaseInfo(currentPlayer.getName(), "Reinforcement Phase",
						"Place reinforcement for " + currentPlayer.getName() + " territories.");

				if (playerStrategyMapping.get(currentPlayer).equals(PlayerStrategyEnum.HUMAN)) {
					// display exchange view every time after the fortification phase is finished.
					cardExchangeViewModel.setViewForCurrentPlayer(currentPlayer);
					cardExchangeViewStage.showAndWait();
					// keep exchanging card till player have cards less than 5
					while (cardExchangeViewModel.getCurrentPlayerCards(currentPlayer).size() >= 5) {
						cardExchangeViewStage.showAndWait();
					}

					// update UI
					cardExchangeViewStage.hide();
					setArmiesOnPlayerOwnedCardTerritory();
					enableComponents(reinfoPhaseUI);

					// calculated armies for reinforcement phase.
					gameService.calcArmiesForReinforcement(currentPlayer);
					displayPlayerInfo();
				} else {
					gameService.calcArmiesForReinforcement(currentPlayer);
					reinforcementForNonHumanPlayer();
				}
			}
		};

		Platform.runLater(task);

	}

	private void sleep(int time) {
		try {
			Thread.sleep(time * 100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
