package service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This is suite for service layer classes.
 * @author Yogesh
 */
@RunWith(Suite.class)
@SuiteClasses({ GameServiceTest.class, MapServiceTest.class })
public class ServiceSuite {

}
