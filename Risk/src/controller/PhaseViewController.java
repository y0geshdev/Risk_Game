package controller;

import java.util.Observable;
import java.util.Observer;

import domain.PhaseViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
/**
 * This is a controller class for Phase View.
 * @author Yogesh
 *
 */
public class PhaseViewController implements Observer {

	@FXML
    private Label playerNameLabel;

    @FXML
    private Label phaseNameLabel;

    @FXML
    private TextArea phaseInfoTF;
	
	@Override
	public void update(Observable o, Object arg) {
		PhaseViewModel model = (PhaseViewModel) o;
		playerNameLabel.setText(model.getCurrentPlayer());
		phaseNameLabel.setText(model.getCurrentPhase());
		phaseInfoTF.setText(model.getPhaseInfo());
	}

}
