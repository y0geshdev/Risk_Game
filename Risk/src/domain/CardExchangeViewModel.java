package domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;

import controller.MapController;

public class CardExchangeViewModel extends Observable {

	Player currentPlayer;
	Card card;
	Map<Player, Queue<Card>> playerToCardMapping;
	Queue<Card> allCards;
	int totalNumberOfExchanges;
	boolean ifPlayerGetsCard;
	public static final String INFANTRY_ARMY = "Infantry";
	public static final String CAVALRY_ARMY = "Cavalry";
	public static final String ARTILLERY_ARMY = "Artillery";
	private Territory cardAndOwnedTerritory;
	
	/**	
	 * default constructor for CardExchangeViewModel.
	 */
	public CardExchangeViewModel() {
		playerToCardMapping = new HashMap<>();
		ifPlayerGetsCard = false;
		totalNumberOfExchanges = 0;
		allCards = new LinkedList<>();
		cardAndOwnedTerritory	=	null;
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
	 *            : number is 2 if the any of the excahnged card was of the
	 *            territory owned by the player else 0
	 */
	public void setPlayerArmyCount(Player currentPlayer, int cardOfPlayerTerritoryExchanged,Territory cardAndOwnedTerritory) {
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

	public Territory getCardAndOwnedTerritory() {
		return cardAndOwnedTerritory;
	}
	
	public void setCardAndOwnedTerritory(Territory cardAndOwnedTerritory) {
		this.cardAndOwnedTerritory=cardAndOwnedTerritory;
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
		String[] cardtype = { INFANTRY_ARMY, CAVALRY_ARMY,ARTILLERY_ARMY };
		Queue<Card> allCards = new LinkedList<>();

		Iterator<Territory> ite = MapController.territoriesSet.iterator();
		while (ite.hasNext()) {
			Territory cardTerritory = ite.next();
			int randIndx = randomIndex(0, 2);
			String cardType = cardtype[randIndx];
			Card card = new Card(cardType, cardTerritory);
			allCards.add(card);
		}
		setAllCards(allCards);
	}
	
	 /** This method is used to give the top card of the deck to the player who has
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
		setCurrentPlayerCards(attacker, playerCards);
	}


	/** This method is used to get a random number which will be between 0 and size
	 * of list of territories.
	 * 
	 * @param min
	 *            : lower range of the random number generated
	 * @param max
	 *            : upper range of the random number generated.
	 * 
	 * @return : returns the random number generated between the range.
	 * 
	 */
	private int randomIndex(int min, int max) {
		Random randIndex = new Random();
		return randIndex.nextInt((max - min) + 1) + min;
	}

}
