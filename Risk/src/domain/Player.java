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

	public Player() {
		armyCount = 0;
		territories = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}

	public void setArmyCount(int armyCount) {
		this.armyCount = armyCount;
	}

	public int getArmyCount() {
		return armyCount;
	}

	public void updateArmyCount(int armyCount) {
		this.armyCount += armyCount;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
