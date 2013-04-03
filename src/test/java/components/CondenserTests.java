package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import model.FlowType;

import org.junit.Before;
import org.junit.Test;

public class CondenserTests {

	private Condenser condenser;
	private Pump coolantPump;

	@Before
	public void setUp() {
		coolantPump = new Pump(1);
		condenser = new Condenser(coolantPump);
		setupWithZeroFlowInOrOut();
	}
	
//	@Test
//	public void updateWaterVolume_shouldIncreaseWaterVolumeByCorrectAmount() {
//		int deltaVolume = 1234;
//		int initialWaterVolume = condenser.getWaterVolume();
//		condenser.updateWaterVolume(deltaVolume);
//		int finalWaterVolume = condenser.getWaterVolume();
//		assertEquals(deltaVolume, finalWaterVolume - initialWaterVolume);
//	}
	
	@Test
	public void updateSteamVolume_shouldIncreaseSteamVolumeByCorrectAmount() {
		int deltaVolume = 1234;
		int initialSteamVolume = condenser.getSteamVolume();
		condenser.addSteam(deltaVolume);
		int finalSteamVolume = condenser.getSteamVolume();
		assertEquals(deltaVolume, finalSteamVolume - initialSteamVolume);
	}
	
	@Test
	public void getCoolantPump_shouldReturnCorrectPumpReference() {
		Pump coolantPumpTest = new Pump(0);
		Condenser c = new Condenser(coolantPumpTest);
		assertSame(coolantPumpTest, c.getCoolantPump());
	}
	
	@Test
	public void Condenser_shouldCoolWhenNoSteamFlowingIn() {
		setupWithHighTemperatureAndFlowIn();
		
		int startingTemperature = 1000;
		
		while (condenser.getTemperature() < startingTemperature) {
			condenser.addSteam(condenser.getInput().getFlowOut().getRate());
			condenser.updateState();
		}
		
		int currentTemperature = condenser.getTemperature();
		int previousTemperature = 1000000; // Arbitrarily high temperature
		
		for (int i = 0; i < 20; i++) {
			currentTemperature = 0;
			condenser.updateState();
		}
	}
	
	
	// -------- Helper Methods :) --------
	
	private void setupWithHighTemperatureAndFlowIn() {
		Valve input = new Valve(1,FlowType.Steam);
		input.getFlowOut().setRate(1000);
		input.getFlowOut().setTemperature(1000);
		condenser.setInput(input);
		
		Valve output = new Valve(2, FlowType.Water);
		condenser.setOutput(output);
	}
	
	private void setupWithZeroFlowInOrOut() {
		Valve input  = new Valve(1, FlowType.Steam);
		Valve output = new Valve(2, FlowType.Water);
		condenser.setInput(input);
		condenser.setOutput(output);
	}

}
