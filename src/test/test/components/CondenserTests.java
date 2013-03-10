package components;

import static org.junit.Assert.assertEquals;

import model.Plant;

import org.junit.Before;
import org.junit.Test;

import components.Condenser;


import simulator.PlantController;
import simulator.ReactorUtils;

public class CondenserTests {

	private Condenser condenser;

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testUpdateWaterVolume() {
		
		int currentWaterVolume = condenser.getWaterVolume();
		
		condenser.updateWaterVolume(300);
		
		assertEquals("Result", (currentWaterVolume+300), condenser.getWaterVolume());
		
	}
		
	@Test
	public void testUpdateSteamVolume() {
		
		int currentSteamVolume = condenser.getSteamVolume();
		
		condenser.updateSteamVolume(300);
		
		assertEquals("Result", (currentSteamVolume+300), condenser.getSteamVolume());
		
	}

}
