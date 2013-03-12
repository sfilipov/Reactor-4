package components;

import static org.junit.Assert.assertEquals;

import model.FlowType;

import org.junit.Before;
import org.junit.Test;

import components.Valve;



public class ValveTests {

	private Valve valve;
	private int valveID = 4;
	private FlowType valveFlowType = FlowType.Water;
	private boolean valveOpen = true;

	@Before
	public void setUp() {
		this.valve = new Valve(this.valveID, this.valveFlowType, this.valveOpen);
	}
	
	/*
	 * Asserts the getID method returns the same value that the Pump was instantiated with(this.valveId).
	 */
	@Test
	public void testGetIDReturnsTheCorrectValue() {
		
		assertEquals( this.valveID, valve.getID());
		
	}
	
	/*
	 * Asserts that when the valveOpen variable is set to true, the isOpen method returns the same value
	 */
	@Test
	public void testValveCanBeSetToOpen() {		
		
		valve.setOpen(true);		
		assertEquals(true, valve.isOpen());		
	}
	
	/*
	 * Asserts that when the valveOpen variable is set to false, the isOpen method returns the same value
	 */
	@Test
	public void testValveCanBeSetToClosed() {		
		
		valve.setOpen(false);		
		assertEquals(false, valve.isOpen());	
	}	

}
