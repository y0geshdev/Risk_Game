package service;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

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
	public void validateMap(HashSet<Continent> continentsSet, HashSet<Territory> territoriesSet,
			List<String> errorList) {
	}

	/**
	 * 
	 * @param continentTerritoriesMapping:
	 *            HashMap which represent continent and territories mappings.
	 * @param neighbourTerritoriesMapping:
	 *            HashMap which represent mapping between territories.
	 * @return true is file is saved else false.
	 */
	public boolean saveMap(File file, HashSet<Continent> continentsSet, HashSet<Territory> territoriesSet) {

		try {
			PrintWriter writer = new PrintWriter(file);
			// static content to maintain map file format.
			writer.println("[Map]");
			writer.println("image=default.bmp");
			writer.println("wrap=default");
			writer.println("scroll=default");
			writer.println("author=default");
			writer.println("warn=default\n");
			writer.println("[Continents]");

			// write continent data to file.
			for (Continent continent : continentsSet)
				writer.println(continent + "=" + continent.getContinentArmyValue());

			writer.println("\n[Territories]");
			//write territory data.
			for (Territory parentTerritory : territoriesSet) {
				writer.print(parentTerritory + ", 0, 0, " + parentTerritory.getContinent());
				for (Territory childTerritory : parentTerritory.getNeighbourTerritories())
					writer.print(", " + childTerritory);
				writer.println();
			}
			writer.close();
			return true;

		} catch (Exception e) {
			return false;
		}

	}
}
