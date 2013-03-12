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
	
	/*
	 * Asserts that when the rpm is set to a value above its maximum, it throws an exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSettingRPM_OutOfRange_ThrowsException() {
		
		this.pump.setRpm(pump.getMaxRpm()+1); // argument above MAX_RPM
		
	}
	
	/*
	 * Asserts that when the rpm is set to a value within the valid range, the rpm is updated correctly
	 * and getRpm returns it correctly.
	 */
	@Test
	public void testSetRpm_450_IsAcceptable() {
		
		pump.setRpm(450);		
		assertEquals(450, this.pump.getRpm());
		
	}
	
	/*
	 * Asserts the getID method returns the same value that the Pump was instantiated with(this.valveId).
	 */
	@Test
	public void testGetIDReturnsTheCorrectValue() {
		
		assertEquals(this.pumpID, pump.getID());
		
	}
	
	/*
	 *  Asserts that when the pumps on variable is set to false, the isOn method returns the same value
	 */
	@Test
	public void testPumpCanBeTurnedOff() {
		
		this.pump.setOn(false);
		assertEquals(false, this.pump.isOn());	
	}
	
	/*
	 *  Asserts that when the pumps on variable is set to true, the isOn method returns the same value
	 */
	@Test
	public void testPumpCanBeTurnedOn() {
		
		this.pump.setOn(true);		
		assertEquals(true, this.pump.isOn());
	}
	
	/*
	 * Asserts that when the pump is on, the Rpm is returned as would be expected (the same as it was set to)
	 */
	@Test
	public void testRpmIsCorrectWhenPumpIsOn() {
		pump.setRpm(450);
		pump.setOn(true);
		
		assertEquals(450, pump.getRpm());		
	}
	
	/*
	 *Asserts that when the pump is off, the Rpm returned is 0, as it cannot be pumping when off
	 */
	@Test
	public void testRpmIsZeroWhenPumpIsOff() {
		pump.setRpm(450);
		pump.setOn(false);		//Setting the state to off should mean there can be no Rpm
		
		assertEquals(0, pump.getRpm());		
	}
	
	

}
