package controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import domain.Continent;
import domain.Player;
import domain.WorldDominationModel;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
/**
 * This class is a controller class for WorldDominationView.
 * @author Yogesh
 *
 */
public class WorldDominationViewController implements Observer {

	@FXML
	private GridPane gridPane;

	private Map<Player, Label> playerToPercentageLabelMap;
	private Map<Player, TextArea> playerToContinentsTAMap;
	private Map<Player, Label> playerToArmiesLabelMap;
	private List<Player> playersList;

	@Override
	public void update(Observable arg0, Object arg1) {
		WorldDominationModel model = (WorldDominationModel) arg0;
		Map<Player, Integer> playerArmiesMapping = model.getPlayerArmiesMapping();
		Map<Player,Set<Continent>> playerContinentsMapping = model.getPlayerContinentsMapping();
		Map<Player,Double> playerMapCoverageMapping = model.getPlayerMapCoverageMapping();
		
		for(Player player : playersList) {
			playerToPercentageLabelMap.get(player).setText(String.format("%.2f", playerMapCoverageMapping.get(player))); //String.valueOf(playerMapCoverageMapping.get(player))
			playerToArmiesLabelMap.get(player).setText(String.valueOf(playerArmiesMapping.get(player)));
			String continents = "";
			for(Continent continent : playerContinentsMapping.get(player)) {
				continents = continents.concat(continent.getName()+" ");
			}
			playerToContinentsTAMap.get(player).setText(continents);
		}

	}

	public void setUpWorldDominationView(List<Player> playersList) {
		playerToPercentageLabelMap = new LinkedHashMap<>();
		playerToContinentsTAMap = new LinkedHashMap<>();
		playerToArmiesLabelMap = new LinkedHashMap<>();
		this.playersList = playersList;

		int rowCounter = 0;
		ColumnConstraints widthCol = new ColumnConstraints(80);
		widthCol.setHgrow(Priority.ALWAYS);
		gridPane.getColumnConstraints().add(widthCol);
		gridPane.getColumnConstraints().add(widthCol);
		gridPane.getColumnConstraints().add(widthCol);
		RowConstraints heightRow = new RowConstraints(35);

		// setting player details
		Label playerHeader = new Label("Player Name");
		playerHeader.setUnderline(true);
		playerHeader.setAlignment(Pos.CENTER);

		// setting percentage map controlled by each player view
		Label mapControlledHeader = new Label("Map Covered");
		mapControlledHeader.setUnderline(true);
		mapControlledHeader.setAlignment(Pos.CENTER);

		// setting continents controlled by each player view
		Label continentControlledHeader = new Label("Continents Owned");
		continentControlledHeader.setUnderline(true);
		continentControlledHeader.setAlignment(Pos.CENTER);

		// setting total armies for each player view
		Label armiesPerPlayerHeader = new Label("Armies Owned");
		armiesPerPlayerHeader.setUnderline(true);
		armiesPerPlayerHeader.setAlignment(Pos.CENTER);

		gridPane.getRowConstraints().add(heightRow);
		GridPane.setConstraints(playerHeader, 0, rowCounter);
		GridPane.setConstraints(mapControlledHeader, 1, rowCounter);
		GridPane.setConstraints(armiesPerPlayerHeader, 2, rowCounter);
		GridPane.setConstraints(continentControlledHeader, 3, rowCounter);

		GridPane.setHalignment(playerHeader, HPos.CENTER);
		GridPane.setHalignment(mapControlledHeader, HPos.CENTER);
		GridPane.setHalignment(continentControlledHeader, HPos.CENTER);
		GridPane.setHalignment(armiesPerPlayerHeader, HPos.CENTER);

		gridPane.getChildren().addAll(playerHeader, mapControlledHeader, continentControlledHeader,
				armiesPerPlayerHeader);

		rowCounter++;

		Label playerNameLabel, mapPercentageLabel, armiesCountLabel;
		TextArea controlledContinentsTA;
		for (Player player : playersList) {
			playerNameLabel = new Label(player.getName());
			mapPercentageLabel = new Label(player.getName());
			armiesCountLabel = new Label(player.getName());
			controlledContinentsTA = new TextArea(player.getName());

			playerToArmiesLabelMap.put(player, armiesCountLabel);
			playerToContinentsTAMap.put(player, controlledContinentsTA);
			playerToPercentageLabelMap.put(player, mapPercentageLabel);

			playerNameLabel.setAlignment(Pos.CENTER);
			mapPercentageLabel.setAlignment(Pos.CENTER);
			controlledContinentsTA.setEditable(false);
			controlledContinentsTA.setMaxSize(150, 35);
			armiesCountLabel.setAlignment(Pos.CENTER);

			gridPane.getRowConstraints().add(heightRow);
			GridPane.setConstraints(playerNameLabel, 0, rowCounter);
			GridPane.setConstraints(mapPercentageLabel, 1, rowCounter);
			GridPane.setConstraints(armiesCountLabel, 2, rowCounter);
			GridPane.setConstraints(controlledContinentsTA, 3, rowCounter);

			GridPane.setHalignment(playerNameLabel, HPos.CENTER);
			GridPane.setHalignment(mapPercentageLabel, HPos.CENTER);
			GridPane.setHalignment(armiesCountLabel, HPos.CENTER);
			GridPane.setHalignment(controlledContinentsTA, HPos.CENTER);

			gridPane.getChildren().addAll(playerNameLabel, mapPercentageLabel, controlledContinentsTA,
					armiesCountLabel);

			rowCounter++;
		}

	}

}
