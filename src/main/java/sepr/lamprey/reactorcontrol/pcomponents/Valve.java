package sepr.lamprey.reactorcontrol.pcomponents;

import sepr.lamprey.reactorcontrol.model.FlowType;


/**
 * Valve is a component that doesn't randomly fail (currently).
 * It doesn't do anything on its own - it is used in the system to
 * control the flow (stop the flow).
 * 
 * @author Lamprey
 */
public class Valve extends PlantComponent {
	private static final long serialVersionUID = -4238759395307525755L;
	
	private final static int DEFAULT_FAILURE_RATE = 0;
	private final static int DEFAULT_REPAIR_RATE  = 0;
	private final static int MAX_FAILURE_RATE     = 0;
	private final static boolean DEFAULT_OPEN_STATE = true;
	
	private final static int MAX_STEAM_FLOW = 300; // Maximum steam flow through allowed. 
	
	private boolean open;
	private int ID;
	
	/**
	 * Creates a new valve that is connected to the specified
	 * two plant components.
	 * 
	 * @param ID 	valve ID
	 * @param type  the type of flow - water/steam 
	 */
	public Valve (int ID, FlowType type) {
		super(DEFAULT_FAILURE_RATE, DEFAULT_REPAIR_RATE, MAX_FAILURE_RATE);
		this.getFlowOut().setType(type);
		this.ID = ID;
		this.open = DEFAULT_OPEN_STATE;
	}
	
	/**
	 * Creates a new valve and sets it's position (open/closed)
	 * 
	 * @param ID 	valve ID
	 * @param type  the type of flow - water/steam 
	 * @param open true if the valve is open
	 */
	public Valve (int ID, FlowType type, boolean open) {
		super(DEFAULT_FAILURE_RATE, DEFAULT_REPAIR_RATE, MAX_FAILURE_RATE);
		this.getFlowOut().setType(type);
		this.ID = ID;
		this.open = open;
	}
	
	/**
	 *
	 * @return ID of this valve
	 */
	public int getID()
	{
		return ID;
	}

	/**
	 * 
	 * @return true if the valve is open
	 */
	public boolean isOpen() {
		return open;
	}
	
	/**
	 * Set the state of the valve.
	 * 
	 * @param open true to open the valve, false to close it
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	/**
	 * 
	 * @return the maximum amount that can flow per turn.
	 */
	public int getMaxSteamFlow() {
		return MAX_STEAM_FLOW;
	}
	
	/**
	 * Does nothing (valves don't have anything to update).
	 */
	@Override
	public void updateState() {
		// Just chill like a valve.
		// (Valves do nothing themselves)
	}
	
	/**
	 * Does nothing (valves don't fail).
	 * 
	 * @return
	 */
	@Override
	public boolean checkFailure() {
		return super.checkFailure();
		//Insert implementation 
	}	
	
}
