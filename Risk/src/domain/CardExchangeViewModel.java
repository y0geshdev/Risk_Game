package domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;

import controller.MapController;

public class CardExchangeViewModel extends Observable {

	/**
	 * Represents the current player object.
	 */
	Player currentPlayer;

	/**
	 * Represents the card object.
	 */
	Card card;

	/**
	 * Represents the mapping of the player and the cards he owned.
	 */
	Map<Player, Queue<Card>> playerToCardMapping;

	/**
	 * Represents the total number of cards.
	 */
	Queue<Card> allCards;

	/**
	 * Represents the total Number of exchanges done in the game.
	 */
	int totalNumberOfExchanges;

	/**
	 * Represents if the player is entitled to get a card or not after the attack
	 * phase.
	 */
	boolean ifPlayerGetsCard;

	/**
	 * Represents the Infantry String constant
	 */
	public static final String INFANTRY_ARMY = "Infantry";

	/**
	 * Represents the Cavalry String constant
	 */
	public static final String CAVALRY_ARMY = "Cavalry";

	/**
	 * Represents the Artillery String constant
	 */
	public static final String ARTILLERY_ARMY = "Artillery";

	/**
	 * Represents the common territory object of the territory owned by the player
	 * and card exchanged by the player.
	 */
	private Territory cardAndOwnedTerritory;

	/**
	 * default constructor for CardExchangeViewModel.
	 */
	public CardExchangeViewModel() {
		playerToCardMapping = new HashMap<>();
		ifPlayerGetsCard = false;
		totalNumberOfExchanges = 0;
		allCards = new LinkedList<>();
		cardAndOwnedTerritory = null;
		// this initialize the total number of cards to be used in game.
		setCards();
	}

	/**
	 * This method returns the type of a particular card.
	 * 
	 * @return
	 */
	public String getCardType() {
		return card.cardType;
	}

	/**
	 * This method returns the territory of the card.
	 * 
	 * @return
	 */
	public Territory getCardTerritory() {
		return card.cardTerritory;
	}

	/**
	 * This method returns the current player for whom the view is to be set.
	 * 
	 * @return
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * This method sets and update the view for the given player.
	 * 
	 * @param currentPlayer
	 *            : player for whom the view is to be set.
	 */
	public void setViewForCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
		
		//notify Observer
		setChanged();
		notifyObservers(this);
	}

	/**
	 * This method sets the initials deck of cards.
	 * 
	 * @param allCards
	 *            : card objects having territory and card type values.
	 */
	public void setAllCards(Queue<Card> allCards) {
		this.allCards = allCards;
	}

	/**
	 * This method returns the whole deck of cards.
	 * 
	 * @return
	 */
	public Queue<Card> getAllCards() {
		return allCards;
	}

	/**
	 * This method returns the cards owned by the given player.
	 * 
	 * @param currentPlayer
	 *            : player for whose owned cards are required.
	 * @return
	 */
	public Queue<Card> getCurrentPlayerCards(Player currentPlayer) {
		Queue<Card> playerCards = playerToCardMapping.get(currentPlayer);
		if (playerCards != null) {
			return playerCards;
		} else {
			return new LinkedList<>();
		}
	}

	/**
	 * This method sets the cards for the player.
	 * 
	 * @param currentPlayer
	 *            : player for whom cards are set.
	 * @param currentPlayerCards
	 *            : player owned cards queue
	 */
	public void setCurrentPlayerCards(Player currentPlayer, Queue<Card> currentPlayerCards) {
		playerToCardMapping.put(currentPlayer, currentPlayerCards);
	}

	/**
	 * This method set the total Number of Exchanges that have happened in the whole
	 * game.
	 * 
	 * @param totalNumberOfExchanges
	 */
	public void setTotalNumberOfExchanges(int totalNumberOfExchanges) {
		this.totalNumberOfExchanges = totalNumberOfExchanges;
	}

	/**
	 * This method returns the total Number of Exchanges that have happened in the
	 * whole game.
	 * 
	 * @return
	 */
	public int getTotalNumberOfExchanges() {
		return totalNumberOfExchanges;
	}

	/**
	 * This method sets the army of the player who has exchanged the cards
	 * 
	 * @param currentPlayer
	 *            : player whose army is being set.
	 * @param cardOfPlayerTerritoryExchanged
	 *            : number is 2 if the any of the exchanged card was of the
	 *            territory owned by the player else 0
	 */
	public void setPlayerArmyCount(Player currentPlayer, int cardOfPlayerTerritoryExchanged,
			Territory cardAndOwnedTerritory) {
		// gets the army count for the player based on the total number of exchanges
		// that has happened in the game.
		int armyCount = getArmyCount();
		armyCount += cardOfPlayerTerritoryExchanged;
		if (totalNumberOfExchanges <= 6) {
			currentPlayer.setArmyCount(currentPlayer.getArmyCount() + armyCount);
		} else {
			for (int i = 1; i <= totalNumberOfExchanges - 6; i++) {
				armyCount += 5;
			}
			currentPlayer.setArmyCount(currentPlayer.getArmyCount() + armyCount);

		}
	}

	/**
	 * This method returns the card and territory owned which is associated with the
	 * card.
	 * 
	 * @return
	 */
	public Territory getCardAndOwnedTerritory() {
		return cardAndOwnedTerritory;
	}

	/**
	 * This method sets the card and territory owned which is associated with the
	 * card by taking into consideration a territory object.
	 * 
	 * @param cardAndOwnedTerritory
	 */
	public void setCardAndOwnedTerritory(Territory cardAndOwnedTerritory) {
		this.cardAndOwnedTerritory = cardAndOwnedTerritory;
	}

	/**
	 * This method returns the valid number of armies based on the totalNumber of
	 * exchanges that have happened during the game as per Risk Rules
	 * 
	 * @return
	 */
	public int getArmyCount() {

		switch (totalNumberOfExchanges) {

		case 1:
			return 4;

		case 2:
			return 6;

		case 3:
			return 8;

		case 4:
			return 10;

		case 5:
			return 12;

		default:
			return 15;

		}
	}

	/**
	 * This method returns if the player is eligible to get the card or not.
	 * 
	 * @return
	 */
	public boolean getIfPlayerGetsCard() {
		return ifPlayerGetsCard;
	}

	/**
	 * This method sets the boolean variable true if the player is eligible to get
	 * the card and false if not. Player is eligible if he has won at lease one
	 * territory during the attack phase.
	 * 
	 * @param ifPossible
	 *            : value to be set for the player's eligibility for winning a card
	 */
	public void setIfPlayerGetsCard(boolean ifPossible) {
		ifPlayerGetsCard = ifPossible;
	}

	/**
	 * This method sets the initial deck of the cards as per the number of
	 * territories.
	 */
	public void setCards() {
		String[] cardtype = { INFANTRY_ARMY, CAVALRY_ARMY, ARTILLERY_ARMY };
		Queue<Card> allCards = new LinkedList<>();

		Iterator<Territory> ite = MapController.territoriesSet.iterator();
		int index = 0;
		while (ite.hasNext()) {
			Territory cardTerritory = ite.next();
			if (index == 3) {
				index = 0;
			}
			String cardType = cardtype[index];
			Card card = new Card(cardType, cardTerritory);
			allCards.add(card);
			index++;
		}
		// Set the initial deck of cards.
		setAllCards(allCards);
	}

	/**
	 * This method is used to give the top card of the deck to the player who has
	 * won at least one territory during his attack phase
	 * 
	 * @param attacker
	 */
	public void assignCardToAPlayer(Player attacker) {
		Queue<Card> allCards = getAllCards();
		Queue<Card> playerCards = getCurrentPlayerCards(attacker);
		Card topCard = allCards.remove();
		if (!playerCards.isEmpty()) {
			playerCards.add(topCard);
		} else {
			playerCards.add(topCard);
		}
		// sets the card for the current player.
		setCurrentPlayerCards(attacker, playerCards);
	}

}
