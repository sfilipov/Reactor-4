package components;

public interface ForcedFailableComponent {
	
	/* Returns the number of steps left until this component can be forcefully failed
	 * (By the opponent player). 
	 */
	int numStepsUntilFailable();
	
	// Returns true if the component can be failed.
	boolean isForceFailable();

}
