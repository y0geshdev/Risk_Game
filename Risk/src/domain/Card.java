package domain;

/**
 * This class is an entity class for cards
 * @author Shivam
 *
 */
public class Card {
	
	/**
	 * Represents the type of card.
	 */
	String cardType;
	
	/**
	 * Represents the territory of the card. 
	 */
	Territory cardTerritory;

	/**
	 * Constructor for the Card Class.
	 * 
	 * @param cardType
	 *            : Type of card.
	 * @param cardTerritory
	 *            : territory of the card.
	 */
	public Card(String cardType, Territory cardTerritory) {
		this.cardType = cardType;
		this.cardTerritory = cardTerritory;
	}

	/**
	 * This method returns the type of card.
	 * 
	 * @return type of card
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * This method sets the card type entered by the user.
	 * 
	 * @param cardType
	 *            : Type of card
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * This method returns the territory of the card.
	 * 
	 * @return territory for the card
	 */
	public Territory getCardTerritory() {
		return cardTerritory;
	}

	/**
	 * This method sets the territory of the card.
	 * 
	 * @param cardTerritory
	 *            : Territory of the card.
	 */
	public void setCardTerritory(Territory cardTerritory) {
		this.cardTerritory = cardTerritory;
	}
	
	/**
	 * Overridden toString() method.
	 */
	@Override
	public String toString() {
		// returns the type and territory name of the card.
		return cardType+" "+cardTerritory.getName();
	}
}

