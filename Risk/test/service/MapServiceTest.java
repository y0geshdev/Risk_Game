package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import controller.MapController;

/**
 * This Junit Test Class will have all the test cases for {@link MapService}
 * class.
 * 
 * @author Yogesh
 *
 */
public class MapServiceTest {

	File nullFileObject, correctFileObject, wrongFileObject;

	@Before
	public void beforEachTestCase() {
		nullFileObject = null;
		correctFileObject = new File("C:\\Users\\pc\\Desktop\\apprisk\\Asiamap.map");
		wrongFileObject = new File("C:\\Users\\pc\\Desktop\\apprisk\\WrongFormatMap.map");
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
