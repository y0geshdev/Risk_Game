package domain;

public class Card {

	String cardType;
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
	 * @return
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
	 * @return
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return cardType+" "+cardTerritory.getName();
	}
}

