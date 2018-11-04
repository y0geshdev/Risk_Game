package domain;

import java.util.Observable;
/**
 * This class extends the Observable class and keeps a track of all the Observers.
 * @author karanbhalla
 *
 */
public class PhaseViewModel extends Observable {

	private String currentPhase;
	private String currentPlayer;
	private String phaseInfo;
	/**
	 * Gets the name of the current phase.
	 * @return the currentPhase of the player.
	 */
	public String getCurrentPhase() {
		return currentPhase;
	}
	/**
	 * Gets the name of current player.
	 * @return the current player's name.
	 */
	public String getCurrentPlayer() {
		return currentPlayer;
	}
	/**
	 * Gets the information about the current phase.
	 * @return the phase info.
	 */
	public String getPhaseInfo() {
		return phaseInfo;
	}
	public void setCurrentPhase(String currentPhase) {
		this.currentPhase = currentPhase;
		notifyObserver();
	}
	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
		notifyObserver();
	}
	public void setPhaseInfo(String phaseInfo) {
		this.phaseInfo = phaseInfo;
		notifyObserver();
	}
	
	private void notifyObserver() {
		setChanged();
		notifyObservers(this);
	}
}
