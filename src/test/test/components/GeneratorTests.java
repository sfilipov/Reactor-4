package components;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;


public class GeneratorTests {

	Turbine turbine;
	Generator generator;
	
	@Before
	public void setUp() {
		turbine = new Turbine(300);
		generator = new Generator(turbine);
	}
	
	/*
	 * Assets that the getPowerOutput method works by attempting to get it
	 * when it has not been updated, and hence should be 0
	 */
	@Test
	public void checkPowerOutputIsZero() {
		assertTrue(generator.getPowerOutput() == 0);
	}
	
	/*
	 * Asserts that when the turbines Rpm is above 0, the updateState method increases the 
	 * powerOutput variable 
	 */
	@Test
	public void checkPowerOutoutIsAboveZero() {
		turbine.setRpm(2000);
		generator.updateState();
		
		assertTrue(generator.getPowerOutput() > 0);
	}
}
