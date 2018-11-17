package domain;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * This class represent model class for World Domination View.
 * 
 * @author Yogesh
 *
 */
public class WorldDominationModel extends Observable {

	/**
	 * Hold reference to players list
	 */
	private List<Player> playersList;

	/**
	 * It reference to a Map which holds players as keys and percentage of world
	 * occupied by them.
	 */
	private Map<Player, Double> playerMapCoverageMapping;

	/**
	 * It reference to a Map which holds players as keys and continents occupied by
	 * them.
	 */
	private Map<Player, Set<Continent>> playerContinentsMapping;

	/**
	 * It reference to a Map which holds players as keys and total armies in all of
	 * their owned territories.
	 */
	private Map<Player, Integer> playerArmiesMapping;

	/**
	 * Constructor for WorldDominationModel.
	 * 
	 * @param playersList:
	 *            List of player playing current game.
	 */
	public WorldDominationModel(List<Player> playersList) {
		super();
		this.playersList = playersList;
		playerMapCoverageMapping = new LinkedHashMap<>();
		playerContinentsMapping = new LinkedHashMap<>();
		playerArmiesMapping = new LinkedHashMap<>();

		// initialize class attributes
		for (Player player : playersList) {
			playerMapCoverageMapping.put(player, 0.0);
			playerContinentsMapping.put(player, new LinkedHashSet<>());
			playerArmiesMapping.put(player, 0);
		}
	}

	/**
	 * Getter for {@link WorldDominationModel#playerMapCoverageMapping}.
	 * 
	 * @return a Map.
	 */
	public Map<Player, Double> getPlayerMapCoverageMapping() {
		return playerMapCoverageMapping;
	}

	/**
	 * Getter for {@link WorldDominationModel#playerContinentsMapping}.
	 * 
	 * @return a Map.
	 */
	public Map<Player, Set<Continent>> getPlayerContinentsMapping() {
		return playerContinentsMapping;
	}

	/**
	 * Getter for {@link WorldDominationModel#playerArmiesMapping}.
	 * 
	 * @return a Map.
	 */
	public Map<Player, Integer> getPlayerArmiesMapping() {
		return playerArmiesMapping;
	}

	/**
	 * This method is called by controller to update this model state which is
	 * further reflected by view.
	 * 
	 * @param continentsSet:
	 *            Set of continents.
	 * @param territoriesSet:
	 *            Set of territories.
	 */
	public void updateState(HashSet<Continent> continentsSet, HashSet<Territory> territoriesSet) {

		double mapCoverage;
		Set<Continent> continentsCovered;
		int armies;
		double totalTerritories = territoriesSet.size();
		for (Player player : playersList) {

			mapCoverage = (player.getTerritories().size() / totalTerritories) * 100;
			playerMapCoverageMapping.put(player, mapCoverage);

			continentsCovered = getAllCoveredContinents(player, continentsSet);
			playerContinentsMapping.put(player, continentsCovered);

			armies = getTotalArmies(player);
			playerArmiesMapping.put(player, armies);

			// notify observer.
			setChanged();
			notifyObservers(this);

		}
	}

	/**
	 * This method return Set of continents occupied by player passed as parameter.
	 * 
	 * @param player:
	 *            player for whom covered continents are to be fetched.
	 * @param continentsSet:
	 *            Set of all the continents in game map.
	 * @return Set containing all the continents which are covered by
	 *         passed player.
	 */
	private Set<Continent> getAllCoveredContinents(Player player, HashSet<Continent> continentsSet) {
		Set<Continent> coveredContinentsSet = new LinkedHashSet<>();
		boolean currentContinentCovered;

		// iterate over continents to check if its occupied by player or not.
		for (Continent continent : continentsSet) {
			currentContinentCovered = true;
			for (Territory territory : continent.getTerritories()) {
				if (!player.getTerritories().contains(territory)) {
					currentContinentCovered = false;
					break;
				}
			}
			if (currentContinentCovered)
				coveredContinentsSet.add(continent);
		}
		return coveredContinentsSet;
	}

	/**
	 * This method calculate all the armies in all the territories owned by passed
	 * player.
	 * 
	 * @param player:
	 *            player for which army count is to done.
	 * @return an int representing total number of armies.
	 */
	private int getTotalArmies(Player player) {
		int armyCount = 0;
		for (Territory territory : player.getTerritories()) {
			armyCount += territory.getArmyCount();
		}
		armyCount += player.getArmyCount();

		return armyCount;
	}

}
