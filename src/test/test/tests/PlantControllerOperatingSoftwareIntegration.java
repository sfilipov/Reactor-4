package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import model.Plant;
import model.Repair;

import org.junit.Before;
import org.junit.Test;

import pcomponents.OperatingSoftware;
import pcomponents.PlantComponent;
import pcomponents.Pump;
import pcomponents.Reactor;
import pcomponents.Turbine;
import pcomponents.Valve;

import simulator.PlantController;
import simulator.ReactorUtils;

public class PlantControllerOperatingSoftwareIntegration {
	
	private PlantController presenter; 
	private ReactorUtils utils;
	private Plant plant;
    private OperatingSoftware OS;
    private Reactor reactor;
    private Pump pump;
    private Valve valve;
    private Turbine turbine;
	
	@Before
	public void setUp() {
		utils = new ReactorUtils();
		presenter = new PlantController(utils);
		presenter.newGame("Bob");
		plant = presenter.getPlant();
		OS = presenter.getPlant().getOperatingSoftware();
		pump = presenter.getPlant().getPumps().get(0);
		valve = presenter.getPlant().getValves().get(0);
		turbine = presenter.getPlant().getTurbine();
		reactor = presenter.getPlant().getReactor();
	}
	
	@Test
	public void testSetControlRods(){
		reactor.setPercentageLowered(50);
		OS.setControlRods(10);
		presenter.executeStoredCommand();
		assertEquals(10, reactor.getPercentageLowered());
	}
	
	@Test
	public void testSetPumpOnOff(){
		pump.setOn(false);
		OS.setPumpOnOff(1, true);
		presenter.executeStoredCommand();
		assertTrue(pump.isOn());
	}
	
	@Test
	public void testSetPumpRpm(){
		pump.setRpm(0);
		OS.setPumpRpm(1, 20);
		presenter.executeStoredCommand();
		assertEquals(20, pump.getRpm());
	}
	
	@Test
	public void testSetValve(){
		valve.setOpen(false);
		OS.setValve(1, true);
		presenter.executeStoredCommand();
		assertTrue(valve.isOpen());
	}
	
	@Test
	public void testRepairPump(){
		List<PlantComponent> failedComponents = plant.getFailedComponents();
		failedComponents.add(plant.getPumps().get(1));
		List<Repair> expected = plant.getBeingRepaired();
		expected.add(new Repair(pump));
		OS.repairPump(0);
		presenter.executeStoredCommand();
		assertEquals(expected, plant.getBeingRepaired());
	}
	
	@Test 
	public void testRepairTurbine(){
		List<PlantComponent> failedComponents = plant.getFailedComponents();
		failedComponents.add(plant.getTurbine());
		List<Repair> expected = plant.getBeingRepaired();
		expected.add(new Repair(turbine));
		OS.repairTurbine();
		presenter.executeStoredCommand();
		assertEquals(expected, plant.getBeingRepaired());
	}
}
