package components;

import static org.junit.Assert.assertTrue;
import model.Plant;

import org.junit.Before;
import org.junit.Test;

import simulator.PlantController;

public class PlantComponentTests {

	private Plant model;
	private PlantController presenter;
    private Pump pump;
	
	@Before
	public void setUp() {
		model = new Plant();
		presenter = new PlantController(model);
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
