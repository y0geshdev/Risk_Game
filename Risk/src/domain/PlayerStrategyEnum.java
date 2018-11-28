package domain;

import java.io.Serializable;

/**
 * An enum which reflects strategies for players playing style.
 * 
 * @author Yogesh
 *
 */
public enum PlayerStrategyEnum implements Serializable{
	HUMAN, AGGRESSIVE, BENEVOLENT, RANDOM, CHEATER;
}
