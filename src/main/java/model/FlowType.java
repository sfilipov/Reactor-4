package model;

import java.io.Serializable;


/** The type of flow that circulates in different parts of the plant.
 *  Steam - from the reactor to the condenser
 *  Water - from the condenser to the reactor
 *  
 *  @author Lamprey
 */
public enum FlowType implements Serializable {
	Water, Steam;
}
