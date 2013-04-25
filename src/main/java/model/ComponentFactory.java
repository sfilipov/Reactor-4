package model;

import java.util.List;

import components.PlantComponent;

public interface ComponentFactory {
	
	List<PlantComponent> createPlantComponents();
	
}
