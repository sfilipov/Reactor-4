package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.Plant;

import org.junit.Before;
import org.junit.Test;

import simulator.PlantController;

public class OperatingSoftwareTests {
	
	private Plant model;
	private PlantController presenter;
	private OperatingSoftware OS;
	
	@Before
	public void testSetup() {
		model = new Plant();
		presenter = new PlantController(model);
		presenter.newGame("Bob");
		OS = new OperatingSoftware();
	}
	

	//Tests that update state method increases the failure rate
	@Test
	public void testUpdateState(){
	    int initialFailureRate = OS.getFailureRate();
		OS.increaseFailureRate();
		assertTrue(initialFailureRate < OS.getFailureRate());
	}
	
	//Tests that the valves are set correctly
	@Test
	public void testSetValve(){
		OS.setValve(1, true);
		assertEquals(1, OS.getValveID());
		assertTrue(OS.isOpen());
	}
	
	//Tests that the pumps rpm is set correctly
	@Test
	public void testSetPumpRpm(){
		OS.setPumpRpm(1, 10);
		assertEquals(10, OS.getRpm());
	}
	
	@Test
	public void testSetControlRods(){
		OS.setControlRods(50);
		assertEquals(50, OS.getPercentageLowered());
	}
	
	
}
