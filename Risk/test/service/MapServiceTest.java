package service;

import static org.junit.Assert.assertEquals;
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
import domain.Continent;
import domain.HumanStrategy;
import domain.Player;
import domain.PlayerStrategyEnum;
import domain.Territory;

/**
 * This Junit Test Class will have all the test cases for {@link MapService}
 * class.
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
	 * Variable holds reference to error message string if no continent or territory exist
	 */
	String errorMessage_noContinent_terrExists;
	/**
	 * Variable holds reference to error message string if one territory exist
	 */
	String errorMessage_hasOneTerritory;
	/**
	 * Variable holds reference to error message string if one territory present
	 * in multiple continents
	 */
	String errorMessage_terrPresentInMultipleContinents;
	/**
	 * Variable holds reference to error message string if one territory has
	 * no neighbouring territory
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
		//setup error message 
		errorMessage_noContinent_terrExists = new String();
		errorMessage_hasOneTerritory = new String();
		errorMessage_terrPresentInMultipleContinents = new String();
		errorMessage_noNeighbouringTerritory = new String();
		errorMessage_unconnectedGraph = new String();
		errorList = new ArrayList<String>();
		//setup map
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
	 * This Test method validate map and check for logic where
	 * MapService validateMap(Set, Set, List) method validate that each
	 * continent have one territory atleast.
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
	 * This Test method validate map and check for logic where
	 * MapService validateMap(Set, Set, List) method validate that each
	 * territory is in single continent.
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
	 * This Test method validate map and check for logic where
	 * MapService validateMap(Set, Set, List) method validate that whether
	 * there is any continent or territory exits or not.
	 */
	@Test
	public void testValidateMapCaseThree() {
		errorMessage_noContinent_terrExists = "No Continent or Territory Exist";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_noContinent_terrExists));
	}

	/**
	 * This Test method validate map and check for logic where
	 * MapService validateMap(Set, Set, List) method validate that each
	 * territory have atleast one neighbor.
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
	 * This Test method validate map and check for logic where
	 * MapService validateMap(Set, Set, List) method validate that map is
	 * connected or not.
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
		
		//check if file is not found		
		List<String> errorList = new ArrayList<>();
		String errorString = "File not Found";
		mapserviceObj.parseFile(nullFileObject, errorList);
		assertTrue(errorList.contains(errorString));
				
		//Check if all checks pass
		errorList = new ArrayList<>();
		mapserviceObj.parseFile(correctFileObject, errorList);
		assertTrue(MapController.continentsSet.size() >= 1);
		assertTrue(MapController.territoriesSet.size() >= 1);
		
		//Checks if file doesn't contain continent and territories
		
		errorList = new ArrayList<>();
		mapserviceObj.parseFile(wrongFileObject, errorList);
		String errorMessageForZeroContinent = "No Continent is present in the File";
		String errorMessageForZeroTerritory = "No Territory is present in the File";
		assertTrue(errorList.contains(errorMessageForZeroContinent));

		}
	
	/**
	 * This method test MapService createPlayers(List, int) method which
	 * should populate playerList which is passed as one of the argument with as
	 * many players as denoted by numberOfplayers argument.
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
	 * This method tests the deserialize(File,errorList) method from MapService class
	 * which should not populate errors in case if it deserializes game state
	 * objects successfully from given file
	 */
	@Test
	public void testDeserializePass() {
		File fileToSave = null;
		Set<Continent> continentSet = new HashSet<>();
		Set<Territory> territorySet = new HashSet<>();
		List<Player> playerList = new ArrayList<>();
		Player currentPlayer = new Player();
		String currentPhase = null;
		Boolean ifStartUpIsComepleted = false;
		Map<Player, PlayerStrategyEnum> playerStrategyMapping = new HashMap<>();
		List<String> errorList = new ArrayList<>();
		fileToSave = new File("resource\\CheckSerialize.ser");

		generateSavedGameState(continentSet, territorySet, playerList, currentPlayer, currentPhase,
				ifStartUpIsComepleted, playerStrategyMapping, errorList);
		assertTrue(errorList.size() == 0);
	}

	/**
	 * This method tests the deserialize(File,errorList) method from MapService class
	 * which should populate errors in case if it fails to deserialize game state
	 * objects
	 */
	@Test
	public void testDeserializeFail() {
		File fileToSave = null;
		Set<Continent> continentSet = new HashSet<>();
		Set<Territory> territorySet = new HashSet<>();
		List<Player> playerList = new ArrayList<>();
		Player currentPlayer = new Player();
		String currentPhase = null;
		Boolean ifStartUpIsComepleted = false;
		Map<Player, PlayerStrategyEnum> playerStrategyMapping = new HashMap<>();
		List<String> errorList = new ArrayList<>();

		generateSavedGameState(continentSet, territorySet, playerList, currentPlayer, currentPhase,
				ifStartUpIsComepleted, playerStrategyMapping, errorList);
		fileToSave = null;
		mapserviceObj.deserialize(fileToSave, errorList);
		assertTrue(errorList.size() != 0);
	}

	public void generateSavedGameState(Set<Continent> continentsSet, Set<Territory> territoriesSet,
			List<Player> playerList, Player currentPlayer, String currentPhase, Boolean ifStartUpIsComepleted,
			Map<Player, PlayerStrategyEnum> playerStrategyMapping, List<String> errorList) {

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

		List<Territory> territoryList = new ArrayList<>();
		List<Continent> continentList = new ArrayList<>();

		Collections.addAll(continentsSet, continentOne, continentTwo);
		Collections.addAll(territoriesSet, territoryOne, territoryTwo, territoryThree, territoryFour, territoryFive);

		Collections.addAll(continentList, continentOne, continentTwo);
		Collections.addAll(territoryList, territoryOne, territoryTwo, territoryThree, territoryFour, territoryFive);

		Collections.addAll(continentOne.getTerritories(), territoryOne, territoryTwo);
		Collections.addAll(continentTwo.getTerritories(), territoryThree, territoryFour, territoryFive);

		MapController.territoriesSet = (HashSet<Territory>) territoriesSet;
		MapController.continentsSet = (HashSet<Continent>) continentsSet;

		p1.setArmyCount(12);
		p1.setPlayingStrategy(new HumanStrategy());
		p1.setTerritories(Arrays.asList(territoryOne, territoryTwo));

		p2.setArmyCount(10);
		p2.setPlayingStrategy(new AggressiveStrategy());
		p2.setTerritories(Arrays.asList(territoryThree, territoryFour));

		p3.setArmyCount(10);
		p3.setPlayingStrategy(new HumanStrategy());
		p3.setTerritories(Arrays.asList(territoryFive));

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
	}


}
