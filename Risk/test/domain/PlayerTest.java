/*package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.MapController;
import javafx.util.Pair;

*//**
 * This Junit Test Class will have all the test cases for {@link Player} class.
 * 
 * @author Yogesh
 *
 *//*
public class PlayerTest {

	*//**
	 * To hold GameService class instance.
	 *//*
	private Player player;
	*//**
	 * It hold collection of all the continents in game in set.
	 *//*
	private HashSet<Continent> continentsSet;
	*//**
	 * It hold collection of all the territory in game in set.
	 *//*
	private HashSet<Territory> territoriesSet;
	*//**
	 * It hold collection of all the territories in game in list.
	 *//*
	private List<Territory> territoryList;
	*//**
	 * It hold collection of all the continents in game in list.
	 *//*
	private List<Continent> continentList;

	*//**
	 * This method setup require common context before every test is run.
	 *//*
	@Before
	public void setUp() {
		player = new Player();

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

		MapController.territoriesSet = territoriesSet;
		MapController.continentsSet = continentsSet;
	}

	*//**
	 * This test will test the reinforcement logic which is done by
	 * {@link Player#reinforcement(Territory, int)} method.
	 *//*
	@Test
	public void testReinforcement() {
		// setting up context.
		player.setArmyCount(10);
		player.reinforcement(territoryList.get(0), 3);

		// asserting different after effects of method call.
		assertEquals(3, territoryList.get(0).getArmyCount());
		assertEquals(7, player.getArmyCount());

	}

	*//**
	 * This test will test the fortification logic which is done by
	 * {@link Player#fortify(Territory, Territory, int)} method.
	 *//*
	@Test
	public void testFortify() {
		// setting up context
		Territory fromTerritory = territoryList.get(0);
		Territory toTerritory = territoryList.get(1);
		fromTerritory.setArmyCount(10);
		toTerritory.setArmyCount(0);
		int armiesToMove = 3;
		player.fortify(fromTerritory, toTerritory, armiesToMove);

		// asserting different after effects of method call.
		assertEquals(3, toTerritory.getArmyCount());
		assertEquals(7, fromTerritory.getArmyCount());
	}

	*//**
	 * This method test
	 * {@link Player#attack(Territory, Territory, Player, boolean, int, int, PhaseViewModel)}
	 * method where attack mode is normal and there is only one army attacking and
	 * defending in both territories.
	 * 
	 *//*
	@Test
	public void testAttackCaseOne() {
		// setUp context
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		attackerTerritory.setArmyCount(1);
		defenderTerritory.setArmyCount(1);

		Player defender = new Player();
		player.setTerritories(new ArrayList<>(Arrays.asList(attackerTerritory)));
		defender.setTerritories(new ArrayList<>(Arrays.asList(defenderTerritory)));
		attackerTerritory.setOwner(player);
		defenderTerritory.setOwner(defender);
		boolean isAllOutMode = false;
		int totalAttackerDice = 1;
		int totalDefenderDice = 1;
		Pair<Boolean, Integer> result = player.attack(attackerTerritory, defenderTerritory, defender, isAllOutMode,
				totalAttackerDice, totalDefenderDice, new PhaseViewModel());

		// assert based on if attacker won defending territory or not.
		if (result.getKey()) {
			assertEquals(1, attackerTerritory.getArmyCount());
			assertEquals(player, defenderTerritory.getOwner());
			assertEquals(0, defenderTerritory.getArmyCount());
		} else {
			assertEquals(0, attackerTerritory.getArmyCount());
			assertEquals(1, defenderTerritory.getArmyCount());
			assertEquals(defender, defenderTerritory.getOwner());
		}
	}

	*//**
	 * This method test
	 * {@link Player#attack(Territory, Territory, Player, boolean, int, int, PhaseViewModel)}
	 * method where attack mode is All-out.
	 * 
	 *//*
	@Test
	public void testAttackCaseTwo() {
		// setUp context
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		attackerTerritory.setArmyCount(10);
		defenderTerritory.setArmyCount(5);

		Player defender = new Player();
		player.setTerritories(new ArrayList<>(Arrays.asList(attackerTerritory)));
		defender.setTerritories(new ArrayList<>(Arrays.asList(defenderTerritory)));
		attackerTerritory.setOwner(player);
		defenderTerritory.setOwner(defender);
		boolean isAllOutMode = true;
		int totalAttackerDice = 0;
		int totalDefenderDice = 0;
		Pair<Boolean, Integer> result = player.attack(attackerTerritory, defenderTerritory, defender, isAllOutMode,
				totalAttackerDice, totalDefenderDice, new PhaseViewModel());

		// assert based on if attacker won defending territory or not.
		if (result.getKey()) {
			assertEquals(player, defenderTerritory.getOwner());
			assertEquals(0, defenderTerritory.getArmyCount());
		} else {
			assertTrue(defenderTerritory.getArmyCount()>0);
			assertEquals(defender, defenderTerritory.getOwner());
		}
	}
	
	*//**
	 * This method will test the
	 * {@link Player#attackerHelper(Territory, Territory, List, List, PhaseViewModel)}
	 * method where attacker beats defender completely.
	 *//*
	@Test
	public void testAttackerHelperCaseOne() {
		// setting up context
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		attackerTerritory.setArmyCount(10);
		defenderTerritory.setArmyCount(5);
		List<Integer> attackerDiceRolls = Arrays.asList(6, 6, 6);
		List<Integer> defenderDiceRolls = Arrays.asList(2, 5);

		// asserting different after effects of method call.
		assertEquals(3, player.attackerHelper(attackerTerritory, defenderTerritory, attackerDiceRolls,
				defenderDiceRolls, new PhaseViewModel()));
		assertEquals(10, attackerTerritory.getArmyCount());
		assertEquals(3, defenderTerritory.getArmyCount());
	}

	*//**
	 * This method will test the
	 * {@link Player#attackerHelper(Territory, Territory, List, List, PhaseViewModel)}
	 * method where attacker beats defender's one army and defender beats attacker's
	 * one army.
	 *//*
	@Test
	public void testAttackerHelperCaseTwo() {
		// setting up context
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		attackerTerritory.setArmyCount(10);
		defenderTerritory.setArmyCount(5);
		List<Integer> attackerDiceRolls = Arrays.asList(6, 6, 6);
		List<Integer> defenderDiceRolls = Arrays.asList(6, 5);

		// asserting different after effects of method call.
		assertEquals(2, player.attackerHelper(attackerTerritory, defenderTerritory, attackerDiceRolls,
				defenderDiceRolls, new PhaseViewModel()));
		assertEquals(9, attackerTerritory.getArmyCount());
		assertEquals(4, defenderTerritory.getArmyCount());
	}

	*//**
	 * This method tests {@link Player#recordDiceRolls(int, boolean)} method where
	 * armySize and if player for which dice are rolled is attacker or defender will
	 * decide how many dices can be rolled.
	 *//*
	@Test
	public void testRecordDiceRolls() {
		List<Integer> diceRollList;
		int armySize;
		boolean isAttacker;

		// if its attacker with 2 armies in attacking territory.
		armySize = 2;
		isAttacker = true;
		diceRollList = player.recordDiceRolls(armySize, isAttacker);
		assertEquals(2, diceRollList.size());

		// if its attacker with 5 armies in attacking territory.
		armySize = 5;
		isAttacker = true;
		diceRollList = player.recordDiceRolls(armySize, isAttacker);
		assertEquals(3, diceRollList.size());

		// if its defender with 1 army in attacking territory.
		armySize = 1;
		isAttacker = false;
		diceRollList = player.recordDiceRolls(armySize, isAttacker);
		assertEquals(1, diceRollList.size());

		// if its defender with 5 army in attacking territory.
		armySize = 5;
		isAttacker = false;
		diceRollList = player.recordDiceRolls(armySize, isAttacker);
		assertEquals(2, diceRollList.size());
	}
}
*/