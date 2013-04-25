package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import components.Condenser;
import components.ConnectorPipe;
import components.Generator;
import components.OperatingSoftware;
import components.PlantComponent;
import components.Pump;
import components.Reactor;
import components.Turbine;
import components.Valve;

public class ModelComponentFactory implements ComponentFactory, Serializable {
	private static final long serialVersionUID = 1L;
	
	private Reactor reactor;
	private Condenser condenser;
	private Turbine turbine;
	private Generator generator;
	private Valve steamValve1;
	private Valve steamValve2;
	private Pump pump1;
	private Pump pump2;
	private Pump coolantPump;
	private ConnectorPipe connectorPipe1;
	private ConnectorPipe connectorPipe2;
	private ConnectorPipe connectorPipe3;
	private ConnectorPipe connectorPipe4;
	private OperatingSoftware operatingSoftware;
	
	@Override
	public List<PlantComponent> createPlantComponents() {
		createComponents();
		setupComponentsReferences();
		return makeComponentList();
	}
	
	private void createComponents() {
		steamValve1 = new Valve(1, FlowType.Steam);
		steamValve2 = new Valve(2, FlowType.Steam);
		turbine = new Turbine(steamValve1.getMaxSteamFlow());
		generator = new Generator(turbine);
		pump1 = new Pump(1);
		pump2 = new Pump(2);
		coolantPump = new Pump(3);
		condenser = new Condenser(coolantPump);
		reactor = new Reactor();
		connectorPipe1 = new ConnectorPipe();
		connectorPipe2 = new ConnectorPipe();
		connectorPipe3 = new ConnectorPipe();
		connectorPipe4 = new ConnectorPipe();
		operatingSoftware = new OperatingSoftware();
	}
	
	private void setupComponentsReferences() {
		setupInputOutputReferences(reactor, connectorPipe1);
		setupInputOutputReferences(connectorPipe1, steamValve1);
		setupInputOutputReferences(connectorPipe1, steamValve2);
		setupInputOutputReferences(steamValve1, turbine);
		setupInputOutputReferences(turbine, connectorPipe2);
		setupInputOutputReferences(steamValve2, connectorPipe2); 
		setupInputOutputReferences(connectorPipe2, condenser);
		setupInputOutputReferences(condenser, connectorPipe3);
		setupInputOutputReferences(connectorPipe3, pump1);
		setupInputOutputReferences(connectorPipe3, pump2);
		setupInputOutputReferences(pump1, connectorPipe4);
		setupInputOutputReferences(pump2, connectorPipe4);
		setupInputOutputReferences(connectorPipe4, reactor);
	}
	
	/**
	 * 
	 * @return a list of the plant components
	 */
	private List<PlantComponent> makeComponentList()
	{
		List<PlantComponent> plantComponents = new ArrayList<PlantComponent>();
		plantComponents.add(reactor);
		plantComponents.add(condenser);
		plantComponents.add(turbine); 
		plantComponents.add(generator);
		plantComponents.add(steamValve1);
		plantComponents.add(steamValve2);
		plantComponents.add(pump1);
		plantComponents.add(pump2);
		plantComponents.add(condenser.getCoolantPump());
		plantComponents.add(connectorPipe1);
		plantComponents.add(connectorPipe2);
		plantComponents.add(connectorPipe3);
		plantComponents.add(connectorPipe4);
		plantComponents.add(operatingSoftware);
		return plantComponents;
	}
	
	/**
	 * Takes two PlantComponents and creates the input/output references between them.
	 * 
	 * @param from PlantComponent that flow is coming out of
	 * @param to PlantComponent that flow is moving into
	 */
	private void setupInputOutputReferences(PlantComponent from, PlantComponent to) {
		if (from instanceof ConnectorPipe) {
			((ConnectorPipe) from).addOutput(to);
		} else {
			from.setOutput(to);
		}
		if (to instanceof ConnectorPipe) {
			((ConnectorPipe) to).addInput(from);
		} else {
			to.setInput(from);
		}
	}
}
