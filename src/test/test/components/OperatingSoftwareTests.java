package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import components.OperatingSoftware;


import simulator.PlantController;
import simulator.ReactorUtils;

public class OperatingSoftwareTests {
	private PlantController presenter; 
	private ReactorUtils utils;
	private OperatingSoftware OS;
	
	@Before
	public void testSetup() {
		utils = new ReactorUtils();
		presenter = new PlantController(utils);
		presenter.newGame("Bob");
		OS = new OperatingSoftware();
	}
	

	//Tests that update state method increases the failure rate
	@Test
	public void testUpdateState(){
	    int initialFailureRate = OS.getFailureRate();
		OS.updateState();
		assertTrue(initialFailureRate < OS.getFailureRate());
	}
	
	//Tests that the valves are set correctly
	@Test
	public void testSetValve(){
		OS.setValve(1, true);
		assertEquals(1, OS.getValveID());
		assertTrue(OS.isOpen());
	}
	
	//Tests that the pumps are set correctly
	@Test
	public void testSetPumpOnOff(){
		OS.setPumpOnOff(1, true);
		assertEquals(1, OS.getPumpID());
		assertTrue(OS.isOn());
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
