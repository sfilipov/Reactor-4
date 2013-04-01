package components;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class GeneratorTests {

	Turbine turbine;
	Generator generator;
	
	@Before
	public void setUp() {
		turbine = mock(Turbine.class);
		generator = new Generator(turbine);
	}
	
	@Test
	public void getPowerOutput_turbineRpmIsZero_powerOutputIsZero() {
		when(turbine.getRpm()).thenReturn(0);
		
		assertTrue(generator.getPowerOutput() == 0);
	}
	
	@Test
	public void getPowerOutput_turbineRpmIsPositive_powerOutputIsPositive() {
		when(turbine.getRpm()).thenReturn(1000);
		
		assertTrue(generator.getPowerOutput() > 0);
	}
}
