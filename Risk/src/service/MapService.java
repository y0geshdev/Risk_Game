package service;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.MapController;
import domain.Continent;
import domain.Territory;

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
	public List<String> validateMap(Set<Continent> continentObjectSet, Set<Territory> territoryObjectSet,
			List<String> errorList) {

		String errorMessage = new String();
		Map<String, Boolean> ifTerritoryInContinent = new HashMap<>();

		Iterator<Continent> ite = continentObjectSet.iterator();
		Iterator<Territory> iteTerritory = territoryObjectSet.iterator();

		while (ite.hasNext()) {
			Continent contObject = ite.next();
			if (contObject.getTerritories().size() < 1) {
				errorMessage = "Continent " + contObject.getName() + " Does Not Have Any Territory in it";
				errorList.add(errorMessage);
				break;
			}
			List<Territory> territoryList = contObject.getTerritories();
			for (int i = 0; i < territoryList.size(); i++) {

				if (ifTerritoryInContinent.get(territoryList.get(i).getName()) != null) {
					errorMessage = "Territory " + territoryList.get(i).getName()
							+ " is present in more than one Continent";
					errorList.add(errorMessage);
					break;
				} else {
					ifTerritoryInContinent.put(territoryList.get(i).getName(), true);
				}
			}
		}

		while (iteTerritory.hasNext()) {
			Territory territoryObject = iteTerritory.next();
			if (territoryObject.getNeighbourTerritories().size() < 1) {
				errorMessage = "Territory	" + territoryObject.getName() + " does not have any neighbouring territory";
				errorList.add(errorMessage);
				break;
			}
		}
		iteTerritory = territoryObjectSet.iterator();
		Set<Territory> testingQueue = new HashSet<>();

		checkConnectedGraph(iteTerritory.next().getNeighbourTerritories(), testingQueue, territoryObjectSet.size());
		if (testingQueue.size() == territoryObjectSet.size()) {
			errorMessage = "The graph you entered is unconnected";
			errorList.add(errorMessage);

		}
		return errorList;

	}

	private void checkConnectedGraph(List<Territory> territoryList, Set<Territory> queueForChecking, int size) {

		for (int i = 0; i < territoryList.size(); i++) {

			if (!queueForChecking.contains(territoryList.get(i))) {
				queueForChecking.add(territoryList.get(i));
				checkConnectedGraph(territoryList.get(i).getNeighbourTerritories(), queueForChecking, size);
			}
		}

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
}
