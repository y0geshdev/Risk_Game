package domain;

/**
 * An enum which reflects strategies for players playing style.
 * 
 * @author Yogesh
 *
 */
public enum PlayerStrategyEnum {
	HUMAN, AGGRESSIVE, BENEVOLENT, RANDOM, CHEATER;

	@Override
	public String toString() {

		switch (this) {
		case HUMAN:
			return "Human";
		case AGGRESSIVE:
			return "Aggressive";
		case BENEVOLENT:
			return "Benevolent";
		case RANDOM:
			return "Random";
		case CHEATER:
			return "Cheater";
		default:
			return "None";
		}

	}

}
