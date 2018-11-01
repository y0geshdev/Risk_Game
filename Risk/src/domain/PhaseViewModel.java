package domain;

import java.util.Observable;

public class PhaseViewModel extends Observable {

	private String currentPhase;
	private String currentPlayer;
	private String phaseInfo;
	
	public String getCurrentPhase() {
		return currentPhase;
	}
	public String getCurrentPlayer() {
		return currentPlayer;
	}
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
