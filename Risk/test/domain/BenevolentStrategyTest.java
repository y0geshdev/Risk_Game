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
 * This class have test cases for Benevolent Strategy.
 * 
 * @author Yogesh
 *
 */
public class BenevolentStrategyTest {

	/**
	 * To hold BenevolentStrategy class instance.
	 */
	private BenevolentStrategy benevolentStrategy;
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
		benevolentStrategy = new BenevolentStrategy();

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
	 * This case will test the reinforcement method of BenevolentStrategy. Assign 2
	 * territory to player with different army count on them and call
	 * BenevolentStrategy reinforcement() method. Then assertion is made on both the
	 * territory army count to test the logic.
	 */
	@Test
	public void testReinforcement() {
		Player player = new Player();
		player.setArmyCount(10);
		player.getTerritories().add(territoryList.get(0));
		player.getTerritories().add(territoryList.get(1));
		territoryList.get(0).setArmyCount(10);
		territoryList.get(1).setArmyCount(1);

		benevolentStrategy.reinforcement(player, null, 0, new PhaseViewModel());
		assertEquals(11, territoryList.get(1).getArmyCount());
		assertEquals(10, territoryList.get(0).getArmyCount());
	}

	/**
	 * This case will test the fortification method BenevolentStrategy. Assigned 2
	 * territory to player with different army counts. Checked if territories are
	 * moved from stronger territory to weaker territory or not.
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

		benevolentStrategy.fortify(player, null, null, 0, new PhaseViewModel());
		assertEquals(14, territoryList.get(1).getArmyCount());
		assertEquals(1, territoryList.get(0).getArmyCount());
	}

	/**
	 * This test case asserts the return value's key of BenevolentStrategy attack
	 * method which never attacks.
	 */
	@Test
	public void testAttack() {
		assertEquals(Boolean.FALSE,
				benevolentStrategy.attack(null, null, null, null, false, 0, 0, new PhaseViewModel()).getKey());
	}

}
