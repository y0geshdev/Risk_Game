package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import domain.Continent;
import domain.Territory;
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

/**
 * RiskController class have all the methods which handle different actions
 * which are performed by user on UI which create or update Game Map.
 * 
 * @author Yogesh
 *
 */
public class MapController {

	/**
	 * This variable is the id of the text field that holds the value of the number
	 * of continents entered by the user.
	 */
	@FXML
	private TextField contsNoTF;

	/**
	 * This variable is the id of the text field holds the value of the number of
	 * territories entered by the user.
	 */
	@FXML
	private TextField terrsNoTF;
	
	/**
	 * This variable is the id of the button that generates the map options in the
	 * territory and continent combo box.
	 */
	@FXML
	private Button generateMapButton;
	
	/**
	 * This variable is the id of the text field that holds the value of the path of
	 * the selected file.
	 */
	@FXML
	private TextField mapPathMapsTF;
	
	/**
	 * This variable is the id of the button that helps in choosing the map for
	 * modification.
	 */
	@FXML
	private Button chooseMapsButton;
	
	/**
	 * This variable is the id of the button that populates the values of the chosen
	 * map in territory and continent combo box.
	 */
	@FXML
	private Button modifyMapButton;
	
	/**
	 * This variable is the id the of the text field that holds the value of the
	 * path of the map chosen for playing the game on.
	 */
	@FXML
	private TextField mapPathGameTF;
	
	/**
	 * This variable is the id of the button that helps in choosing the map for
	 * playing game on.
	 */
	@FXML
	private Button chooseGameButton;
	
	/**
	 * This variable is the id of the button that helps in starting the game.
	 */
	@FXML
	private Button startGameButton;
	
	/**
	 * This variable is the id of the AnchorPane which holds the UI for editing the
	 * map.
	 */
	@FXML
	private AnchorPane lowerAP;
	
	/**
	 * This variable is the id of the combo box that holds the continent values.
	 */
	@FXML
	private ComboBox<Continent> contsCB;
	
	/**
	 * This variable is the id of the list view that holds the values of the
	 * territories to be mapped with the selected continent.
	 */
	@FXML
	private ListView<Territory> allTerritoriesCTMapping;
	
	/**
	 * This variable is the id of the list view that holds the values of the
	 * territories that are mapped to the selected continent.
	 */
	@FXML
	private ListView<Territory> mappedTerritoriesCTMapping;
	
	/**
	 * This variable is the id of the button that removes the selected territory
	 * from the selected continent mapping.
	 */
	@FXML
	private Button removeTerrsToContsButton;
	
	/**
	 * This variable is the id of the button that add the selected territory to the
	 * selected continent mapping.
	 */
	@FXML
	private Button addTerrsToContsButton;
	
	/**
	 * This variable is the id of the combo box that holds the territory values.
	 */
	@FXML
	private ComboBox<Territory> terrsCB;
	
	/**
	 * This variable is the id of the list view that holds the values of the
	 * territories to be mapped with the selected territory.
	 */
	@FXML
	private ListView<Territory> allTerritoriesTTMapping;
	
	/**
	 * This variable is the id of the list view that holds the values of the
	 * territories that are mapped to the selected territory.
	 */
	@FXML
	private ListView<Territory> mappedTerritoriesTTMapping;
	
	/**
	 * This variable is the id of the button that add the selected territory to the
	 * selected territory mapping.
	 */
	@FXML
	private Button addTerrsToTerrsButton;
	
	/**
	 * This variable is the id of the button that removes the selected territory
	 * from the selected territory mapping.
	 */
	@FXML
	private Button removeTerrsToTerrsButton;
	
	/**
	 * This variable is the id of the button that shows the configured map.
	 */
	@FXML
	private Button showMapButton;
	
	/**
	 * This variable is the id of the button that saves the configured map.
	 */
	@FXML
	private Button saveMapButton;
	
	/**
	 * This variable is the id of the text field that holds the control value for a
	 * selected continent.
	 */
	@FXML
	private TextField contiControlValueTF;
	
	/**
	 * This variable is the id of the button that updates the control value of the
	 * continent.
	 */
	@FXML
	private Button updateContiControlValueButton;
	
	/**
	 * This variable stores the continent object.
	 */
	static public HashSet<Continent> continentsSet;
	
	/**
	 * This variable stores the territory object.
	 */
	static public HashSet<Territory> territoriesSet;
	
	/**
	 * This variable is to call map service functions.
	 */
	private MapService mapService = new MapService();

	/**
	 * This method handle {@link MapController#generateMap} button event. This
	 * generate number of territories and continents as mentioned by user and
	 * populate required fields on UI.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void generateMap(ActionEvent event) {
		MapController.continentsSet = new HashSet<>();
		MapController.territoriesSet = new HashSet<>();
		List<Continent> continentsList = new ArrayList<>();
		List<Territory> territoriesList = new ArrayList<>();

		int continents, territories;
		try {
			continents = Integer.parseInt(contsNoTF.getText());
			territories = Integer.parseInt(terrsNoTF.getText());
			if (continents == 0 || territories == 0)
				throw new Exception();
		} catch (Exception e) {
			showError("Invalid continents/territories.");
			return;
		}

		Continent tempContinent;
		for (int i = 1; i <= continents; i++) {
			tempContinent = new Continent("Continent " + i);
			continentsSet.add(tempContinent);
			continentsList.add(tempContinent);
		}

		Territory tempTerritory;
		for (int i = 1; i <= territories; i++) {
			tempTerritory = new Territory("Territory " + i);
			territoriesSet.add(tempTerritory);
			territoriesList.add(tempTerritory);
		}

		contsCB.setItems(FXCollections.observableList(continentsList));
		contsCB.setValue(continentsList.get(0));
		contiControlValueTF.setText(String.valueOf(continentsList.get(0).getContinentArmyValue()));
		allTerritoriesCTMapping.setItems(FXCollections.observableList(territoriesList));
		mappedTerritoriesCTMapping.setItems(FXCollections.observableList(continentsList.get(0).getTerritories()));

		terrsCB.setItems(FXCollections.observableList(territoriesList));
		terrsCB.setValue(territoriesList.get(0));
		allTerritoriesTTMapping.setItems(FXCollections.observableList(territoriesList));
		mappedTerritoriesTTMapping
				.setItems(FXCollections.observableList(territoriesList.get(0).getNeighbourTerritories()));
	}

	/**
	 * This method handle {@link MapController#chooseMaps} button event and open a
	 * dialog box to choose a map file to modify.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void chooseMapToModify(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter(".map file", "*.map"));
		File file = chooser.showOpenDialog(null);
		mapPathMapsTF.setText(file == null ? "" : file.getAbsolutePath());
	}

	/**
	 * This method handle {@link MapController#chooseGame} button event and open a
	 * dialog box to choose a map file to play game.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void chooseMapToPlayGame(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter(".map file", "*.map"));
		File file = chooser.showOpenDialog(null);
		mapPathGameTF.setText(file == null ? "" : file.getAbsolutePath());
	}

	/**
	 * This method handle {@link MapController#contsCB} ComboBox value change event
	 * to update related territories on UI.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void updateTerritoriesPerContinent(ActionEvent event) {
		Continent selectedContinent = contsCB.getValue();
		if (selectedContinent == null)
			return;
		mappedTerritoriesCTMapping.setItems(FXCollections.observableList(selectedContinent.getTerritories()));
		contiControlValueTF.setText(String.valueOf(selectedContinent.getContinentArmyValue()));
	}

	/**
	 * This method handle {@link MapController#addTerrsToContsButton} button event
	 * to add selected Territory to selected Continent.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void addTerritoriesToContinent(ActionEvent event) {
		Territory selectedTerritory = allTerritoriesCTMapping.getSelectionModel().getSelectedItem();
		Continent selectedContinent = contsCB.getValue();

		if (selectedTerritory != null && !selectedContinent.getTerritories().contains(selectedTerritory)
				&& selectedTerritory.getContinent() == null) {
			selectedContinent.getTerritories().add(selectedTerritory);
			selectedTerritory.setContinent(selectedContinent);
		} else
			showError("Invalid territory selected.");

		mappedTerritoriesCTMapping.setItems(FXCollections.observableList(selectedContinent.getTerritories()));
	}

	/**
	 * This method handle {@link MapController#removeTerrsToContsButton} button
	 * event to remove selected Territory from selected Continent.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void removeTerritoriesToContinent(ActionEvent event) {
		Territory selectedTerritory = mappedTerritoriesCTMapping.getSelectionModel().getSelectedItem();
		Continent selectedContinent = contsCB.getValue();
		if (selectedTerritory != null) {
			selectedContinent.getTerritories().remove(selectedTerritory);
			selectedTerritory.setContinent(null);
		} else
			showError("Invalid territory selected.");

		mappedTerritoriesCTMapping.setItems(FXCollections.observableList(selectedContinent.getTerritories()));
	}

	/**
	 * This method handle {@link MapController#terrsCB} ComboBox value change event
	 * to update related territories on UI.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void updateTerritoriesPerTerritory(ActionEvent event) {
		Territory selectedTerritory = terrsCB.getValue();
		if (selectedTerritory == null)
			return;
		mappedTerritoriesTTMapping.setItems(FXCollections.observableList(selectedTerritory.getNeighbourTerritories()));
	}

	/**
	 * This method handle {@link MapController#addTerrsToTerrsButton} button event
	 * to add selected Territory to another selected territory in terrsCB as it's
	 * neighbor.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void addTerritoriesToTerritory(ActionEvent event) {
		Territory neighbourTerritory = allTerritoriesTTMapping.getSelectionModel().getSelectedItem();
		Territory selectedTerritory = terrsCB.getValue();

		if (neighbourTerritory != null && !selectedTerritory.getNeighbourTerritories().contains(neighbourTerritory)
				&& !selectedTerritory.equals(neighbourTerritory)) {
			selectedTerritory.getNeighbourTerritories().add(neighbourTerritory);
			if (!neighbourTerritory.getNeighbourTerritories().contains(selectedTerritory)) {
				neighbourTerritory.getNeighbourTerritories().add(selectedTerritory);
			}
		} else
			showError("Invalid territory selected.");
		mappedTerritoriesTTMapping.setItems(FXCollections.observableList(selectedTerritory.getNeighbourTerritories()));
	}

	/**
	 * This method handle {@link MapController#removeTerrsToTerrsButton} button
	 * event to remove selected Territory from selected territory in terrsCB
	 * mapping.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void removeTerritoriesToTerritory(ActionEvent event) {
		Territory neighbourTerritory = mappedTerritoriesTTMapping.getSelectionModel().getSelectedItem();
		Territory selectedTerritory = terrsCB.getValue();
		if (neighbourTerritory != null) {
			selectedTerritory.getNeighbourTerritories().remove(neighbourTerritory);
			neighbourTerritory.getNeighbourTerritories().remove(selectedTerritory);
		} else
			showError("Invalid territory selected.");
		mappedTerritoriesTTMapping.setItems(FXCollections.observableList(selectedTerritory.getNeighbourTerritories()));
	}

	/**
	 * This method handle {@link MapController#updateContiControlValueButton} button
	 * event to update selected continent army value in Continent object in
	 * {@link MapController#continentTerritoriesMapping} mapping.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void updateContiControlValue(ActionEvent event) {
		Continent selectedContinent = contsCB.getValue();
		String contiArmyValue = contiControlValueTF.getText();
		try {
			if (contiArmyValue != null && Integer.parseInt(contiArmyValue) > 0) {
				selectedContinent.setContinentArmyValue(Integer.parseInt(contiArmyValue));
			}
			if (Integer.parseInt(contiArmyValue) <= 0) {
				showError("Enter positive number.");
			}
		} catch (Exception e) {
			showError("Enter valid value.");
		}

	}

	/**
	 * This method handle {@link MapController#saveMapButton} button event. It
	 * validates the mapping and save it to a text file.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void saveMap(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter(".map file", "*.map"));
		File file = fileChooser.showSaveDialog(null);

		if (file != null) {
			List<String> errorList = new ArrayList<String>();
			mapService.validateMap(continentsSet, territoriesSet, errorList);

			if (errorList.size() == 0) {
				if (mapService.saveMap(file, continentsSet, territoriesSet)) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("INFORMATION");
					alert.setHeaderText(null);
					alert.setContentText("Map Saved.");
					alert.showAndWait();
					mappedTerritoriesTTMapping.setItems(null);
					allTerritoriesTTMapping.setItems(null);
					mappedTerritoriesCTMapping.setItems(null);
					allTerritoriesCTMapping.setItems(null);
					terrsCB.setValue(null);
					contsCB.setItems(null);

				} else
					showError("Issue saving map. Try again.");
			} else {
				String errors = "Resolve below errors:";
				for (String error : errorList)
					errors = errors.concat("\n-" + error);
				showError(errors);
			}
		}
	}

	/**
	 * This method handles {@link MapController#modifyMapButton} event. It loads the
	 * map from text file and display to user for modification.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void modifyMap(ActionEvent event) {

		String filePath = mapPathMapsTF.getText();
		if (filePath.trim().length() == 0)
			showError("Choose a file to load map.");
		else {
			File file = new File(filePath);
			/*
			 * try { mapService.parseFile(file); } catch (FileNotFoundException e) {
			 * showError("Unable to find selected File."); return; } catch (IOException e) {
			 * showError("Error reading from file."); return; } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			List<String> errorList = new ArrayList<>();
			mapService.parseFile(file, errorList);
			if (errorList.size() > 0) {
				String errors = "Resolve below errors:";
				for (String error : errorList) {
					errors = errors.concat("\n-" + error);
				}
				showError(errors);
				return;
			} else {

				List<Continent> continentsList = new ArrayList<>();
				List<Territory> territoriesList = new ArrayList<>();
				if (continentsSet != null && continentsSet.size() > 0) {
					for (Continent continent : continentsSet)
						continentsList.add(continent);
				}
				if (territoriesSet != null && territoriesSet.size() > 0) {
					for (Territory territory : territoriesSet)
						territoriesList.add(territory);
				}
				contsCB.setItems(FXCollections.observableList(continentsList));
				contsCB.setValue(continentsList.get(0));
				contiControlValueTF.setText(String.valueOf(continentsList.get(0).getContinentArmyValue()));
				allTerritoriesCTMapping.setItems(FXCollections.observableList(territoriesList));
				mappedTerritoriesCTMapping
						.setItems(FXCollections.observableList(continentsList.get(0).getTerritories()));

				terrsCB.setItems(FXCollections.observableList(territoriesList));
				terrsCB.setValue(territoriesList.get(0));
				allTerritoriesTTMapping.setItems(FXCollections.observableList(territoriesList));
				mappedTerritoriesTTMapping
						.setItems(FXCollections.observableList(territoriesList.get(0).getNeighbourTerritories()));
			}
		}

	}

	/**
	 * This method handles {@link MapController#startGameButton} event. It loads the
	 * chosen map and start the game.
	 * 
	 * @param event:
	 *            ActionEvent instance which is generated by user.
	 */
	@FXML
	public void startGame(ActionEvent event) {

		String filePath = mapPathGameTF.getText();
		if (filePath.trim().length() == 0)
			showError("Choose a map to start game.");
		else {
			File file = new File(filePath);
			/*try {
				mapService.parseFile(file);
			} catch (FileNotFoundException e) {
				showError("Unable to find selected File.");
				return;
			} catch (IOException e) {
				showError("Error reading from file.");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			List<String> errorList = new ArrayList<>();
			mapService.parseFile(file, errorList);
			mapService.validateMap(continentsSet, territoriesSet, errorList);

			if (errorList.size() == 0) {
				Parent root;
				GameController gameController;
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Game_Copy.fxml"));
					root = loader.load();
					gameController = loader.getController();
				} catch (IOException e) {
					showError("Unable to load Game.fxml file.");
					return;
				}
				Stage stage = new Stage();
				stage.setTitle("Risk Game");
				stage.setScene(new Scene(root, 800, 600));
				stage.show();
				gameController.startGame(continentsSet, territoriesSet);

			} else {
				String errors = "Resolve below errors:";
				for (String error : errorList)
					errors = errors.concat("\n-" + error);
				showError(errors);
			}
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
}