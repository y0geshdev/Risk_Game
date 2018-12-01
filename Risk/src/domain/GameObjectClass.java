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

	/**
	 * serialUId for the class.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * variable to hold reference of the continent set.
	 */
	HashSet<Continent> continentSet;
	/**
	 * variable to hold reference of the territory set.
	 */
	HashSet<Territory> territorySet;
	/**
	 * variable to hold reference of the playerList.
	 */
	List<Player> playerList;
	/**
	 * variable to hold reference of the current player.
	 */
	Player currentPlayer;
	/**
	 * variable to hold reference of the current phase of the game.
	 */
	String currentPhase;
	/**
	 * variable to hold reference of the if the startUp phase is completed or not.
	 */
	boolean ifStartUpIsComepleted;

	/**
	 * variable to hold reference of the CardExchangeViewModel.
	 */
	CardExchangeViewModel cardExchangeViewModel;

	/**
	 * Class constructor
	 * 
	 * @param continentSet
	 *            : parameter of the continent set.
	 * @param territorySet:
	 *            parameter of the territory set.
	 * @param playerList:
	 *            parameter of the player List.
	 * @param currentPlayer:
	 *            parameter of the current player.
	 * @param currentPhase:
	 *            parameter of the current phase.
	 * @param ifStartUpIsComepleted:
	 *            parameter of the if the startup phase is completed or not.
	 * @param cardExchangeViewModel:
	 *            parameter of the cardExchangeView model. .           
	 */
	public GameObjectClass(HashSet<Continent> continentSet, HashSet<Territory> territorySet, List<Player> playerList,
			Player currentPlayer, String currentPhase, boolean ifStartUpIsComepleted,
			CardExchangeViewModel cardExchangeViewModel) {
		super();
		this.continentSet = continentSet;
		this.territorySet = territorySet;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.currentPhase = currentPhase;
		this.ifStartUpIsComepleted = ifStartUpIsComepleted;
		this.cardExchangeViewModel = cardExchangeViewModel;
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
	 * @return the player currently playing the game
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public CardExchangeViewModel getCardExchangeViewModel() {
		return cardExchangeViewModel;
	}

	public void setCardExchangeViewModel(CardExchangeViewModel cardExchangeViewModel) {
		this.cardExchangeViewModel = cardExchangeViewModel;
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
	 * Getter for IfStartUpIsComepleted boolean which is true if start up phase is
	 * completed
	 * 
	 * @return IfStartUpIsComepleted as a boolean parameter
	 */
	public boolean getIfStartUpIsComepleted() {
		return ifStartUpIsComepleted;
	}

}