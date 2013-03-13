package components;

import java.io.Serializable;
import java.util.Random;

import model.Flow;




/**
 * PlantComponent is an abstract class that has fields and methods that apply
 * to most or all plant components. That allows for flexible changes to future
 * implementations (i.e. valves/generator that break randomly).
 * 
 * @author Lamprey
 */
public abstract class PlantComponent implements Serializable {
	private static final long serialVersionUID = -4184587415447732647L;
	public final static boolean DEFAULT_PRESSURISED = false;
	
	private PlantComponent input;
	private PlantComponent output;
	private boolean pressurised;
	private Flow flowOut;
	
	/**
	 * Creates a new PlantComponent object.
	 */
	protected PlantComponent() {
		this.pressurised = DEFAULT_PRESSURISED;
		this.flowOut = new Flow();
	}	
	
	/**
	 * Creates a new PlantComponent object.
	 */
	protected PlantComponent(boolean pressurised) {
		this.pressurised = pressurised;
		this.flowOut = new Flow();
	}
		
	/**
	 * 
	 * @return the component connected to the input of this component.
	 */
	public PlantComponent getInput() {
		return this.input;
	}

	/**
	 * Changes the component connected to the input of this component.
	 * 
	 * @param input the component to connect to the input of this component
	 */
	public void setInput(PlantComponent input) {
		this.input = input;
	}

	/**
	 * 
	 * @return the component connected to the output of this component
	 */
	public PlantComponent getOutput() {
		return this.output;
	}

	/**
	 * Changes the component connected to the output of this component.
	 * 
	 * @param output the component to connect to the output of this component
	 */
	public void setOutput(PlantComponent output) {
		this.output = output;
	}
	
	/**
	 * 
	 * @return true if the component is pressurised (i.e. Reactor or Condenser)
	 */
	public boolean isPressurised() {
		return this.pressurised;
	}

	/**
	 * 
	 * @return the flow object that stores the rate of flow out of this component
	 */
	public Flow getFlowOut() {
		return this.flowOut;
	}
}
