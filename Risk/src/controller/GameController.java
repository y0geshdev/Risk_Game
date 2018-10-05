package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.GameService;

/**
 * GameController class have all the methods which handle different actions
 * which are performed by user while playing game.
 *  
 * @author Yogesh
 *
 */
public class GameController {

	
	@FXML
	GridPane gamePane;
	
	@FXML
	Label numberOfPlayers;
	
	@FXML
	TextField countOfPlayers;
	
	@FXML
	Button renderGame;
	
	
	@FXML
	public void startGameHandler(ActionEvent event) {
		GameService serviceObject	=	new GameService();
		serviceObject.formMap(gamePane);
	}
	
}
