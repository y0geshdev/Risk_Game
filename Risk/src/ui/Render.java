package ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Continent;
import domain.Territory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Render extends Application{

	private static Set<Continent>continentSetObj;
	private static Set<Territory>territorySetObj;
	private static Map<String, TextField> idTotextFieldMapping=new HashMap<>();
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Risk Game");
		GridPane rootPane	=	new GridPane();
		rootPane.setPadding(new Insets(20, 20, 20, 20));
		rootPane.setVgap(8);
		rootPane.setHgap(10);
		TextField numberOfPlayerCount	=	new TextField();
		Label 	  numberOfPlayer		=	new Label();
		Button   startGameButton		=	new Button();
		
		
		GridPane.setConstraints(numberOfPlayer, 0, 0);
		GridPane.setConstraints(numberOfPlayerCount, 1, 0);
		GridPane.setConstraints(startGameButton, 2, 0);
		
		numberOfPlayer.setText("Number Of Players");
		numberOfPlayerCount.setPromptText("Count of Players");
		startGameButton.setText("Start");
		startGameButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				formMap(rootPane);
				
			}
		});
		rootPane.getChildren().addAll(numberOfPlayer,numberOfPlayerCount,startGameButton);
//		generateMapping();
		primaryStage.setScene(new Scene(rootPane,800,400));
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	private static void formMap(GridPane pane) {

		Iterator<Continent> ite = continentSetObj.iterator();
		int colCounter = 0;
		while (ite.hasNext()) {
			Continent obj = ite.next();
			String nameofTheContinent = obj.getName();
			List<Territory> territoryList = obj.getTerritories();
			Integer controlValue	=	obj.getContinentArmyValue();
			Label continentName = new Label(nameofTheContinent);
			Label controlValueLabel = new Label("Control Value: "+controlValue.toString());
			pane.setConstraints(continentName, colCounter, 1);
			pane.setConstraints(controlValueLabel, colCounter, 2);
			pane.getChildren().addAll(continentName,controlValueLabel);
			for (int i = 0; i < territoryList.size(); i++) {
				Territory territoryObj = territoryList.get(i);
				TextField territoryField = new TextField();
				pane.setConstraints(territoryField, colCounter, i + 3);
//				territoryField.setDisable(true);
				territoryField.setPromptText(territoryObj.getName());
//				territoryField.setId(territoryObj.getName());
//				territoryField.setOnAction(new EventHandler<ActionEvent>() {
//
//					@Override
//					public void handle(ActionEvent event) {
//						highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories());
//						
//					}
//				});
//				territoryField.setOnKeyPressed(new EventHandler<KeyEvent>() {
//
//					@Override
//					public void handle(KeyEvent event) {
//						// TODO Auto-generated method stub
//						highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories());
//					}
//					
//				});
				
				territoryField.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if(event.getClickCount()==2)
						highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories(),territoryField,event.getClickCount());
						highlightNeighbouringTerritoriesHelper(territoryObj.getNeighbourTerritories(), territoryField, event.getClickCount());
					}
				});
				
				idTotextFieldMapping.put(territoryObj.getName(), territoryField);
				pane.getChildren().add(territoryField);
			}
			colCounter++;
		}

	}
	
	private static void generateMapping() {
		
		Iterator<Territory> ite	=	territorySetObj.iterator();
		while(ite.hasNext()) {
			Territory territoryObject	=	ite.next();
			String territoryName	=	territoryObject.getName();
			List<Territory> neighbouringTerritories	=	territoryObject.getNeighbourTerritories();
			TextField tf	=	idTotextFieldMapping.get(territoryName);

		}
	}
	
	private static void highlightNeighbouringTerritoriesHelper(List<Territory> neigbouringCountries,TextField mainTf,int clickCount) {

		
		for (int i = 0; i < neigbouringCountries.size(); i++) {
			Territory t	=	neigbouringCountries.get(i);
			TextField tf	=	idTotextFieldMapping.get(t.getName());
			if(clickCount==2) {
			tf.setStyle("-fx-text-box-border: red;");
			}else {
				tf.setStyle("");	
			}
		}
		
	}
	
	public void startGame(Set<Continent>continentObjectSet,Set<Territory>territoryObjectSet) {
		String [] args	=	new String[2];
		continentSetObj	=	continentObjectSet;
		territorySetObj	=	territoryObjectSet;
		
		main(args);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	
}
