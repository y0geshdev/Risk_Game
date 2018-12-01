package domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This is a suite for Domain model classes.
 * 
 * @author Yogesh
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ WorldDominationModelTest.class, HumanStrategyTest.class, CheaterStrategyTest.class,
		RandomStrategyTest.class, BenevolentStrategyTest.class, AggressiveStrategyTest.class })
public class DomainTestSuite {

}
