package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.MapController;
import domain.Continent;
import domain.GameConstants;
import domain.Territory;
import ui.Render;

/**
 * This class is used to handle all the service calls from {@link MapController}
 * class. This is used to implement required business logic.
 * 
 * @author Yogesh
 *
 */
public class MapService {

	/**
	 * This method is used to validate that whether the map in application memory is
	 * valid map or not.
	 * 
	 * @param continentTerritoriesMapping:
	 *            HashMap which represent continent and territories mappings.
	 * @param neighbourTerritoriesMapping:
	 *            HashMap which represent mapping between territories.
	 * @param errorList:
	 *            An ArrayList to store validation errors.
	 */
	public void validateMap(HashMap<Continent, List<Territory>> continentTerritoriesMapping,
			HashMap<Territory, List<Territory>> neighbourTerritoriesMapping, List<String> errorList) {
	}

	/**
	 * 
	 * @param continentTerritoriesMapping:
	 *            HashMap which represent continent and territories mappings.
	 * @param neighbourTerritoriesMapping:
	 *            HashMap which represent mapping between territories.
	 * @return true is file is saved else false.
	 */
	public boolean saveMap(File file, HashMap<Continent, List<Territory>> continentTerritoriesMapping,
			HashMap<Territory, List<Territory>> neighbourTerritoriesMapping) {
		
		try {
			PrintWriter writer = new PrintWriter(file);
			//static content to maintain map file format.
			writer.println("[Map]");
			writer.println("image=default.bmp");
			writer.println("wrap=default");
			writer.println("scroll=default");
			writer.println("author=default");
			writer.println("warn=default\n");
			writer.println("[Continents]");
			
			//write continent data to file.
			for(Continent continent : continentTerritoriesMapping.keySet())
				writer.println(continent+"="+continent.getContinentArmyValue());
			
			writer.println("\n[Territories]");
			//for(Territory territory : )
			for(Territory parentTerritory : neighbourTerritoriesMapping.keySet()) {
				writer.print(parentTerritory+", 0, 0, "+parentTerritory.getContinent());
				for(Territory childTerritory : neighbourTerritoriesMapping.get(parentTerritory))
					writer.print(", "+childTerritory);
				writer.println();
			}
			writer.close();
			return true;
			
		} catch (Exception e) {
			return false;
		}

	}
	
	/**
	 * This method is using the File object in order to access the file and parse
	 * it's contents. It will call the rendering Class method which will render the
	 * map on UI.
	 * 
	 * 
	 * @author Kunal
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
	public Map<String, Set> parseFile(File file, boolean ifForValidation) throws Exception {
		
		Set<Territory> territoryObjectSet = new HashSet<>();
		Set<Continent> continentObjectSet = new HashSet<>();
		Continent continentObject;
		Territory territoryObject;
		Territory tempTerritoryObject;
		ArrayList<Territory> neighbouringTerritories;
		HashMap<String, ArrayList<Territory>> continentToTerritoryMap = null;
		HashMap<String, Territory> ifTerritoryObject;
		HashMap<String, Continent> ifContinentObject = null;
		ArrayList<Territory> territoryInAContinentList;
		
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
					String continentName;
					if(lineContent.length > 3) {
						continentName = lineContent[3];
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
					}
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
		
		continentAndTerritorySetObjectMap.put(GameConstants.CONTINENT_SET_KEY, continentObjectSet);
		continentAndTerritorySetObjectMap.put(GameConstants.TERRITORY_SET_KEY, territoryObjectSet);
		
		if (!ifForValidation) {
			Render renderObj = new Render();
			renderObj.startGame(continentObjectSet, territoryObjectSet);
		}
		return continentAndTerritorySetObjectMap;
	}
}
