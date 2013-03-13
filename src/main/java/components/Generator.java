package components;

/**
 * Generator class is used to calculate the power output of the plant
 * based on the current RPM of the Turbine. The generator is perfect,
 * i.e. it never breaks.
 * 
 * @author Lamprey
 */
public class Generator extends PlantComponent {
	private static final long serialVersionUID = -7558087247939142245L;

	private static final int DIVISOR = 123; //Random number to make score look better (not a multiple of 10/100/1000)
	
	private Turbine turbine;
	
	/**
	 * Creates a new non-breakable generator.
	 * 
	 * @param turbine the turbine that the power output calculations are based on
	 */
	public Generator(Turbine turbine) {
		super();
		this.turbine = turbine;
	}
	
	/**
	 * 
	 * @return the power output.
	 */
	public int getPowerOutput() {
		return turbine.getRpm() / DIVISOR;
	}
}
