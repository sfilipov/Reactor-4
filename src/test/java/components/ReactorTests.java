package components;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ReactorTests {
	private Reactor reactor;

	@Before
	public void setUp() {
		reactor = new Reactor();
		reactor.setInput(new ConnectorPipe());
	}
	
	@Test
	public void pumpInWater_pumpedInPositive_addsToWaterVolume() {
		int waterVolume = reactor.getWaterVolume();
		reactor.pumpInWater(100);
		
		assertEquals(waterVolume + 100, reactor.getWaterVolume());
	}	
	
	@Test
	public void pumpInWater_pumpedInZero_sameWaterVolume() {
		int waterVolume = reactor.getWaterVolume();
		reactor.pumpInWater(0);
		
		assertEquals(waterVolume, reactor.getWaterVolume());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void pumpInWater_pumpedInNegative_throwsException() {
		reactor.pumpInWater(-100);
	}
	
	@Test
	public void removeSteam_volumePositive_removesFromSteamVolume() {
		int steamVolume = reactor.getSteamVolume();
		reactor.removeSteam(100);
		
		assertEquals(steamVolume-100, reactor.getSteamVolume());
	}
	
	@Test
	public void removeSteam_volumeZero_sameSteamVolume() {
		int steamVolume = reactor.getSteamVolume();
		reactor.removeSteam(0);
		
		assertEquals(steamVolume, reactor.getSteamVolume());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void removeSteam_volumeNegative_throwsException() {
		reactor.removeSteam(-100);
	}
	
	@Test
	public void quench_notUsed_lowersTemperatureAndPressure() {
		reactor.setTemperature(1500);
		reactor.updateState();
		reactor.updateState();
		
		int beforeTemperature = reactor.getTemperature();
		int beforePressure    = reactor.getPressure();
		
		reactor.quench();
		
		int afterTemperature  = reactor.getTemperature();
		int afterPressure     = reactor.getPressure();
		
		assertTrue(beforeTemperature > afterTemperature);
		assertTrue(beforePressure > afterPressure);
	}
	
	@Test
	public void quench_alreadyUsed_noChangeToTemperatureAndPressure() {
		reactor.setTemperature(1500);
		reactor.quench();
		reactor.updateState();
		reactor.updateState();
		
		int beforeTemperature = reactor.getTemperature();
		int beforePressure    = reactor.getPressure();
		
		reactor.quench();
		
		int afterTemperature  = reactor.getTemperature();
		int afterPressure     = reactor.getPressure();
		
		assertTrue(beforeTemperature == afterTemperature);
		assertTrue(beforePressure == afterPressure);
	}
}
