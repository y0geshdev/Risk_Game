package domain;

import javafx.util.Pair;

/**
 * Interface for player strategy.
 * @author Yogesh
 *
 */
public interface IStrategy {

	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies, PhaseViewModel phaseViewModel);
	public void fortify(Player player, Territory from, Territory to, int armiesToMove,  PhaseViewModel phaseViewModel);
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory, Territory defenderTerritory, 
			boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice, PhaseViewModel phaseViewModel);
}
