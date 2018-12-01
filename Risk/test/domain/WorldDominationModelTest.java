package domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * This class test methods on WorldDominationModel.
 * 
 * @author Yogesh
 *
 */
public class WorldDominationModelTest {

	/**
	 * To hold WorldDominationModel class instance.
	 */
	private WorldDominationModel model;
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
		model = new WorldDominationModel(new ArrayList<>());

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
	 * This method will test getAllCoveredContinents in WorldDominationModel class.
	 *
	 */
	@Test
	public void testGetAllCoveredContinents() {
		Player player = new Player();
		Collections.addAll(player.getTerritories(), territoryList.get(0),territoryList.get(1));
		
		Set<Continent> set = model.getAllCoveredContinents(player, continentsSet);
		assertEquals(1,set.size());
		
	}

	/**
	 * This method will test getTotalArmies() in WorldDominationModel class.
	 * 
	 */
	@Test
	public void testGetTotalArmies() {
		Player player = new Player();
		Collections.addAll(player.getTerritories(), territoryList.get(0),territoryList.get(1));
		territoryList.get(0).setArmyCount(100);
		territoryList.get(1).setArmyCount(5);
		player.setArmyCount(15);
		
		int actual = model.getTotalArmies(player);
		assertEquals(120, actual);
	}

}
