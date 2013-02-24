package sepr.lamprey.reactorcontrol.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sepr.lamprey.reactorcontrol.model.FlowType;
import sepr.lamprey.reactorcontrol.pcomponents.Valve;

public class ValveTest {

	private Valve valve;
	private int valveID = 4;
	private FlowType valveFlowType = FlowType.Water;
	private boolean valveOpen = true;

	@Before
	public void setUp() {
		this.valve = new Valve(this.valveID, this.valveFlowType, this.valveOpen);
	}
	
	@Test
	public void testGetID() {
		
		assertEquals("Result", this.valveID, valve.getID());
		
	}
	
	@Test
	public void testIsOpen() {
		
		assertEquals("Result", valveOpen, valve.isOpen());
		
		valve.setOpen(false);
		
		assertEquals("Result", false, valve.isOpen());
		
	}

}
