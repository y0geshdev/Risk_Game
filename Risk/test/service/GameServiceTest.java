package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import controller.MapController;
import domain.AggressiveStrategy;
import domain.CardExchangeViewModel;
import domain.Continent;
import domain.HumanStrategy;
import domain.PhaseViewModel;
import domain.Player;
import domain.PlayerStrategyEnum;
import domain.Territory;
import javafx.util.Pair;

/**
 * This Junit Test Class will have all the test cases for GameService class.
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
	 * It holds reference to file to save object
	 */
	File fileToSave;
	/**
	 * Variable holds reference to list of players
	 */
	List<Player> playerList;
	/**
	 * Variable holds reference to current player playing the game
	 */
	Player currentPlayer;
	/**
	 * Variable holds reference to current phase in the game
	 */
	String currentPhase;
	/**
	 * Variable holds reference to boolean parameter that is true is start up phase
	 * is completed
	 */
	boolean ifStartUpIsComepleted;
	/**
	 * Variable holds reference to a map of player and its playing strategy
	 */
	Map<Player, PlayerStrategyEnum> playerStrategyMapping;
	
	/**
	 * Variable holds reference to CardExchangeViewModel 
	 */
	CardExchangeViewModel cardExchangeViewModel;

	/**
	 * This method setup require common context before every test is run.
	 */
	@Before
	public void setUp() {
		gameService = new GameService();
		fileToSave = new File("resource\\CheckSerialize.ser");

		// setup players
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();

		// setUp continents
		Continent continentOne = new Continent("C1", 5);
		Continent continentTwo = new Continent("C2", 10);

		// setUp territories.
		Territory territoryOne = new Territory("T1", continentOne);
		Territory territoryTwo = new Territory("T2", continentOne);
		Territory territoryThree = new Territory("T3", continentTwo);
		Territory territoryFour = new Territory("T4", continentTwo);
		Territory territoryFive = new Territory("T5", continentTwo);

		// mapping territories
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

		// setting up whole Map.
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

		// setting for serialization in file
		p1.setArmyCount(12);
		p1.setPlayingStrategy(new HumanStrategy());
		p1.setTerritories(Arrays.asList(territoryOne, territoryTwo));

		p2.setArmyCount(10);
		p2.setPlayingStrategy(new AggressiveStrategy());
		p2.setTerritories(Arrays.asList(territoryThree, territoryFour));

		p3.setArmyCount(10);
		p3.setPlayingStrategy(new HumanStrategy());
		p3.setTerritories(Arrays.asList(territoryFive));

		// setting up gameState objects
		playerList = new ArrayList<>();
		playerList.add(p1);
		playerList.add(p2);
		playerList.add(p3);

		currentPlayer = p1;
		currentPhase = "reinforcementPhase";
		ifStartUpIsComepleted = true;

		playerStrategyMapping = new HashMap<>();
		playerStrategyMapping.put(p1, PlayerStrategyEnum.HUMAN);
		playerStrategyMapping.put(p2, PlayerStrategyEnum.AGGRESSIVE);
		playerStrategyMapping.put(p3, PlayerStrategyEnum.HUMAN);
		
		cardExchangeViewModel	=	new CardExchangeViewModel(territoriesSet);
	}

	/**
	 * This method test if GameService assignTerritories() assigns territories to
	 * players or not.
	 */
	@Test
	public void testAssignTerritories() {
		Player playerOne = new Player();
		Player playerTwo = new Player();
		List<Player> playersList = new ArrayList<>();
		Collections.addAll(playersList, playerOne, playerTwo);

		gameService.assignTerritories(playersList, territoriesSet);

		assertTrue(playerOne.getTerritories().size() > 0 && playerTwo.getTerritories().size() > 0);
	}

	/**
	 * This method is use to test the functionality of GameService
	 * calcArmiesForReinforcement() method for HUMAN strategy that whether it
	 * calculate armies for a given player as per game rules or not when player
	 * occupy whole continent.
	 */
	@Test
	public void testCalcArmiesForReinforcementCaseOne() {

		Player playerOne = new Player();
		Collections.addAll(playerOne.getTerritories(), territoryList.get(0), territoryList.get(1));
		gameService.calcArmiesForReinforcement(playerOne, PlayerStrategyEnum.HUMAN,
				new CardExchangeViewModel(territoriesSet));
		assertEquals(8, playerOne.getArmyCount());

	}

	/**
	 * This method is use to test the functionality of GameService
	 * calcArmiesForReinforcement() method for HUMAN strategy that whether it
	 * calculate armies for a given player as per game rules or not when player
	 * doesn't occupy whole continent.
	 */
	@Test
	public void testCalcArmiesForReinforcementCaseTwo() {

		Player p2 = new Player();
		Collections.addAll(p2.getTerritories(), territoryList.get(0));
		gameService.calcArmiesForReinforcement(p2, PlayerStrategyEnum.HUMAN, new CardExchangeViewModel(territoriesSet));
		assertEquals(3, p2.getArmyCount());
	}

	/**
	 * This test case is for GameService validateArmyInput() method where we test to
	 * various cases where method validates given parameters as per logic.
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
	 * This test checks if the GameService getAttackableTerritories(Territory)
	 * method returns valid attackable territories according to given territory and
	 * according to game rule.
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
	 * This test checks if the GameService fortify(Territory, Territory, int, List)
	 * method fortify given territory according to game logic and after proper
	 * validation or not when armies to move is greater than from territory army
	 * count.
	 */
	@Test
	public void testValidatefortifcationParametersCaseOne() {
		territoryList.get(0).setArmyCount(10);
		String errorString;
		List<String> errorList = new ArrayList<>();

		gameService.validatefortifcationParameters(territoryList.get(0), territoryList.get(1), 11, errorList);
		errorString = "Can only move upto 9 armies.";
		assertEquals(errorString, errorList.get(0));

	}

	/**
	 * This test checks if the GameService fortify(Territory, Territory, int, List)
	 * method fortify given territory according to game logic and after proper
	 * validation or not when from territory try to move armies to itself.
	 */
	@Test
	public void testValidatefortifcationParametersCaseTwo() {
		territoryList.get(0).setArmyCount(10);
		String errorString;
		List<String> errorList = new ArrayList<>();

		errorString = "Can't move from same territory to same territory.";
		errorList = new ArrayList<>();
		gameService.validatefortifcationParameters(territoryList.get(0), territoryList.get(0), 5, errorList);
		assertEquals(errorString, errorList.get(0));

	}

	/**
	 * This test checks if the GameService attack() method for HUMAN strategy attack
	 * given territory or not.
	 */
	@Test
	public void testAttack() {
		Player playerOne = new Player();
		Player playerTwo = new Player();
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		attackerTerritory.setOwner(playerOne);
		defenderTerritory.setOwner(playerTwo);
		attackerTerritory.setArmyCount(10);
		defenderTerritory.setArmyCount(5);
		playerOne.setPlayingStrategy(new HumanStrategy());

		// testing for all out attack mode
		Pair<Boolean, Integer> result = gameService.attack(playerOne, playerTwo, attackerTerritory, defenderTerritory,
				true, 0, 0, new PhaseViewModel());
		if (result.getKey()) {
			assertEquals(defenderTerritory.getOwner(), playerOne);
		} else {
			assertTrue(attackerTerritory.getOwner() != defenderTerritory.getOwner());
		}

	}

	/**
	 * This test checks if the GameService getFortifiableTerritories(Territory)
	 * method returns valid fortifiable territories according to given territory and
	 * according to game rule.
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

	/**
	 * This test checks if the GameService getNextPlayer() method returns valid
	 * player whose turn it is as per round robin fashion for choosing player turn
	 */
	@Test
	public void testGetNextPlayer() {
		List<Player> playerList = new ArrayList<>();
		int numberOfplayers = 5;

		for (int i = 0; i < numberOfplayers; i++) {
			playerList.add(new Player());
		}

		Player currPlayer = gameService.getNextPlayer(null, playerList);
		int currPlayerIndex = playerList.indexOf(currPlayer);

		Player nextPlayer = gameService.getNextPlayer(currPlayer, playerList);

		if (currPlayerIndex != playerList.size() - 1) {
			assertEquals(playerList.get(currPlayerIndex + 1), nextPlayer);
		} else {
			assertEquals(playerList.get(0), nextPlayer);
		}
	}

	/**
	 * This test checks if the GameService addReinforcement() method for HUMAN
	 * strategy to add correct number of reinforcement armies to current player's
	 * selected territory
	 */

	@Test
	public void testAddReinforcement() {

		Player player = new Player();
		player.setArmyCount(40);
		Territory testTerritory = territoryList.get(0);
		testTerritory.setOwner(player);
		player.setPlayingStrategy(new HumanStrategy());
		gameService.addReinforcement(player, testTerritory, 3, new PhaseViewModel());

		int finalArmy = testTerritory.getArmyCount();

		assertEquals(3, finalArmy);

		gameService.addReinforcement(player, testTerritory, 3, new PhaseViewModel());

		finalArmy = testTerritory.getArmyCount();
		assertEquals(6, finalArmy);

	}

	/**
	 * This test check if the method GameService canPlayerAttackFurther() returns
	 * correct boolean based on if current player can attack further or not
	 */

	@Test
	public void testCanPlayerAttackFurther() {

		Player player = new Player();
		player.setTerritories(Arrays.asList(territoryList.get(0), territoryList.get(2), territoryList.get(4)));

		territoryList.get(0).setOwner(player);
		territoryList.get(0).setArmyCount(3);

		territoryList.get(2).setOwner(player);
		territoryList.get(2).setArmyCount(3);

		territoryList.get(4).setOwner(player);
		territoryList.get(0).setArmyCount(3);

		assertTrue(gameService.canPlayerAttackFurther(player));

	}

	/**
	 * This test check if the method GameService ifContinentOccupied() returns
	 * correct boolean based on if current player occupies all the territories of
	 * the continent and hence occupies the continent.
	 */
	@Test
	public void testIfContinentOccupied() {
		Player player = new Player();
		player.setTerritories(Arrays.asList(territoryList.get(0), territoryList.get(1)));

		assertTrue(gameService.ifContinentOccupied(continentList.get(0).getTerritories(), player.getTerritories()));

	}

	/**
	 * This test checks if the GameService endOfStartUpPhase() method returns valid
	 * boolean to check if the startUp phase is completed or not.
	 */
	@Test
	public void testEndOfStartUpPhase() {
		Set<Player> playersWithZeroArmies = new HashSet<>();
		List<Player> playerList = new ArrayList<>();
		int numberOfPlayers = 5;
		for (int i = 0; i < numberOfPlayers; i++) {
			playerList.add(new Player());
		}

		assertTrue(!gameService.endOfStartUpPhase(playersWithZeroArmies, playerList));

		for (int i = 0; i < playerList.size(); i++) {
			playersWithZeroArmies.add(playerList.get(i));
		}

		assertTrue(gameService.endOfStartUpPhase(playersWithZeroArmies, playerList));
	}

	/**
	 * This method checks if the GameService endOfReinforcementPhase(Player,
	 * CardExchangeViewModel) method return correct result or not.
	 */
	@Test
	public void testEndOfReinforcementPhase() {
		Player playerOne = new Player();
		playerOne.setArmyCount(10);

		assertFalse(gameService.endOfReinforcementPhase(playerOne, new CardExchangeViewModel(territoriesSet)));

		playerOne.setArmyCount(0);
		assertTrue(gameService.endOfReinforcementPhase(playerOne, new CardExchangeViewModel(territoriesSet)));
	}

	/**
	 * This method test logic for GameService isGameEnded(Player, int) method.
	 */
	@Test
	public void testIsGameEnded() {
		// Setting context
		Player playerOne = new Player();
		playerOne.setTerritories(territoryList);
		Player playerTwo = new Player();
		playerTwo.setTerritories(Arrays.asList(territoryList.get(0), territoryList.get(1), territoryList.get(2)));

		// asserts
		assertTrue(gameService.isGameEnded(playerOne, territoryList.size()));
		assertFalse(gameService.isGameEnded(playerTwo, territoryList.size()));

	}

	/**
	 * This method will test whether the values entered by user for number of dice
	 * roll for attacker and defender are valid or not.
	 */
	@Test
	public void testValidateSelectedDiceNumber() {
		// Setting context
		Territory attackerTerritory = territoryList.get(0);
		Territory defenderTerritory = territoryList.get(1);
		String attackerTotalDice = "random";
		String defenderTotalDice = "20";
		List<String> errorList = new ArrayList<>();
		String error = "Enter valid number of dice for attacker and defender.";
		gameService.validateSelectedDiceNumber(attackerTerritory, defenderTerritory, attackerTotalDice,
				defenderTotalDice, errorList);

		// asserts
		assertEquals(error, errorList.get(0));
	}

	/**
	 * This method tests SerializePossible method and returns true if it is
	 * successfully able to serialize gameState objects into a file.
	 */

	@Test
	public void testSerializePossible() {
		List<String> errorList = new ArrayList<>();
		boolean ifSerialized = gameService.serialize(fileToSave, continentsSet, territoriesSet, playerList,
				currentPlayer, currentPhase, ifStartUpIsComepleted, playerStrategyMapping,cardExchangeViewModel, errorList);
		assertTrue(ifSerialized);
	}

	/**
	 * This method tests SerializePossible method and returns true if it is not able
	 * to serialize gameState objects into a file.
	 */
	@Test
	public void testSerializeFail() {
		fileToSave = null;
		List<String> errorList = new ArrayList<>();
		boolean ifSerialized = gameService.serialize(fileToSave, continentsSet, territoriesSet, playerList,
				currentPlayer, currentPhase, ifStartUpIsComepleted, playerStrategyMapping,cardExchangeViewModel, errorList);
		assertTrue(!ifSerialized);
	}
}
