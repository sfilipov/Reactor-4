package components;

/**
 * Critical components have health and can't be repaired. 
 * 
 * Once the health of a critical component is less than or equal to 0,
 * the critical component is broken and the game is over. Currently only
 * the condenser and the reactor are critical components.
 * 
 * @author Simeon
 *
 */
public abstract class CriticalComponent extends PlantComponent {
	private int health;
	private int temperature;
	private int pressure;
	private int waterVolume;
	private int steamVolume;
	
	CriticalComponent(int waterVolume) {
		super(true); //pressurised
		
		this.health = 100;
		this.temperature = 50;
		this.pressure = 0;
		this.steamVolume = 0;
		
		this.waterVolume = waterVolume;
	}
	
	public int getHealth() {
		return health;
	}
	
	protected void setHealth(int health) {
		this.health = health;
	}
	
	public int getTemperature() {
		return temperature;
	}
	
	protected void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	public int getPressure() {
		return pressure;
	}
	
	protected void setPressure(int pressure) {
		this.pressure = pressure;
	}
	
	public int getWaterVolume() {
		return waterVolume;
	}
	
	protected void setWaterVolume(int waterVolume) {
		this.waterVolume = waterVolume;
	}
	
	public int getSteamVolume()
	{
		return steamVolume;
	}
	
	protected void setSteamVolume(int steamVolume) {
		this.steamVolume = steamVolume;
	}
}
