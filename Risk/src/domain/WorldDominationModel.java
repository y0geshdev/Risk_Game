package domain;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

public class WorldDominationModel extends Observable {

	private List<Player> playersList;
	private Map<Player, Double> playerMapCoverageMapping;
	private Map<Player, Set<Continent>> playerContinentsMapping;
	private Map<Player, Integer> playerArmiesMapping;

	public WorldDominationModel(List<Player> playersList) {
		super();
		this.playersList = playersList;
		playerMapCoverageMapping = new LinkedHashMap<>();
		playerContinentsMapping = new LinkedHashMap<>();
		playerArmiesMapping = new LinkedHashMap<>();

		for (Player player : playersList) {
			playerMapCoverageMapping.put(player, 0.0);
			playerContinentsMapping.put(player, new LinkedHashSet<>());
			playerArmiesMapping.put(player, 0);
		}
	}

	public Map<Player, Double> getPlayerMapCoverageMapping() {
		return playerMapCoverageMapping;
	}

	public Map<Player, Set<Continent>> getPlayerContinentsMapping() {
		return playerContinentsMapping;
	}

	public Map<Player, Integer> getPlayerArmiesMapping() {
		return playerArmiesMapping;
	}

	public void updateState(HashSet<Continent> continentsSet, HashSet<Territory> territoriesSet) {

		double mapCoverage;
		Set<Continent> continentsCovered;
		int armies;
		double totalTerritories = territoriesSet.size();
		for (Player player : playersList) {

			mapCoverage = (player.getTerritories().size() / totalTerritories)*100;
			playerMapCoverageMapping.put(player, mapCoverage);

			continentsCovered = getAllCoveredContinents(player, continentsSet);
			//playerContinentsMapping.get(player).addAll(continentsCovered);
			playerContinentsMapping.put(player,continentsCovered);

			armies = getTotalArmies(player);
			playerArmiesMapping.put(player, armies);

			setChanged();
			notifyObservers(this);

		}
	}

	private Set<Continent> getAllCoveredContinents(Player player, HashSet<Continent> continentsSet) {
		Set<Continent> coveredContinentsSet = new LinkedHashSet<>();
		boolean currentContinentCovered;
		
		for (Continent continent : continentsSet) {
			currentContinentCovered = true;
			for (Territory territory : continent.getTerritories()) {
				if (!player.getTerritories().contains(territory)) {
					currentContinentCovered = false;
					break;
				}
			}
			if(currentContinentCovered)
				coveredContinentsSet.add(continent);
		}
		return coveredContinentsSet;
	}

	private int getTotalArmies(Player player) {
		int armyCount = 0;
		for (Territory territory : player.getTerritories()) {
			armyCount += territory.getArmyCount();
		}
		armyCount += player.getArmyCount();

		return armyCount;
	}

}
