import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import controller.ControllerTestSuite;
import domain.DomainTestSuite;
import service.ServiceTestSuite;

/**
 * This is a test suite for whole game.
 * 
 * @author Yogesh
 */
@RunWith(Suite.class)
@SuiteClasses({ ControllerTestSuite.class, DomainTestSuite.class, ServiceTestSuite.class })
public class GameTestSuite {

}
