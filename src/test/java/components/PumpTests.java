package components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import components.Pump;



public class PumpTests {

	private Pump pump;
	private int pumpID = 4;

	@Before
	public void setUp() {
		this.pump = new Pump(pumpID);
	}	
	
	@Test(expected = IllegalArgumentException.class)
	public void setRpm_RPM_OutOfRange_ThrowsException() {
		
		this.pump.setRpm(pump.getMaxRpm()+1); // argument above MAX_RPM
		
	}
	
	@Test
	public void setRpm_100_RpmIs100() {
		
		pump.setRpm(100);		//A value within the valid range, to check the setRpm works
		assertEquals(100, this.pump.getRpm());
		
	}

}
