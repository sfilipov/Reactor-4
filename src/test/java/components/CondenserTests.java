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
		coolantPump.setRpm(coolantPump.getMaxRpm());
		condenser = new Condenser(coolantPump);
	}
	
	@Test
	public void addSteam_positiveAmount_increaseSteamVolumeByAmount() {
		int steamVolumeBefore = condenser.getSteamVolume();
		condenser.addSteam(1000, 0);
		int steamVolumeAfter = condenser.getSteamVolume();
		assertEquals(1000, steamVolumeAfter - steamVolumeBefore);
	}
	
	@Test
	public void updateState_addHotSteamOnce_condenserCoolsItself() {		
		condenser.addSteam(1000, 1000);
		condenser.updateState();
		int temperatureBefore = condenser.getTemperature();
		condenser.updateState();
		int temperatureAfter = condenser.getTemperature();
		
		assertTrue(temperatureBefore > temperatureAfter);
	}
	
	@Test
	public void updateState_addHotSteamOnce_condenseSomeSteamToWater() {
		condenser.addSteam(1000, 1000);
		int steamVolumeBefore = condenser.getSteamVolume();
		int waterVolumeBefore = condenser.getWaterVolume();
		condenser.updateState();
		int steamVolumeAfter = condenser.getSteamVolume();
		int waterVolumeAfter = condenser.getWaterVolume();
		
		assertTrue(steamVolumeBefore > steamVolumeAfter);
		assertTrue(waterVolumeBefore < waterVolumeAfter);
	}
}
