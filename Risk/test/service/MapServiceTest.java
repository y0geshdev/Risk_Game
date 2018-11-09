package service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import controller.MapController;
import domain.Continent;
import domain.Territory;

/**
 * This Junit Test Class will have all the test cases for {@link MapService}
 * class.
 * 
 * @author Yogesh
 *
 */
public class MapServiceTest {

	MapService mapserviceObj = new MapService();
	Set<Continent> continentsSet;
	Set<Territory> territoriesSet;
	Continent continent1, continent2;
	Territory territory1, territory2, territory3, territory4;
	List<String> errorList;
	List<Territory> territoryList1;
	List<Territory> territoryList2;
	List<Territory> territoryList3;
	String errorMessage_noContinent_terrExists;
	String errorMessage_hasOneTerritory;
	String errorMessage_terrPresentInMultipleContinents;
	String errorMessage_noNeighbouringTerritory;
	String errorMessage_unconnectedGraph;
	File nullFileObject, correctFileObject, wrongFileObject;

	/**
	 * This method setup require common context before every test is run.
	 */
	@Before
	public void setUp() {
		errorMessage_noContinent_terrExists = new String();
		errorMessage_hasOneTerritory = new String();
		errorMessage_terrPresentInMultipleContinents = new String();
		errorMessage_noNeighbouringTerritory = new String();
		errorMessage_unconnectedGraph = new String();
		errorList = new ArrayList<String>();
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
	 * {@link MapService#validateMap(Set, Set, List)} method validate that each
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
	 * {@link MapService#validateMap(Set, Set, List)} method validate that each
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
	 * {@link MapService#validateMap(Set, Set, List)} method validate that whether
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
	 * {@link MapService#validateMap(Set, Set, List)} method validate that each
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
	 * {@link MapService#validateMap(Set, Set, List)} method validate that map is
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

}
