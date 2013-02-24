package tests;

import static org.junit.Assert.assertEquals;

import model.Plant;

import org.junit.Before;
import org.junit.Test;

import pcomponents.Condenser;
import pcomponents.Reactor;
import pcomponents.Turbine;

import simulator.PlantController;
import simulator.ReactorUtils;


public class ComponentFailures {

	private PlantController presenter; 
	private ReactorUtils utils;
	private Plant plant;
	private Turbine turbine;
	private Condenser condenser;
	private Reactor reactor;
	
	
	@Before
	public void setUp() {
		utils = new ReactorUtils();
		presenter = new PlantController(utils);
		presenter.newGame("Bob");
		plant = presenter.getPlant();
		reactor = presenter.getPlant().getReactor();
		turbine = presenter.getPlant().getTurbine();
		condenser = presenter.getPlant().getCondenser();
	}
	
	//13.1
	@Test 
	public void reactorFail(){
		reactor.setTemperature(3000);
		reactor.setPressure(2500);
		presenter.step(1);
		assertEquals(90, reactor.getHealth());
	}

	//13.1
	@Test
	public void condenserFail(){
		condenser.setTemperature(3000);
		condenser.setPressure(2500);
		presenter.step(1);
		assertEquals(90, condenser.getHealth());
	}
	
	//6.2
	@Test
	public void turbineFail(){
	   turbine.setRpm(10);	
	   plant.addFailedComponent(turbine);
	   presenter.step(1);
	   assertEquals(0, turbine.getRpm());
	}
}
