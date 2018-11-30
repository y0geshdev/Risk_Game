package controller;

import java.util.Observable;
import java.util.Observer;

import domain.PhaseViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * This is a controller class for Phase View.
 * 
 * @author Yogesh
 *
 */
public class PhaseViewController implements Observer {

	/**
	 * Represents label for player's name.
	 */
	@FXML
	private Label playerNameLabel;

	/**
	 * Represents label for phase name.
	 */
	@FXML
	private Label phaseNameLabel;

	/**
	 * Represents textarea for phase related information.
	 */
	@FXML
	private TextArea phaseInfoTF;

	/**
	 * Overridden method of Observer's update(Observable, Object) to update
	 * view according to updated to state of Observable object passed as parameter.
	 */
	@Override
	public void update(Observable o, Object arg) {
		PhaseViewModel model = (PhaseViewModel) o;
		playerNameLabel.setText(model.getCurrentPlayer());
		phaseNameLabel.setText(model.getCurrentPhase());
		phaseInfoTF.setText(model.getPhaseInfo());
	}

}
