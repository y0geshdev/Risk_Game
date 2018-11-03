package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.MapController;
import domain.Continent;
import domain.Player;
import domain.Territory;

/**
 * This Junit Test Class will have all the test cases for {@link GameService}
 * class.
 * 
 * @author Yogesh
 *
 */
public class GameServiceTest {
	/**
	 * To hold GameService class instance.
	 */
	private GameService gameService;
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
		gameService = new GameService();
		Continent continentOne = new Continent("C1", 5);
		Continent continentTwo = new Continent("C2", 10);

		Territory territoryOne = new Territory("T1", continentOne);
		Territory territoryTwo = new Territory("T2", continentOne);
		Territory territoryThree = new Territory("T3", continentTwo);
		Territory territoryFour = new Territory("T4", continentTwo);
		Territory territoryFive = new Territory("T5", continentTwo);

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

	/**
	 * This method test if {@link GameService#assignTerritories(List)} assigns
	 * territories to players or not.
	 */
	@Test
	public void testAssignTerritories() {
		Player playerOne = new Player();
		Player playerTwo = new Player();
		List<Player> playersList = new ArrayList<>();
		Collections.addAll(playersList, playerOne, playerTwo);

		gameService.assignTerritories(playersList);

		assertTrue(playerOne.getTerritories().size() > 0 && playerTwo.getTerritories().size() > 0);
	}

	/**
	 * This method is use to test the functionality of
	 * {@link GameService#calcArmiesForReinforcement(Player)} method that whether it
	 * calculate armies for a given player as per game rules or not.
	 */
	@Test
	public void testCalcArmiesForReinforcement() {

		// case where player occupy whole continent.
		Player playerOne = new Player();
		Collections.addAll(playerOne.getTerritories(), territoryList.get(0), territoryList.get(1));
		gameService.calcArmiesForReinforcement(playerOne);
		assertEquals(8, playerOne.getArmyCount());

		// case where player doesn't occupy whole continent.
		Player p2 = new Player();
		Collections.addAll(p2.getTerritories(), territoryList.get(0));
		gameService.calcArmiesForReinforcement(p2);
		assertEquals(3, p2.getArmyCount());
	}

	/**
	 * This method test {@link GameService#createPlayers(List, int)} method which
	 * should populate playerList which is passed as one of the argument with as
	 * many players as denoted by numberOfplayers argument.
	 * 
	 */
	@Test
	public void testCreatePlayers() {
		List<Player> playerList = new ArrayList<>();
		int numberOfplayers = 5;
		gameService.createPlayers(playerList, numberOfplayers);
		assertEquals(numberOfplayers, playerList.size());
	}

	/**
	 * This test case is for
	 * {@link GameService#validateArmyInput(String, Player, Territory, List)} method
	 * where we test to various cases where method validates given parameters as per
	 * logic.
	 */
	@Test
	public void testValidateArmyInput() {
		Player player = new Player();
		player.setArmyCount(10);
		String errorString;

		// case where all checks pass
		List<String> errorList = new ArrayList<>();
		gameService.validateArmyInput("5", player, new Territory(), errorList);
		assertEquals(0, errorList.size());

		// case where string is not valid
		errorString = "Please, Enter a valid number";
		gameService.validateArmyInput("invalid", player, new Territory(), errorList);
		assertEquals(errorString, errorList.get(0));

		// case where army input is greater than player's army count.
		errorString = "Number of armies cannot be more than what owner owns";
		errorList = new ArrayList<>();
		gameService.validateArmyInput("11", player, new Territory(), errorList);
		assertEquals(errorString, errorList.get(0));

		// case where territory to which armies are moved is null
		errorString = "Please select at least one territory";
		errorList = new ArrayList<>();
		gameService.validateArmyInput("10", player, null, errorList);
		assertEquals(errorString, errorList.get(0));

		// case where territory to which armies are moved is null and number of armies
		// are less than 1
		errorList = new ArrayList<>();
		errorString = "Number of Armies cannot be less than 1";
		gameService.validateArmyInput("0", player, new Territory(), errorList);
		assertEquals(errorString, errorList.get(0));
	}

	/**
	 * This test checks if the
	 * {@link GameService#getAttackableTerritories(Territory)} method returns valid
	 * attackable territories according to given territory and according to game
	 * rule.
	 */
	@Test
	public void testGetAttackableTerritories() {
		Player playerOne = new Player();
		Player playerTwo = new Player();

		playerOne.getTerritories().add(territoryList.get(0));
		territoryList.get(0).setOwner(playerOne);
		for (int i = 1; i < territoryList.size(); i++) {
			territoryList.get(i).setOwner(playerTwo);
			playerTwo.getTerritories().add(territoryList.get(i));
		}

		assertEquals(2, gameService.getAttackableTerritories(territoryList.get(0)).size());
	}

	/**
	 * This test checks if the
	 * {@link GameService#fortify(Territory, Territory, int, List)} method fortify
	 * given territory according to game logic and after proper validation or not.
	 */
	@Test
	public void testFortify() {
		territoryList.get(0).setArmyCount(10);
		String errorString;
		List<String> errorList = new ArrayList<>();

		// test case where armies to move is greater than from territory army count.
		gameService.fortify(territoryList.get(0), territoryList.get(1), 11, errorList);
		assertEquals(1, errorList.size());

		// test where from territory try to move armies to itself.
		errorString = "Can't move from same territory to same territory.";
		errorList = new ArrayList<>();
		gameService.fortify(territoryList.get(0), territoryList.get(0), 5, errorList);
		assertEquals(errorString, errorList.get(0));

	}
	
	/**
	 * This test checks if the
	 * {@link GameService#attack(Territory, Territory)} method attack
	 * given territory or not.
	 */
	@Test
	public void testAttack() {
		Player playerOne = new Player();
		Player playerTwo = new Player();
		territoryList.get(0).setOwner(playerOne);
		territoryList.get(1).setOwner(playerTwo);
		
		gameService.attack(territoryList.get(0), territoryList.get(1));
		assertEquals(playerOne, territoryList.get(1).getOwner());
	}

	/**
	 * This test checks if the
	 * {@link GameService#getFortifiableTerritories(Territory)} method returns valid
	 * fortifiable territories according to given territory and according to game
	 * rule.
	 */
	@Test
	public void testGetFortifiableTerritories() {
		Player player = new Player();
		Collections.addAll(player.getTerritories(), territoryList.get(0), territoryList.get(1), territoryList.get(2),
				territoryList.get(4));
		territoryList.get(0).setOwner(player);
		territoryList.get(1).setOwner(player);
		territoryList.get(2).setOwner(player);
		territoryList.get(4).setOwner(player);

		assertEquals(3, gameService.getFortifiableTerritories(territoryList.get(0)).size());
	}

	
	@Test
	public void testGetNextPlayer() {
	
		List<Player> playerList = new ArrayList<>();
		int numberOfplayers = 5;
		gameService.createPlayers(playerList, numberOfplayers);
		
		Player currPlayer	=	gameService.getNextPlayer(null, playerList);
		
		int currPlayerIndex	=	playerList.indexOf(currPlayer);
		
		Player nextPlayer	=	gameService.getNextPlayer(currPlayer, playerList);
		
		if(currPlayerIndex!=playerList.size()-1) {
			assertEquals(playerList.get(currPlayerIndex+1), nextPlayer);
		}else {
			assertEquals(playerList.get(0), nextPlayer);
		}
	}
}
