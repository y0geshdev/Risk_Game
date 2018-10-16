package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
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

	@Before
	public void beforEachTestCase() {
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
		correctFileObject = new File("C:\\Users\\pc\\Desktop\\apprisk\\Asiamap.map");
		wrongFileObject = new File("C:\\Users\\pc\\Desktop\\apprisk\\WrongFormatMap.map");
	}
	/**
	 * Test Method to test that each Continent has at least 1 territory in it.
	 * 
	 */

	@Test
	public void testTerritoriesinContinent() {
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
	 * Test Method to test that each territory is present in only one Continent.
	 * 
	 */
	@Test
	public void territoryinSingleContinent() {

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
	 * Test Method to validate the condition when no Continent or territory exist.
	 */
	@Test
	public void noContinents_territoriesExist() {
		errorMessage_noContinent_terrExists = "No Continent or Territory Exist";
		mapserviceObj.validateMap(continentsSet, territoriesSet, errorList);

		assertTrue(errorList.contains(errorMessage_noContinent_terrExists));
	}

	/**
	 * Test method to validate that each territory should have a neighbouring
	 * territory.
	 */
	@Test
	public void testIsNeighbouringTerritoryPresent() {
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
	 * Test method to check that whether the graph is connected or not.
	 */
	@Test
	public void testGraphIsConnected() {
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


	@Ignore
	public void testValidateMap() {
		fail("Not yet implemented");
	}

	@Ignore
	public void testSaveMap() {
		fail("Not yet implemented");
	}

	/**
	 * Tests parseFile() method for different type of map file Objects
	 * @param errorMessage represents display message for different errors
	 */ 
 
@Test
	public void testParseFile() {
		MapService mp = new MapService();
		try {
			mp.parseFile(nullFileObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String errorMessage = "File not Found";
			assertEquals(errorMessage, e.getMessage());
			e.printStackTrace();
		}

		try {
			mp.parseFile(correctFileObject);
			assertTrue(MapController.continentsSet.size() >= 1);
			assertTrue(MapController.territoriesSet.size() >= 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mp.parseFile(wrongFileObject);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			String errorMessageForZeroContinent = "No Continent is present in the File";
			assertEquals(errorMessageForZeroContinent, e.getMessage());
			String errorMessageForZeroTerritory = "No Territory is present in the File";
			assertEquals(errorMessageForZeroTerritory, e.getMessage());

			e.printStackTrace();
		}
	}

}
