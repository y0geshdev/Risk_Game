package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity class represent a Territory in game.
 * 
 * @author Yogesh
 *
 */
public class Territory  implements Serializable{

	/**
	 * Represents name of the territory.
	 */
	private String name;
	
	/**
	 * Represents neighboring territory list.
	 */
	private List<Territory> neighbourTerritories;
	
	/**
	 * Continent to which this territory belongs.
	 */
	private Continent continent;
	
	/**
	 * Owner of this territory.
	 */
	private Player owner;
	
	/**
	 * Army count in this territory.
	 */
	private int armyCount;

	/**
	 * default constructor for territory
	 */
	public Territory() {
		this.neighbourTerritories = new ArrayList<>();
		armyCount = 0;
	}

	/**
	 * Creates a territory with given name and associated continent
	 * 
	 * @param name:
	 *            Name for the new territory
	 * @param continent:
	 *            object of the associated continent
	 */
	public Territory(String name, Continent continent) {
		this.name = name;
		this.continent = continent;
		this.neighbourTerritories = new ArrayList<>();
		armyCount = 0;
	}

	/**
	 * Creates a territory with given name
	 * 
	 * @param name:
	 *            Name for the new territory
	 */
	public Territory(String name) {
		this.name = name;
		this.neighbourTerritories = new ArrayList<>();
		armyCount = 0;
	}

	/**
	 * Gets the name for the territory
	 * 
	 * @return the name of this territory
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the name of this territory
	 * 
	 * @param name:
	 *            the new name for this territory
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the neighbouring territories for this territory
	 * 
	 * @return the list of neighbouring territories for this territory
	 */
	public List<Territory> getNeighbourTerritories() {
		return neighbourTerritories;
	}

	/**
	 * Sets the neighbouring territories for this territory
	 * 
	 * @param neighbourTerritories:
	 *            List of neighbouring territories for this territory
	 */
	public void setNeighbourTerritories(List<Territory> neighbourTerritories) {
		this.neighbourTerritories = neighbourTerritories;
	}

	/**
	 * Gets the continent object associated with this territory
	 * 
	 * @return associated continent object
	 */
	public Continent getContinent() {
		return continent;
	}

	/**
	 * Changes the associated continent for this territory
	 * 
	 * @param continent:
	 *            Object of continent that contains this territory
	 */
	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	/**
	 * Gets the object of player who owns this territory
	 * 
	 * @return object of this player
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Sets the player who owns this territory
	 *
	 * @param owner:
	 *            Object of the player owning this territory
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	/**
	 * Gets number of armies that this territory has
	 * 
	 * @return this number of armies
	 */
	public int getArmyCount() {
		return armyCount;
	}

	/**
	 * Sets number of armies for this territory
	 * 
	 * @param numberOfArmies:
	 *            Number of armies that territory has
	 */
	public void setArmyCount(int numberOfArmies) {
		this.armyCount = numberOfArmies;
	}
	
	/**
	 * Overridden toString() method.
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
