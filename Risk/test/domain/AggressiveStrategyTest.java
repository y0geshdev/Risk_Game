package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import javafx.util.Pair;

/**
 * This class have test cases for AggressiveStrategy.
 * 
 * @author Yogesh
 *
 */
public class AggressiveStrategyTest {

	/**
	 * To hold AggressiveStrategy class instance.
	 */
	private AggressiveStrategy aggressiveStrategy;
	/**
	 * It hold collection of all the continents in game in set.
	 */
	private HashSet<Continent> continentsSet;
	/**
	 * It hold collection of all the territory in game in set.
	 */
	private HashSet<Territory> territoriesSet;
	/**
	 * It hold collection of all the territories in game in list.
	 */
	private List<Territory> territoryList;
	/**
	 * It hold collection of all the continents in game in list.
	 */
	private List<Continent> continentList;

	/**
	 * This method setup require common context before every test is run.
	 */
	@Before
	public void setUp() {
		aggressiveStrategy = new AggressiveStrategy();

		// setUp continents
		Continent continentOne = new Continent("C1", 5);
		Continent continentTwo = new Continent("C2", 10);

		// setUp territories
		Territory territoryOne = new Territory("T1", continentOne);
		Territory territoryTwo = new Territory("T2", continentOne);
		Territory territoryThree = new Territory("T3", continentTwo);
		Territory territoryFour = new Territory("T4", continentTwo);
		Territory territoryFive = new Territory("T5", continentTwo);

		// territory mapping.
		territoryOne.getNeighbourTerritories().add(territoryTwo);
		territoryOne.getNeighbourTerritories().add(territoryFive);
		territoryTwo.getNeighbourTerritories().add(territoryOne);
		territoryTwo.getNeighbourTerritories().add(territoryThree);
		territoryThree.getNeighbourTerritories().add(territoryTwo);
		territoryThree.getNeighbourTerritories().add(territoryFour);
		territoryFour.getNeighbourTerritories().add(territoryThree);
		territoryFour.getNeighbourTerritories().add(territoryFive);
		territoryFive.getNeighbourTerritories().add(territoryFour);
		territoryFive.getNeighbourTerritories().add(territoryOne);

		// preparing list and sets of territory and continents containing those
		// territories.
		continentsSet = new HashSet<>();
		territoriesSet = new HashSet<>();

		territoryList = new ArrayList<>();
		continentList = new ArrayList<>();

		Collections.addAll(continentsSet, continentOne, continentTwo);
		Collections.addAll(territoriesSet, territoryOne, territoryTwo, territoryThree, territoryFour, territoryFive);

		Collections.addAll(continentList, continentOne, continentTwo);
		Collections.addAll(territoryList, territoryOne, territoryTwo, territoryThree, territoryFour, territoryFive);

		Collections.addAll(continentOne.getTerritories(), territoryOne, territoryTwo);
		Collections.addAll(continentTwo.getTerritories(), territoryThree, territoryFour, territoryFive);

	}

	/**
	 * This case will test the reinforcement method of AggressiveStrategy. Assign 2
	 * territory to player with different army count on them and call
	 * AggressiveStrategy reinforcement() method. It should reinforce strongest
	 * territoty.
	 */
	@Test
	public void testReinforcement() {
		Player player = new Player();
		player.setArmyCount(10);
		player.getTerritories().add(territoryList.get(0));
		player.getTerritories().add(territoryList.get(1));
		territoryList.get(0).setArmyCount(10);
		territoryList.get(1).setArmyCount(1);

		aggressiveStrategy.reinforcement(player, null, 0, new PhaseViewModel());
		assertEquals(1, territoryList.get(1).getArmyCount());
		assertEquals(20, territoryList.get(0).getArmyCount());
	}

	/**
	 * This case will test the fortification method AggressiveStrategy. Assigned 2
	 * territory to player with different army counts. Checked if territories are
	 * moved from weaker territory to stronger territory or not.
	 */
	@Test
	public void testFortify() {
		Player player = new Player();
		player.getTerritories().add(territoryList.get(0));
		player.getTerritories().add(territoryList.get(1));
		territoryList.get(0).setArmyCount(10);
		territoryList.get(1).setArmyCount(5);
		territoryList.get(0).setOwner(player);
		territoryList.get(1).setOwner(player);

		aggressiveStrategy.fortify(player, null, null, 0, new PhaseViewModel());
		assertEquals(14, territoryList.get(0).getArmyCount());
		assertEquals(1, territoryList.get(1).getArmyCount());
	}

	/**
	 * This case will test AggresiveStrategy attack() method. Player is assigned
	 * with one territory and all the neighbor territory of this assigned territory
	 * have very high number of armies assigned to them apart from one. So if player
	 * won the attack then this will be the territory which player will conquer else
	 * player's territory army count will be left 1.
	 */
	@Test
	public void testAttack() {
		Player player = new Player();
		player.getTerritories().add(territoryList.get(0));
		territoryList.get(0).setArmyCount(10);
		territoryList.get(0).setOwner(player);

		Player defender = new Player();
		defender.setName("defender");
		Collections.addAll(defender.getTerritories(), territoryList.get(1), territoryList.get(2), territoryList.get(3),
				territoryList.get(4));
		territoryList.get(1).setOwner(defender);
		territoryList.get(2).setOwner(defender);
		territoryList.get(3).setOwner(defender);
		territoryList.get(4).setOwner(defender);
		territoryList.get(1).setArmyCount(1);
		territoryList.get(2).setArmyCount(10);
		territoryList.get(3).setArmyCount(10);
		territoryList.get(4).setArmyCount(10);

		Pair<Boolean, Integer> result = aggressiveStrategy.attack(player, defender, null, null, false, 0, 0,
				new PhaseViewModel());
		if (result.getKey()) {
			assertEquals(2, player.getTerritories().size());
			assertEquals(player, territoryList.get(1).getOwner());
		} else {
			assertEquals(1, player.getTerritories().get(0).getArmyCount());
		}
	}

	/**
	 * This case will test the fetchAttackFromTerritory() method of
	 * AggresiveStrategy. Assign different territory to player with different number
	 * of army counts. Check if the returned territory is the one which have maximum
	 * number of armies or not.
	 */
	@Test
	public void testFetchAttackFromTerritory() {
		Player player = new Player();
		Collections.addAll(player.getTerritories(), territoryList.get(0), territoryList.get(1), territoryList.get(2),
				territoryList.get(3));
		territoryList.get(0).setArmyCount(10);
		territoryList.get(1).setArmyCount(20);
		territoryList.get(2).setArmyCount(30);
		territoryList.get(3).setArmyCount(40);

		Territory actual = aggressiveStrategy.fetchAttackFromTerritory(player);
		assertEquals(territoryList.get(3), actual);
	}

	/**
	 * This case will test the fetchAttackToTerritory() method of AggresiveStrategy.
	 * Assign one territory to player and assign neighbor of that territory to some
	 * other player. Place different number of armies in those neighbor and call
	 * method. Check if returned territory is the neighbor territory which have
	 * minimum armies placed in it.
	 */
	@Test
	public void testFetchAttackToTerritory() {
		Player player = new Player();
		player.getTerritories().add(territoryList.get(0));
		territoryList.get(0).setOwner(player);

		Player defender = new Player();
		defender.setName("defender");
		Collections.addAll(defender.getTerritories(), territoryList.get(1), territoryList.get(4));
		territoryList.get(1).setOwner(defender);
		territoryList.get(4).setOwner(defender);
		territoryList.get(1).setArmyCount(1);
		territoryList.get(4).setArmyCount(10);

		Territory actual = aggressiveStrategy.fetchAttackToTerritory(territoryList.get(0));
		assertEquals(territoryList.get(1), actual);
	}

}
