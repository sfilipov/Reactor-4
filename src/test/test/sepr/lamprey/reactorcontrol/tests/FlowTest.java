package sepr.lamprey.reactorcontrol.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sepr.lamprey.reactorcontrol.model.Flow;
import sepr.lamprey.reactorcontrol.model.FlowType;

public class FlowTest {

	
	private Flow flow;

	@Before
	public void setUp() {	
		flow = new Flow();
	}
	
	@Test
	public void testSetGetRate() {
		
		flow.setRate(5);
		
		assertEquals("Result", 5, flow.getRate());
		
	}
	
	@Test
	public void testSetGetTemperature() {
		
		flow.setTemperature(45);
		
		assertEquals("Result", 45, flow.getTemperature());
		
	}
	
	@Test
	public void testSetGetType() {
		
		FlowType flowType = FlowType.Water;
		
		flow.setType(flowType);
		
		assertEquals("Result", FlowType.Water, flow.getType());
		
	}

}
