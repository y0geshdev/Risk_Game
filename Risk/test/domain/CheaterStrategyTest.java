package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * This test class have test cases for CheaterStrategy.
 * 
 * @author Yogesh
 *
 */
public class CheaterStrategyTest {

	/**
	 * To hold CheaterStrategy class instance.
	 */
	private CheaterStrategy cheaterStrategy;
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
		cheaterStrategy = new CheaterStrategy();

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
	 * This test case test CheaterStrategy reinforcement method. It set some army to
	 * player's territory and then call method and then check if they are doubled or
	 * not.
	 */
	@Test
	public void testReinforcement() {
		Player player = new Player();
		territoryList.get(0).setArmyCount(1);
		territoryList.get(1).setArmyCount(2);
		Collections.addAll(player.getTerritories(), territoryList.get(0), territoryList.get(1));
		cheaterStrategy.reinforcement(player, null, 0, new PhaseViewModel());

		assertEquals(2, territoryList.get(0).getArmyCount());
		assertEquals(4, territoryList.get(1).getArmyCount());

	}

	/**
	 * This test case is for CheaterStrategy fortification method. It test if
	 * fortification will double the armies in territories whose neighbor territory
	 * is owned by someone else or not.
	 */
	@Test
	public void testFortify() {
		Player player = new Player();
		territoryList.get(2).setArmyCount(1);
		territoryList.get(3).setArmyCount(1);
		territoryList.get(4).setArmyCount(1);
		territoryList.get(2).setOwner(player);
		territoryList.get(3).setOwner(player);
		territoryList.get(4).setOwner(player);

		Player defender = new Player();
		defender.setName("defender");
		Collections.addAll(defender.getTerritories(), territoryList.get(0), territoryList.get(1));
		territoryList.get(0).setOwner(defender);
		territoryList.get(1).setOwner(defender);

		Collections.addAll(player.getTerritories(), territoryList.get(2), territoryList.get(3), territoryList.get(4));
		cheaterStrategy.fortify(player, null, null, 0, new PhaseViewModel());

		assertEquals(2, territoryList.get(2).getArmyCount());
		assertEquals(1, territoryList.get(3).getArmyCount());
		assertEquals(2, territoryList.get(4).getArmyCount());
	}

	/**
	 * This test will set territory 3, 4 and 5 to player and then call attack method
	 * on Cheater strategy. Then it check if all territory is occupied by player or
	 * not.
	 */
	@Test
	public void testAttack() {
		Player player = new Player();
		territoryList.get(2).setArmyCount(1);
		territoryList.get(3).setArmyCount(1);
		territoryList.get(4).setArmyCount(1);
		territoryList.get(2).setOwner(player);
		territoryList.get(3).setOwner(player);
		territoryList.get(4).setOwner(player);

		Player defender = new Player();
		defender.setName("defender");
		Collections.addAll(defender.getTerritories(), territoryList.get(0), territoryList.get(1));
		territoryList.get(0).setOwner(defender);
		territoryList.get(1).setOwner(defender);

		Collections.addAll(player.getTerritories(), territoryList.get(2), territoryList.get(3), territoryList.get(4));
		cheaterStrategy.attack(player, null, null, null, true, 0, 0, new PhaseViewModel());
		assertEquals(5, player.getTerritories().size());
	}

}
