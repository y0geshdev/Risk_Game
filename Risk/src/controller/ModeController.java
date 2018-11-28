package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.IStrategy;
import domain.Player;
import domain.PlayerStrategyEnum;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import service.MapService;

public class ModeController {

	@FXML
	private ComboBox<Player> playerListTM;

	@FXML
	private ListView<PlayerStrategyEnum> allStrategiesTM;

	@FXML
	private ListView<PlayerStrategyEnum> mappedStrategiesTM;

	@FXML
	private TextField movesForDraw;

	@FXML
	private TextField noOfGamesTM;

	@FXML
	private TextField map1PathTM;

	@FXML
	private TextField map2PathTM;

	@FXML
	private TextField map3PathTM;

	@FXML
	private TextField map4PathTM;

	@FXML
	private TextField map5PathTM;

	@FXML
	private TextField noOfPlayerTM;

	@FXML
	private TextField map1PathSM;

	@FXML
	private TextField noOfPlayerSM;

	@FXML
	private ComboBox<Player> playerListSM;

	@FXML
	private ListView<PlayerStrategyEnum> allStrategiesSM;

	@FXML
	private ListView<PlayerStrategyEnum> mappedStrategiesSM;

	@FXML
	private Button addSM;

	@FXML
	private Button removeSM;

	@FXML
	private Button addTM;

	@FXML
	private Button removeTM;

	@FXML
	private Button gamePlaySM;

	@FXML
	private Button gamePlayTM;

	@FXML
	AnchorPane singleModePane;

	@FXML
	AnchorPane tournamentModePaness;

	List<Player> playerList;

	List<PlayerStrategyEnum> allStrategies;
	Map<Player, PlayerStrategyEnum> playerStrategyMapping;

	MapService mapService = new MapService();

	public void setup() {
		allStrategies = new ArrayList<>();
		Collections.addAll(allStrategies, PlayerStrategyEnum.HUMAN, PlayerStrategyEnum.AGGRESSIVE,
				PlayerStrategyEnum.BENEVOLENT, PlayerStrategyEnum.RANDOM, PlayerStrategyEnum.CHEATER);
		// disableTMComponents();
		disableSMComponents();
	}

	public void setPlayerStartegyTM(ActionEvent event) {

		String error;
		if (map1PathTM.getText().trim().length() == 0 && map2PathTM.getText().trim().length() == 0
				&& map3PathTM.getText().trim().length() == 0 && map4PathTM.getText().trim().length() == 0
				&& map5PathTM.getText().trim().length() == 0) {
			showError("Choose at least one file to play");
		} else {

			try {
				int playersCount = Integer.parseInt(noOfPlayerTM.getText());
				int drawMoves = Integer.parseInt(movesForDraw.getText());
				int gameNumber = Integer.parseInt(noOfGamesTM.getText());
				if (playersCount <= 1) {
					error = "Number of Players cannot be less than 2";
					showError(error);
				} else if (drawMoves < 10 || drawMoves > 50) {
					error = "Enter Valid Number of Draw Moves";
					showError(error);
				} else if (gameNumber < 1 || gameNumber > 5) {
					error = "Enter Valid Number of Games";
					showError(error);
				} else {
					playerStrategyMapping = new HashMap<>();
					enableTMComponents();
					playerList = new ArrayList<>(playersCount);
					mapService.createPlayers(playerList, playersCount);
					playerListTM.setItems(FXCollections.observableList(playerList));
					playerListTM.setValue(playerList.get(0));
					allStrategiesTM.setItems(FXCollections.observableList(allStrategies));
					for (Player player : playerList)
						playerStrategyMapping.put(player, null);
				}
			} catch (NumberFormatException e) {
				error = "Enter a valid Number For Draw, Player Count and Number of Games";
				showError(error);
			}

		}

	}

	@FXML
	public void addPlayerStrategyTM(ActionEvent event) {

		PlayerStrategyEnum selectedStrategy = allStrategiesTM.getSelectionModel().getSelectedItem();
		Player selectedPlayer = playerListTM.getSelectionModel().getSelectedItem();
		// Base case.
		if (selectedStrategy == null) {
			showError("Please select Strategy To Add");
			return;
		}
		playerStrategyMapping.put(selectedPlayer, selectedStrategy);
		mappedStrategiesTM.setItems(FXCollections.observableArrayList(playerStrategyMapping.get(selectedPlayer)));

	}

	public void removePlayerStrategyTM(ActionEvent event) {
		PlayerStrategyEnum selectedStrategy = mappedStrategiesTM.getSelectionModel().getSelectedItem();
		Player selectedPlayer = playerListTM.getSelectionModel().getSelectedItem();
		// Base case.
		if (selectedStrategy == null) {
			showError("Please select Strategy To Remove");
			return;
		}
		playerStrategyMapping.put(selectedPlayer, null);
		mappedStrategiesTM.setItems(FXCollections.observableArrayList());
	}

	@FXML
	public void addPlayerStrategySM(ActionEvent event) {

		PlayerStrategyEnum selectedStrategy = allStrategiesSM.getSelectionModel().getSelectedItem();
		Player selectedPlayer = playerListSM.getSelectionModel().getSelectedItem();
		// Base case.
		if (selectedStrategy == null) {
			showError("Please select Strategy To Add");
			return;
		}
		playerStrategyMapping.put(selectedPlayer, selectedStrategy);
		mappedStrategiesSM.setItems(FXCollections.observableArrayList(playerStrategyMapping.get(selectedPlayer)));

	}

	@FXML
	public void removePlayerStrategySM(ActionEvent event) {

		PlayerStrategyEnum selectedStrategy = mappedStrategiesSM.getSelectionModel().getSelectedItem();
		Player selectedPlayer = playerListSM.getSelectionModel().getSelectedItem();
		// Base case.
		if (selectedStrategy == null) {
			showError("Please select Strategy To Remove");
			return;
		}
		playerStrategyMapping.put(selectedPlayer, null);
		mappedStrategiesSM.setItems(FXCollections.observableArrayList());

	}

	@FXML
	public void setPlayerStartegySM(ActionEvent event) {

		if (map1PathSM.getText().trim().length() == 0 || noOfPlayerSM.getText().trim().length() == 0) {
			showError("Invalid file or number of players");
			return;
		}
		File file = new File(map1PathSM.getText());
		List<String> errorList = new ArrayList<>();

		// parse user selected map
		mapService.parseFile(file, errorList);
		if (errorList.size() > 0) {
			String errors = "Resolve below errors:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
			return;
		}
		// validate map which is in memory.
		mapService.validateMap(MapController.continentsSet, MapController.territoriesSet, errorList);

		if (errorList.size() > 0) {
			String errors = "Resolve below errors:";
			for (String error : errorList)
				errors = errors.concat("\n-" + error);
			showError(errors);
			return;
		}
		int playersCount;
		try {
			playersCount = Integer.parseInt(noOfPlayerSM.getText());
		} catch (NumberFormatException e) {
			showError("Enter a valid Number");
			return;
		}

		if (playersCount <= 1) {
			showError("Number of Players cannot be less than 2");
		} else {
			enableSMComponents();
			playerStrategyMapping = new HashMap<>();
			playerList = new ArrayList<>(playersCount);
			mapService.createPlayers(playerList, playersCount);

			playerListSM.setItems(FXCollections.observableList(playerList));
			playerListSM.setValue(playerList.get(0));
			allStrategiesSM.setItems(FXCollections.observableList(allStrategies));
			for (Player player : playerList)
				playerStrategyMapping.put(player, null);
		}

	}

	@FXML
	public void playSM(ActionEvent event) {

		String error;
		for (Player player : playerStrategyMapping.keySet()) {
			if (playerStrategyMapping.get(player) == null) {
				error = "Strategy for " + player + " is null";
				showError(error);
				return;
			}
		}

		IStrategy playerStrategy;
		for (Player player : playerList) {
			playerStrategy = mapService.getStrategyfromEnum(playerStrategyMapping.get(player));
			player.setPlayingStrategy(playerStrategy);
		}
		Parent root;
		GameController gameController;
		try {

			// load fxml for UI.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Game.fxml"));
			root = loader.load();
			gameController = loader.getController();
		} catch (IOException e) {
			showError("Unable to load Game.fxml file.");
			e.printStackTrace();
			return;
		}
		// setup stage
		Stage stage = new Stage();
		stage.setTitle("Game");
		stage.setScene(new Scene(root, 800, 600));
		stage.show();
		singleModePane.getScene().getWindow().hide();
		gameController.setGameStage(stage);
		gameController.startGame(MapController.continentsSet, MapController.territoriesSet, playerList,
				playerStrategyMapping);
		
	}

	public void playTM(ActionEvent event) {

		String error;
		for (int i = 0; i < playerList.size(); i++) {
			Player curPlayer = playerList.get(i);
			if (playerStrategyMapping.get(curPlayer) == null) {
				error = "Strategy for " + curPlayer + " is null";
				showError(error);
				return;
			}else if(playerStrategyMapping.get(curPlayer).equals(PlayerStrategyEnum.HUMAN)) {
				error = "Strategy for " + curPlayer + " cannot be Human in Tournament Mode";
				showError(error);
				return;
			}
		}

		IStrategy playerStrategy;
		for (Player player : playerList) {
			playerStrategy = mapService.getStrategyfromEnum(playerStrategyMapping.get(player));
			player.setPlayingStrategy(playerStrategy);
		}
		
		int drawMoves = Integer.parseInt(movesForDraw.getText());
		int noOfGames = Integer.parseInt(noOfGamesTM.getText());
		List<File> mapFiles = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			addFiles(i, mapFiles);
		}
		Parent root;
		GameController gameController;
		try {

			// load fxml for UI. 
			FXMLLoader loader = new
			FXMLLoader(getClass().getResource("/ui/Game.fxml"));
			root = loader.load();
			gameController = loader.getController();
		} catch (IOException e) {
			showError("Unable to load Game.fxml file.");
			e.printStackTrace();
			return;
		}
		// setup stage 
		Stage stage = new Stage(); stage.setTitle("Game");
		stage.setScene(new Scene(root, 800, 600));
		stage.show();
		gameController.playTournament(playerList, drawMoves, noOfGames, mapFiles,playerStrategyMapping);

	}

	public void addFiles(int ind, List<File> mapFiles) {
		File file;
		switch (ind) {
		case 0:
			if (map1PathTM.getText().trim().length() != 0) {
				file = new File(map1PathTM.getText());
				mapFiles.add(file);
			}
			break;
		case 1:
			if (map2PathTM.getText().trim().length() != 0) {
				file = new File(map2PathTM.getText());
				mapFiles.add(file);
			}
			break;

		case 2:
			if (map3PathTM.getText().trim().length() != 0) {
				file = new File(map3PathTM.getText());
				mapFiles.add(file);
			}
			break;

		case 3:
			if (map4PathTM.getText().trim().length() != 0) {
				file = new File(map4PathTM.getText());
				mapFiles.add(file);
			}
			break;

		case 4:
			if (map5PathTM.getText().trim().length() != 0) {
				file = new File(map5PathTM.getText());
				mapFiles.add(file);
			}
			break;
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

	@FXML
	public void chooseFileSM(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter(".map file", "*.map"));
		File file = chooser.showOpenDialog(null);
		map1PathSM.setText(file == null ? "" : file.getAbsolutePath());
	}

	@FXML
	public void chooseFileTM(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter(".map file", "*.map"));
		File file = chooser.showOpenDialog(null);
		if (map1PathTM.getText().trim().length() == 0) {
			map1PathTM.setText(file == null ? "" : file.getAbsolutePath());
			return;
		} else if (map2PathTM.getText().trim().length() == 0) {
			map2PathTM.setText(file == null ? "" : file.getAbsolutePath());
			return;
		} else if (map3PathTM.getText().trim().length() == 0) {
			map3PathTM.setText(file == null ? "" : file.getAbsolutePath());
			return;
		} else if (map4PathTM.getText().trim().length() == 0) {
			map4PathTM.setText(file == null ? "" : file.getAbsolutePath());
			return;
		} else if (map5PathTM.getText().trim().length() == 0) {
			map5PathTM.setText(file == null ? "" : file.getAbsolutePath());
			return;
		}

	}

	public void disableSMComponents() {
		playerListSM.setDisable(true);
		addSM.setDisable(true);
		removeSM.setDisable(true);
		allStrategiesSM.setDisable(true);
		mappedStrategiesSM.setDisable(true);
		gamePlaySM.setDisable(true);
	}

	public void enableSMComponents() {
		playerListSM.setDisable(false);
		addSM.setDisable(false);
		removeSM.setDisable(false);
		allStrategiesSM.setDisable(false);
		mappedStrategiesSM.setDisable(false);
		gamePlaySM.setDisable(false);
	}

	public void disableTMComponents() {
		playerListTM.setDisable(true);
		addTM.setDisable(true);
		removeTM.setDisable(true);
		allStrategiesTM.setDisable(true);
		mappedStrategiesTM.setDisable(true);
		gamePlayTM.setDisable(true);
	}

	public void enableTMComponents() {
		playerListTM.setDisable(false);
		addTM.setDisable(false);
		removeTM.setDisable(false);
		allStrategiesTM.setDisable(false);
		mappedStrategiesTM.setDisable(false);
		gamePlayTM.setDisable(false);
	}

	@FXML
	public void updateMappedStrategyLV(ActionEvent event) {
		Player selectedPlayer = playerListSM.getValue();
		if (selectedPlayer == null || playerStrategyMapping.get(selectedPlayer) == null) {
			mappedStrategiesSM.setItems(FXCollections.observableArrayList());
			return;
		} else {
			mappedStrategiesSM.setItems(FXCollections.observableArrayList(playerStrategyMapping.get(selectedPlayer)));
		}

	}

	@FXML
	public void updateMappedStrategyTM(ActionEvent event) {
		Player selectedPlayer = playerListTM.getValue();
		if (selectedPlayer == null || playerStrategyMapping.get(selectedPlayer) == null) {
			mappedStrategiesTM.setItems(FXCollections.observableArrayList());
			return;
		} else {
			mappedStrategiesTM.setItems(FXCollections.observableArrayList(playerStrategyMapping.get(selectedPlayer)));
		}

	}
}
