package domain;

import java.util.Observable;

/**
 * This class extends the Observable class and keeps a track of all the
 * Observers.
 * 
 * @author karanbhalla
 *
 */
public class PhaseViewModel extends Observable {

	/**
	 * Representing current phase going on in game.
	 */
	private String currentPhase;

	/**
	 * Holds reference to player currently playing game.
	 */
	private String currentPlayer;

	/**
	 * Holds information regarding what is going on in this phase.
	 */
	private String phaseInfo;

	/**
	 * Gets the name of the current phase.
	 * 
	 * @return the currentPhase of the player.
	 */
	public String getCurrentPhase() {
		return currentPhase;
	}

	/**
	 * Gets the name of current player.
	 * 
	 * @return the current player's name.
	 */
	public String getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Gets the information about the current phase.
	 * 
	 * @return the phase info.
	 */
	public String getPhaseInfo() {
		return phaseInfo;
	}

	/**
	 * Setter for currentPhase.
	 * 
	 * @param currentPhase:
	 *            String representing current phase.
	 */
	public void setCurrentPhase(String currentPhase) {
		this.currentPhase = currentPhase;
		notifyObserver();
	}

	/**
	 * Setter for currentPlayer.
	 * 
	 * @param currentPlayer:
	 *            String representing current player.
	 */
	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
		notifyObserver();
	}

	/**
	 * Setter for phaseInfo.
	 * 
	 * @param phaseInfo:
	 *            String representing phase information.
	 */
	public void setPhaseInfo(String phaseInfo) {
		this.phaseInfo = phaseInfo;
		notifyObserver();
	}

	/**
	 * Method to notify observers attached to this observable.
	 */
	private void notifyObserver() {
		setChanged();
		notifyObservers(this);
	}
}
