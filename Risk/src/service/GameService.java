package service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.GameController;
import controller.MapController;
import domain.Continent;
import domain.GameConstants;
import domain.Territory;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * This class handle all the service call from {@link GameController} class and
 * provide business logic for same.
 * 
 * @author Yogesh
 *
 */
public class GameService {

	private static Map<String, TextField> idTotextFieldMapping = new HashMap<>();
	private static Set<Continent> continentSetObj;
	private static Set<Territory> territorySetObj;
	
	static {
		continentSetObj	=	MapController.continentsSet;
		territorySetObj	=	MapController.territoriesSet;
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
	public static void formMap(GridPane pane) {

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

	
}
