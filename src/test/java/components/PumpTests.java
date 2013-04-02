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
	
	@Test
	public void getRpm_pumpIsSetOn_RpmIsCorrect() {
		pump.setRpm(100); 		//A Value within the valid range
		pump.setOn(true);
		
		assertEquals(100, pump.getRpm());		
	}
	
	@Test
	public void getRpm_pumpIsSetOff_RpmIsZero() {
		pump.setRpm(100);		//A Value within the valid range
		pump.setOn(false);		//Setting the state to off should mean there can be no Rpm
		
		assertEquals(0, pump.getRpm());		
	}	

}
