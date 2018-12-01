package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import controller.MapController;
import domain.AggressiveStrategy;
import domain.CheaterStrategy;
import domain.Continent;
import domain.GameObjectClass;
import domain.Player;
import domain.PlayerStrategyEnum;
import domain.Territory;

/**
 * This Junit Test Class will have all the test cases for MapService class.
 * 
 * @author Yogesh
 *
 */
public class MapServiceTest {
	/**
	 * Variable holds reference to mapservice class
	 */
	MapService mapserviceObj = new MapService();
	/**
	 * Variable holds reference to set of continent
	 */
	Set<Continent> continentsSet;
	/**
	 * Variable holds reference to set of territories
	 */
	Set<Territory> territoriesSet;
	/**
	 * Variable holds reference to continents
	 */
	Continent continent1, continent2;
	/**
	 * Variable holds reference to territories
	 */
	Territory territory1, territory2, territory3, territory4;
	/**
	 * Variable holds reference to list of errors
	 */
	List<String> errorList;
	/**
	 * Variable holds reference to list of territories
	 */
	List<Territory> territoryList1, territoryList2, territoryList3;
	/**
	 * Variable holds reference to error message string if no continent or territory
	 * exist
	 */
	String errorMessage_noContinent_terrExists;
	/**
	 * Variable holds reference to error message string if one territory exist
	 */
	String errorMessage_hasOneTerritory;
	/**
	 * Variable holds reference to error message string if one territory present in
	 * multiple continents
	 */
	String errorMessage_terrPresentInMultipleContinents;
	/**
	 * Variable holds reference to error message string if one territory has no
	 * neighbouring territory
	 */
	String errorMessage_noNeighbouringTerritory;
	/**
	 * Variable holds reference to error message string if graph is not connected
	 */
	String errorMessage_unconnectedGraph;
	/**
	 * Variables holds reference to file
	 */
	File nullFileObject, correctFileObject, wrongFileObject;

	/**
	 * This method setup require common context before every test is run.
	 */
	@Before
	public void setUp() {
		// setup error message
		errorMessage_noContinent_terrExists = new String();
		errorMessage_hasOneTerritory = new String();
		errorMessage_terrPresentInMultipleContinents = new String();
		errorMessage_noNeighbouringTerritory = new String();
		errorMessage_unconnectedGraph = new String();
		errorList = new ArrayList<String>();
		// setup map
		continentsSet = new HashSet<>();
		territoriesSet = new HashSet<>();
		territoryList1 = new ArrayList<Territory>();
		territoryList2 = new ArrayList<Territory>();
		territoryList3 = new ArrayList<Territory>();
		territory1 = new Territory("Delhi");
		territory2 = new Territory("Bhopal");
		territory3 = new Territory("Ludhiana");
		territory4 = new Territory("Patiala");
		continent1 = new Continent("Asia");
		continent2 = new Continent("Africa");
		nullFileObject = null;
		correctFileObject = new File("resource\\Asiamap.map");
		wrongFileObject = new File("resource\\WrongFormatMap.map");
	}

	/**
	 * This Test method validate map and check for logic where MapService
	 * validateMap(Set, Set, List) method validate that each continent have one
	 * territory atleast.
	 */
	@Test
	public void testValidateMapCaseOne() {
		territoryList1.add(territory1);
		territoryList1.add(territory2);
		territoryList2.add(territory3);
		territoryList2.add(territory4);
		territoriesSet.addAll(territoryList1);
		territoriesSet.addAll(territoryList2);
		continentsSet.add(continent1);
		continentsSet.add(continent2);
		continent2.setTerritories(territoryList2);
		errorMessage_hasOneTerritory = "Asia Does Not Have Any Territory in it";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_hasOneTerritory));

	}

	/**
	 * This Test method validate map and check for logic where MapService
	 * validateMap(Set, Set, List) method validate that each territory is in single
	 * continent.
	 * 
	 */
	@Test
	public void testValidateMapCaseTwo() {

		territoryList1.add(territory1);
		territoriesSet.addAll(territoryList1);
		continentsSet.add(continent1);
		continentsSet.add(continent2);
		errorMessage_terrPresentInMultipleContinents = "Territory Delhi is present in more than one Continent";
		continent1.setTerritories(territoryList1);
		continent2.setTerritories(territoryList1);
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_terrPresentInMultipleContinents));
	}

	/**
	 * This Test method validate map and check for logic where MapService
	 * validateMap(Set, Set, List) method validate that whether there is any
	 * continent or territory exits or not.
	 */
	@Test
	public void testValidateMapCaseThree() {
		errorMessage_noContinent_terrExists = "No Continent or Territory Exist";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_noContinent_terrExists));
	}

	/**
	 * This Test method validate map and check for logic where MapService
	 * validateMap(Set, Set, List) method validate that each territory have atleast
	 * one neighbor.
	 */
	@Test
	public void testValidateMapCaseFour() {
		territoryList1.add(territory1);
		territoriesSet.addAll(territoryList1);
		continentsSet.add(continent1);
		continentsSet.add(continent2);
		continent1.setTerritories(territoryList1);
		errorMessage_noNeighbouringTerritory = "Delhi does not have any neighbouring territory";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_noNeighbouringTerritory));
	}

	/**
	 * This Test method validate map and check for logic where MapService
	 * validateMap(Set, Set, List) method validate that map is connected or not.
	 */
	@Test
	public void testValidateMapCaseFive() {
		territoryList1.add(territory1);
		territoryList1.add(territory2);
		territoryList2.add(territory3);
		territoryList2.add(territory4);
		territoryList3.add(territory2);
		territoryList3.add(territory3);
		territoriesSet.addAll(territoryList1);
		territoriesSet.addAll(territoryList2);
		territory1.setNeighbourTerritories(territoryList3);
		continentsSet.add(continent1);
		continentsSet.add(continent2);
		errorMessage_unconnectedGraph = "The graph you entered is unconnected";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_unconnectedGraph));

	}

	/**
	 * Tests parseFile() method for different type of map file Objects
	 * 
	 */
	@Test
	public void testParseFile() {

		// check if file is not found
		List<String> errorList = new ArrayList<>();
		String errorString = "File not Found";
		mapserviceObj.parseFile(nullFileObject, errorList);
		assertTrue(errorList.contains(errorString));

		// Check if all checks pass
		errorList = new ArrayList<>();
		mapserviceObj.parseFile(correctFileObject, errorList);
		assertTrue(MapController.continentsSet.size() >= 1);
		assertTrue(MapController.territoriesSet.size() >= 1);

		// Checks if file doesn't contain continent and territories

		errorList = new ArrayList<>();
		mapserviceObj.parseFile(wrongFileObject, errorList);
		String errorMessageForZeroContinent = "No Continent is present in the File";
		String errorMessageForZeroTerritory = "No Territory is present in the File";
		assertTrue(errorList.contains(errorMessageForZeroContinent));

	}

	/**
	 * This method test MapService createPlayers(List, int) method which should
	 * populate playerList which is passed as one of the argument with as many
	 * players as denoted by numberOfplayers argument.
	 * 
	 */
	@Test
	public void testCreatePlayers() {
		List<Player> playerList = new ArrayList<>();
		int numberOfplayers = 5;
		mapserviceObj.createPlayers(playerList, numberOfplayers);
		assertEquals(numberOfplayers, playerList.size());
	}

	/**
	 * This method tests the deserialize(File,errorList) method from MapService
	 * class which should not populate errors in case if it deserializes game state
	 * objects successfully from given file
	 */
	@Test
	public void testDeserializePass() {
		File fileToSave = null;
		String currentPhase = "reinforcementPhase";
		GameObjectClass gameStateObj = null;
		List<String> errorList = new ArrayList<>();
		fileToSave = new File("resource\\CheckSerialize.ser");
		gameStateObj = mapserviceObj.deserialize(fileToSave, errorList);
		assertTrue(errorList.size() == 0);
		assertTrue(gameStateObj.getCurrentPhase().equalsIgnoreCase(currentPhase));

	}

	/**
	 * This method tests the deserialize(File,errorList) method from MapService
	 * class which should populate errors in case if it fails to deserialize game
	 * state objects
	 */
	@Test
	public void testDeserializeFail() {
		File fileToSave = null;
		List<String> errorList = new ArrayList<>();
		fileToSave = null;
		mapserviceObj.deserialize(fileToSave, errorList);
		assertTrue(errorList.size() != 0);
	}

	/**
	 * This method returns true as number of drawMoves is between 10 and 50.
	 */
	@Test
	public void testvalidateTournamentModeVariablesPass() {
		int noOfGames = 2;
		int drawMoves = 20;
		int noOfPlayers = 2;
		List<String> errorList = new ArrayList<>();
		mapserviceObj.validateTournamentModeVariables(drawMoves, noOfGames, noOfPlayers, errorList);

		assertTrue(errorList.size() == 0);
	}

	/**
	 * This method will return true as size of errorList is not zero because value
	 * of drawMoves is greater is greater than 50.
	 */
	@Test
	public void testvalidateTournamentModeVariablesFail() {
		int noOfGames = 2;
		int drawMoves = 60;
		int noOfPlayers = 2;
		List<String> errorList = new ArrayList<>();
		mapserviceObj.validateTournamentModeVariables(drawMoves, noOfGames, noOfPlayers, errorList);

		assertTrue(errorList.size() != 0);
	}

	/**
	 * This method validates the player strategy mapping with the player. Returns
	 * true if errorList is empty.
	 */
	@Test
	public void validatePlayerStrategyMappingForTMPass() {
		List<Player> playerList = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		Map<Player, PlayerStrategyEnum> playerStrategyMapping = new HashMap<>();

		Player p1 = new Player(new AggressiveStrategy());
		Player p2 = new Player(new CheaterStrategy());
		playerList.add(p1);
		playerList.add(p2);

		playerStrategyMapping.put(p1, PlayerStrategyEnum.AGGRESSIVE);
		playerStrategyMapping.put(p2, PlayerStrategyEnum.CHEATER);

		mapserviceObj.validatePlayerStrategyMappingForTM(playerList, errorList, playerStrategyMapping);

		assertTrue(errorList.size() == 0);
	}

	/**
	 * This method validates the player strategy mapping with the player. Returns
	 * true if errorList is not empty.
	 */
	@Test
	public void validatePlayerStrategyMappingForTMFail() {
		List<Player> playerList = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		Map<Player, PlayerStrategyEnum> playerStrategyMapping = new HashMap<>();

		Player p1 = new Player(new AggressiveStrategy());
		Player p2 = new Player(new CheaterStrategy());
		playerList.add(p1);
		playerList.add(p2);

		playerStrategyMapping.put(p1, PlayerStrategyEnum.AGGRESSIVE);
		playerStrategyMapping.put(p2, PlayerStrategyEnum.HUMAN);

		mapserviceObj.validatePlayerStrategyMappingForTM(playerList, errorList, playerStrategyMapping);

		assertTrue(errorList.size() != 0);
	}
}
