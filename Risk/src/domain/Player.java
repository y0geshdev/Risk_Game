package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

/**
 * This entity class represent a Player in game.
 * 
 * @author Yogesh
 *
 */
public class Player implements Serializable{

	/**
	 * It represents player's name.
	 */
	private String name;

	/**
	 * It represents list of territories.
	 */
	private List<Territory> territories;

	/**
	 * Army count which this player holds to place on territories.
	 */
	private int armyCount;

	private IStrategy playingStrategy;

	public Player() {
		armyCount = 0;
		territories = new ArrayList<>();
	}

	/**
	 * default constructor for Player
	 * @param playingStrategy: holds reference to strategy of a player.
	 */
	public Player(IStrategy playingStrategy) {
		armyCount = 0;
		territories = new ArrayList<>();
		this.playingStrategy = playingStrategy;
	}

	/**
	 * Gets the name of this player
	 * 
	 * @return this player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the name for this player
	 * 
	 * @param name:
	 *            This is the new name for this player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * gets the list of territories
	 * 
	 * @return the list of territories player owns
	 */
	public List<Territory> getTerritories() {
		return territories;
	}

	/**
	 * Changes the territories associated with this player
	 * 
	 * @param territories
	 *            This is the list of territories player owns
	 */
	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}

	/**
	 * sets the army count for this player
	 * 
	 * @param armyCount:
	 *            Number of armies that this player owns
	 */
	public void setArmyCount(int armyCount) {
		this.armyCount = armyCount;
	}

	/**
	 * gets the number of armies that this player owns
	 * 
	 * @return the number of armies that player owns
	 */
	public int getArmyCount() {
		return armyCount;
	}

	/**
	 * Overridden toString() method.
	 */
	public String toString() {
		return this.name;
	}

	public IStrategy getPlayingStrategy() {
		return playingStrategy;
	}

	public void setPlayingStrategy(IStrategy playingStrategy) {
		this.playingStrategy = playingStrategy;
	}

	/**
	 * This method have logic to do reinforcement for current player.
	 * 
	 * @param selectedTerritory:
	 *            Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 *            number of armies to add to a territory as reinforcement.
	 * @param phaseViewModel:
	 * 				reference to PhaseViewModel
	 */
	public void reinforcement(Territory selectedTerritory, int numberOfArmies, PhaseViewModel phaseViewModel) {
		playingStrategy.reinforcement(this, selectedTerritory, numberOfArmies, phaseViewModel);
	}

	/**
	 * This method have the logic to fortify one territory from other territory.
	 * 
	 * @param from:
	 *            Territory from which armies to be moved.
	 * @param to:
	 *            Territory to which armies to be moved.
	 * @param armiesToMove:
	 *            Number of armies to move.
	 * @param phaseViewModel:
	 * 				reference to PhaseViewModel
	 */
	public void fortify(Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		playingStrategy.fortify(this, from, to, armiesToMove,phaseViewModel);
	}

	/**
	 * This method perform attack from attacker to defender territory.
	 * 
	 * @param attackerTerritory:
	 *            Territory from which attack is performed.
	 * @param defenderTerritory:
	 *            Territory to which attack is performed.
	 * @param defender:
	 *            Owner of defenderTerritory.
	 * @param isAllOutMode:
	 *            true if current attack is of All-Out mode else false.
	 * @param totalAttackerDice:
	 *            Number of dice to roll for attacker if current mode of attack is
	 *            normal mode.
	 * @param totalDefenderDice:
	 *            Number of dice to roll for defender if current mode of attack is
	 *            normal mode.
	 * @param phaseViewModel:
	 *            PhaseViewModel instance to update information on phase view with
	 *            each step of attack.
	 * @return A {@link Pair} class which hold data as Boolean and Integer
	 *         representing attack outcome and minimum troops to move.
	 */
	public Pair<Boolean, Integer> attack(Player defender, Territory attackerTerritory, Territory defenderTerritory,
			boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice, PhaseViewModel phaseViewModel) {
		return playingStrategy.attack(this, defender, attackerTerritory, defenderTerritory, isAllOutMode, totalAttackerDice,
				totalDefenderDice, phaseViewModel);
	}

}
