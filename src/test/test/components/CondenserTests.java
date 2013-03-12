package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import model.FlowType;
import model.Plant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import components.Condenser;


import simulator.PlantController;
import simulator.ReactorUtils;

public class CondenserTests {

	private Condenser c;
	private Pump coolantPump;

	@Before
	public void setUp() {
		coolantPump = new Pump(86);
		c = new Condenser(coolantPump);
		setupWithZeroFlowInOrOut();
	}
	
	@After 
	public void tearDown() {
		coolantPump = null;
		c = null;
	}
	
	@Test
	public void updateWaterVolume_shouldIncreaseWaterVolumeByCorrectAmount() {
		
		int deltaVolume = 1234;
		
		int initialWaterVolume = c.getWaterVolume();
		
		c.updateWaterVolume(deltaVolume);
		
		int finalWaterVolume = c.getWaterVolume();
		
		assertEquals(deltaVolume, finalWaterVolume - initialWaterVolume);
	}
	
	@Test
	public void updateSteamVolume_shouldIncreaseSteamVolumeByCorrectAmount() {
		
		int deltaVolume = 1234;
		
		int initialSteamVolume = c.getSteamVolume();
		
		c.updateSteamVolume(deltaVolume);
		
		int finalSteamVolume = c.getSteamVolume();
		
		assertEquals(deltaVolume, finalSteamVolume - initialSteamVolume);
	}
	
	@Test
	public void getCoolantPump_shouldReturnCorrectPumpReference() {
		Pump coolantPumpTest = new Pump(0);
		Condenser c = new Condenser(coolantPumpTest);
		
		assertSame(coolantPumpTest, c.getCoolantPump());
	}
	
	/*
	 * Attempt to break the condenser with a huge amount of hot steam flowing in.
	 */
	@Test
	public void Condenser_shouldBePossibleToBreak() {
		setupWithHighTemperatureAndFlowIn();
		for (int i = 0; i < 100; i++) {
			c.updateSteamVolume(10000);
			c.updateState();
		}
		
		assertTrue(c.checkFailure());
	}
	
	@Test
	public void Condenser_shouldCoolWhenNoSteamFlowingIn() {
		setupWithHighTemperatureAndFlowIn();
		
		int startingTemperature = 1000;
		
		while (c.getTemperature() < startingTemperature) {
			c.updateSteamVolume(c.getInput().getFlowOut().getRate());
			c.updateState();
		}
		
		int currentTemperature = c.getTemperature();
		int previousTemperature = 1000000; // Arbitrarily high temperature
		
		for (int i = 0; i < 20; i++) {
			currentTemperature = 0;
			c.updateState();
		}
	}
	
	
	// -------- Helper Methods :) --------
	
	private void setupWithHighTemperatureAndFlowIn() {
		Valve input = new Valve(10,FlowType.Steam);
		input.getFlowOut().setRate(1000);
		input.getFlowOut().setTemperature(1000);
		c.setInput(input);
		
		Valve output = new Valve(20, FlowType.Water);
		c.setOutput(output);
	}
	
	private void setupWithZeroFlowInOrOut() {
		Valve input = new Valve(10, FlowType.Steam);
		Valve output = new Valve(20, FlowType.Water);
		c.setInput(input);
		c.setOutput(output);
	}

}
