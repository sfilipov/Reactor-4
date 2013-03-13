package model;

import static org.junit.Assert.assertEquals;
import org.mockito.Mockito;

import org.junit.Before;
import org.junit.Test;

import components.FailableComponent;
import components.PlantComponent;


public class RepairTests {
	FailableComponent componentToRepair;
	
	@Before
	public void setUp() {
		componentToRepair = Mockito.mock(FailableComponent.class);
	}
	
	@Test
	public void decTimeStepsRemaining_zeroStepsUntilRepair_zeroStepsRemaining() {
		Mockito.when(componentToRepair.getRepairTime()).thenReturn(0);
		Repair repair = new Repair(componentToRepair);
		repair.decTimeStepsRemaining();
		
		assertEquals(0, repair.getTimeStepsRemaining());
	}
	
	@Test
	public void decTimeStepsRemaining_oneStepUntilRepair_zeroStepsRemaining() {
		Mockito.when(componentToRepair.getRepairTime()).thenReturn(1);
		Repair repair = new Repair(componentToRepair);
		repair.decTimeStepsRemaining();
		
		assertEquals(0, repair.getTimeStepsRemaining());
	}
	
	@Test
	public void decTimeStepsRemaining_twoStepsUntilRepair_oneStepRemaining() {
		Mockito.when(componentToRepair.getRepairTime()).thenReturn(2);
		Repair repair = new Repair(componentToRepair);
		repair.decTimeStepsRemaining();
		
		assertEquals(1, repair.getTimeStepsRemaining());
	}
}
