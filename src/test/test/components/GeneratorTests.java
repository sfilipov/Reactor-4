package components;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;


public class GeneratorTests {

	Turbine turbine;
	Generator generator;
	
	@Before
	public void setUp() {
		turbine = new Turbine(100);
		generator = new Generator(turbine);
	}
	
	@Test
	public void getPowerOutput_turbineRpmIsZero_powerOutputIsZero() {
		turbine.setRpm(0);
		
		assertTrue(generator.getPowerOutput() == 0);
	}
	
	@Test
	public void getPowerOutput_turbineRpmIsPositive_powerOutputIsPositive() {
		turbine.setRpm(1000);
		
		assertTrue(generator.getPowerOutput() > 0);
	}
}
