package domain;

import javafx.util.Pair;

/**
 * Strategy for cheater player.
 * 
 * @author Yogesh
 *
 */
public class CheaterStrategy implements IStrategy {

	@Override
	public void reinforcement(Player player, Territory selectedTerritory, int numberOfArmies,
			PhaseViewModel phaseViewModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fortify(Player player, Territory from, Territory to, int armiesToMove, PhaseViewModel phaseViewModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public Pair<Boolean, Integer> attack(Player attacker, Player defender, Territory attackerTerritory,
			Territory defenderTerritory, boolean isAllOutMode, int totalAttackerDice, int totalDefenderDice,
			PhaseViewModel phaseViewModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
