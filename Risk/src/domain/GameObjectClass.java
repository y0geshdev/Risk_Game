package domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

/**
 * This class is contains all the objects required for storing game state at any
 * consistent state
 * 
 * @author Kunal
 *
 */
public class GameObjectClass implements Serializable {

	private static final long serialVersionUID = 1L;
	HashSet<Continent> continentSet;
	HashSet<Territory> territorySet;
	List<Player> playerList;
	Player currentPlayer;
	String currentPhase;
	boolean ifStartUpIsComepleted;
		
	//constructor
	public GameObjectClass(HashSet<Continent> continentSet, HashSet<Territory> territorySet, List<Player> playerList,
			Player currentPlayer,String currentPhase,boolean ifStartUpIsComepleted) {
		super();
		this.continentSet = continentSet;
		this.territorySet = territorySet;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.currentPhase	=	currentPhase;
		this.ifStartUpIsComepleted	=	ifStartUpIsComepleted;
		
	}
	/**
	 * Getter for serial version ID
	 * 
	 * @return the serial version ID
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * Getter for Continent Set
	 * 
	 * @return the set containing continents
	 */
	public HashSet<Continent> getContinentSet() {
		return continentSet;
	}

	/**
	 * Getter for Territory Set
	 * 
	 * @return the set containing territories
	 */
	public HashSet<Territory> getTerritorySet() {
		return territorySet;
	}

	/**
	 * Getter for Player list
	 * 
	 * @return the list of players
	 */
	public List<Player> getPlayerList() {
		return playerList;
	}

	/**
	 * Getter for Current player
	 * 
	 * @return the  player currently playing the game
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Getter for Current phase
	 * 
	 * @return current phase as a string
	 */
	public String getCurrentPhase() {
		return currentPhase;
	}
	
	/**
	 * Getter for IfStartUpIsComepleted boolean which is true if start up phase is completed
	 * 
	 * @return IfStartUpIsComepleted as a boolean parameter
	 */
	public boolean getIfStartUpIsComepleted() {
		return ifStartUpIsComepleted;
	}
	
	/**
	 * Setter for IfStartUpIsComepleted boolean parameter
	 * 
	 * @param ifStartUpIsComepleted:
	 * 				boolean parameter which is true if start up phase is completed	
	 */
	public void setIfStartUpIsComepleted(boolean ifStartUpIsComepleted) {
		this.ifStartUpIsComepleted = ifStartUpIsComepleted;
	}

}