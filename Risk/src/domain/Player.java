package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * This entity class represent a Player in game.
 * 
 * @author Yogesh
 *
 */
public class Player {
	private String name;
	private List<Territory> territories;
	private int armyCount;

	/**
	 * default constructor for Player
	 */
	public Player() {
		armyCount = 0;
		territories = new ArrayList<>();
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

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * This method have logic to do reinforcement for current player.
	 * @param selectedTerritory:
	 * 							Territory to which armies is to be added for reinforcement.
	 * @param numberOfArmies:
	 * 						number of armies to add to a territory as reinforcement.
	 */
	public void reinforcement(Territory selectedTerritory, int numberOfArmies) {
		selectedTerritory.setArmyCount(selectedTerritory.getArmyCount() + numberOfArmies);
		this.setArmyCount(this.getArmyCount() - numberOfArmies);
	}

	/**
	 * This method have the logic to fortify one territory from other territory.
	 * @param from:
	 * 			Territoty from which armies to be moved.
	 * @param to:
	 * 			Territory to which armies to be moved.
	 * @param armiesToMove:
	 * 					Number of armies to move.
	 */
	public void fortify(Territory from, Territory to, int armiesToMove) {
		from.setArmyCount(from.getArmyCount() - armiesToMove);
		to.setArmyCount(to.getArmyCount() + armiesToMove);
	}

}
