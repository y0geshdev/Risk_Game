package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Continent;
import domain.GameConstants;
import domain.Territory;
import ui.Render;

/**
 * 
 * @author Kunal
 * @version 1.0.0
 * 
 *          This class is used to parse the .map file. It contains the method to
 *          convert the file data in Territory and Continent Objects.
 *
 */
public class MapFileParser {

	private static Set<Territory> territoryObjectSet = new HashSet<>();
	private static Set<Continent> continentObjectSet = new HashSet<>();
	private static Continent continentObject;
	private static Territory territoryObject;
	private static Territory tempTerritoryObject;
	private static ArrayList<Territory> neighbouringTerritories;
	private static HashMap<String, ArrayList<Territory>> continentToTerritoryMap;
	private static HashMap<String, Territory> ifTerritoryObject;
	private static HashMap<String, Continent> ifContinentObject;
	private static ArrayList<Territory> territoryInAContinentList;

	/**
	 * This method is using the File object in order to access the file and parse
	 * it's contents. It will call the rendering Class method which will render the
	 * map on UI.
	 * 
	 * @param file:
	 *            This object is passed from the <u>class</u> where we choose a
	 *            particular map file.
	 * 
	 * @param ifForValidation
	 *            : This object for checking if this function is being called for
	 *            validation or for starting the game
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Set> parseFile(File file, boolean ifForValidation) throws Exception {

		Map<String, Set> continentAndTerritorySetObjectMap = new HashMap<>();
		BufferedReader bufferedReaderObject = new BufferedReader(new FileReader(file));
		String fileContents;

		while ((fileContents = bufferedReaderObject.readLine()) != null) {

			if (fileContents.equals(GameConstants.CONTINENT_KEY)) {
				ifContinentObject = new HashMap<>();
				fileContents = bufferedReaderObject.readLine();
				do {
					String[] lineContent = fileContents.split("=");
					String continentName = lineContent[0];
					int continentArmyValue = Integer.parseInt(lineContent[1]);
					continentObject = new Continent(continentName, continentArmyValue);
					continentObjectSet.add(continentObject);
					ifContinentObject.put(continentName, continentObject);
					fileContents = bufferedReaderObject.readLine();

				} while (!fileContents.isEmpty());
			}

			if (fileContents.equals(GameConstants.TERRITORY_KEY)) {
				fileContents = bufferedReaderObject.readLine();
				continentToTerritoryMap = new HashMap<>();
				ifTerritoryObject = new HashMap<>();

				do {
					if (fileContents.isEmpty()) {
						fileContents = bufferedReaderObject.readLine();
						continue;
					}
					String[] lineContent = fileContents.split(",");
					String territoryName = lineContent[0];
					String continentName = lineContent[3];
					neighbouringTerritories = new ArrayList<>();
					territoryInAContinentList = new ArrayList<Territory>();

					for (int i = 4; i < lineContent.length; i++) {
						tempTerritoryObject = new Territory(lineContent[i]);
						if (ifTerritoryObject.get(tempTerritoryObject.getName()) == null) {
							ifTerritoryObject.put(tempTerritoryObject.getName(), tempTerritoryObject);
							neighbouringTerritories.add(tempTerritoryObject);
						} else {
							neighbouringTerritories.add(ifTerritoryObject.get(tempTerritoryObject.getName()));
						}

					}
					territoryObject = new Territory(territoryName);
					if (ifTerritoryObject.get(territoryObject.getName()) != null) {
						territoryObject = ifTerritoryObject.get(territoryObject.getName());
						territoryObject.setContinent(ifContinentObject.get(continentName));
						territoryObject.setNeighbourTerritories(neighbouringTerritories);

					} else {
						territoryObject.setName(territoryName);
						territoryObject.setContinent(ifContinentObject.get(continentName));
						territoryObject.setNeighbourTerritories(neighbouringTerritories);
						ifTerritoryObject.put(territoryObject.getName(), territoryObject);
					}
					if (continentToTerritoryMap.get(continentName) != null) {
						continentToTerritoryMap.get(continentName).add(territoryObject);
					} else {
						territoryInAContinentList.add(territoryObject);
						continentToTerritoryMap.put(continentName, territoryInAContinentList);
					}
					territoryObjectSet.add(territoryObject);
					fileContents = bufferedReaderObject.readLine();

				} while (fileContents != null);
			}
		}

		Iterator<Continent> iteratorObject = continentObjectSet.iterator();

		while (iteratorObject.hasNext()) {
			Continent continentToSetTerritories = iteratorObject.next();
			List<Territory> abc	=	continentToTerritoryMap.get(continentToSetTerritories.getName());
			continentToSetTerritories.setTerritories(abc);
		}
		// To check if all the objects had all the values or not. Will Remove it
		// eventually.
		// int i = 1;
		// System.out.println("Continent Ka Naam " + c.getName());
		// System.out.println("Continent Ki Control Value " +
		// c.getContinentArmyValue());
		// System.out.println("Continent Ki Territories " + c.getTerritories());
		// i++;
		// i = 1;
//		 Iterator<Continent>ite = continentObjectSet.iterator();
//		 while (ite.hasNext()) {
//		 Continent t = (Continent) ite.next();
//		 System.out.println("Territory Ka Naam " + t.getName());
//		 System.out.println("Territory Ka Continent " + t.getContinentArmyValue());
//		 System.out.println("Territory Ki Neighbouring Territories " +
//		 t.getTerritories());
////		 i++;
//		 }
		continentAndTerritorySetObjectMap.put(GameConstants.CONTINENT_SET_KEY, continentObjectSet);
		continentAndTerritorySetObjectMap.put(GameConstants.TERRITORY_SET_KEY, territoryObjectSet);
		
		if (!ifForValidation) {
			Render renderObj = new Render();
			renderObj.startGame(continentObjectSet, territoryObjectSet);
		}
		return continentAndTerritorySetObjectMap;
	}

	/**
	 * This method is used for testing purposes to check if the method it has called
	 * is working as expected.
	 * 
	 * @param args:
	 *            arguments from the console. Not used as of now.
	 */
	public static void main(String[] args) {
		File file = new File("C:\\Coding Practice\\connected.map");
		try {
			parseFile(file, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
