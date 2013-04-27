package simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import model.PlantModel;
import model.Repair;

import org.junit.Before;
import org.junit.Test;

import components.RandomlyFailableComponent;
import components.OperatingSoftware;
import components.Pump;
import components.Reactor;
import components.Turbine;
import components.Valve;

public class PlantControllerOperatingSoftwareIntegration {
	
	private PlantModel model;
	private PlantController controller;
	private PlantModel plant;
    private OperatingSoftware OS;
    private Reactor reactor;
    private Pump pump;
    private Valve valve;
    private Turbine turbine;
	
	@Before
	public void setUp() {
		model = new PlantModel();
		controller = new PlantController(model);
		controller.newGame("Bob");
		plant = controller.getPlant();
		OS = controller.getPlant().getOperatingSoftware();
		pump = controller.getPlant().getPumps().get(0);
		valve = controller.getPlant().getValves().get(0);
		turbine = controller.getPlant().getTurbine();
		reactor = controller.getPlant().getReactor();
	}
	
	@Test
	public void testSetControlRods(){
		reactor.setPercentageLowered(50);
		OS.setControlRods(10);
		controller.executeStoredCommand();
		assertEquals(10, reactor.getPercentageLowered());
	}
	
	@Test
	public void testSetPumpRpm(){
		pump.setRpm(0);
		OS.setPumpRpm(1, 20);
		controller.executeStoredCommand();
		assertEquals(20, pump.getRpm());
	}
	
	@Test
	public void testSetValve(){
		valve.setOpen(false);
		OS.setValve(1, true);
		controller.executeStoredCommand();
		assertTrue(valve.isOpen());
	}
	
	@Test
	public void testRepairPump(){
		List<RandomlyFailableComponent> failedComponents = plant.getFailedComponents();
		failedComponents.add(plant.getPumps().get(1));
		List<Repair> expected = plant.getBeingRepaired();
		expected.add(new Repair(pump));
		OS.repairPump(0);
		controller.executeStoredCommand();
		assertEquals(expected, plant.getBeingRepaired());
	}
	
	@Test 
	public void testRepairTurbine(){
		List<RandomlyFailableComponent> failedComponents = plant.getFailedComponents();
		failedComponents.add(plant.getTurbine());
		List<Repair> expected = plant.getBeingRepaired();
		expected.add(new Repair(turbine));
		OS.repairTurbine();
		controller.executeStoredCommand();
		assertEquals(expected, plant.getBeingRepaired());
	}
}
