package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javafx.util.Pair;

/**
 * This test class have test cases for RandomStrategy.
 * 
 * @author Yogesh
 *
 */
public class RandomStrategyTest {

	/**
	 * To hold RandomStrategy class instance.
	 */
	private RandomStrategy randomStrategy;
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
		randomStrategy = new RandomStrategy();

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
	 * This case assign a single territory to player and call reinforcement method
	 * on RandomStrategy and then check if all the armies are assigned to that
	 * territory or not.
	 */
	@Test
	public void testReinforcement() {
		Player player = new Player();
		Collections.addAll(player.getTerritories(), territoryList.get(0));
		player.setArmyCount(10);

		randomStrategy.reinforcement(player, null, 0, new PhaseViewModel());
		assertEquals(10, territoryList.get(0).getArmyCount());
		assertEquals(0, player.getArmyCount());
	}

	/**
	 * The random strategy uses To and from territory which are randomly selected at
	 * run time. So cannot observe which territory state is changed.
	 */
	@Ignore
	@Test
	public void testFortify() {
		fail("Not yet implemented");
	}

	/**
	 * This test case will assign only one territory to player and call attack
	 * method of Random strategy. If attack is successful then player will have 2
	 * territory else it will have only one territory.
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
		territoryList.get(2).setArmyCount(1);
		territoryList.get(3).setArmyCount(1);
		territoryList.get(4).setArmyCount(1);

		Pair<Boolean, Integer> result = randomStrategy.attack(player, defender, null, null, false, 0, 0,
				new PhaseViewModel());
		if (result.getKey()) {
			assertEquals(2, player.getTerritories().size());
		} else
			assertEquals(1, player.getTerritories().size());
	}

}
