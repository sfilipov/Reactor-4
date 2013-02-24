package sepr.lamprey.reactorcontrol.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import sepr.lamprey.reactorcontrol.pcomponents.Pump;
import sepr.lamprey.reactorcontrol.simulator.PlantController;
import sepr.lamprey.reactorcontrol.simulator.ReactorUtils;

public class PlantComponentTest {

	private PlantController presenter; 
	private ReactorUtils utils;
    private Pump pump;
	
	@Before
	public void setUp() {
		utils = new ReactorUtils();
		presenter = new PlantController(utils);
		pump = presenter.getPlant().getPumps().get(0);
	}
	//need to set get failure rate to public for testing
	@Test
	public void testFailureRate() {
		int initialFailureRate = pump.getFailureRate();
		pump.increaseFailureRate();	
		int currentFailureRate = pump.getFailureRate();
		assertTrue(initialFailureRate < currentFailureRate);
	}
	

}
