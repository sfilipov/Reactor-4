package simulator;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class MultiplayerController {

	// Player 2 key bindings.
	private static final int FAIL_PUMP_1 = KeyEvent.VK_1; // 1
	private static final int FAIL_PUMP_2 = KeyEvent.VK_2; // 2
	private static final int FAIL_PUMP_3 = KeyEvent.VK_3; // 3
	private static final int FAIL_TURBINE = KeyEvent.VK_4; // 4
	private static final int FAIL_OS = KeyEvent.VK_5; // 5

	// Reference to the plant controller for failing components
	// + generally knowing what's going on in there!
	private PlantController plantController;

	public MultiplayerController(PlantController plantController) {
		this.plantController = plantController;
		initialiseGlobalKeyListener();
	}

	/**
	 * Initialises a key press listener for the entire application, not just
	 * specific components.
	 */
	private void initialiseGlobalKeyListener() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(new KeyEventDispatcher() {
					@Override
					public boolean dispatchKeyEvent(KeyEvent event) {
						// Only capture and act upon KEY_PRESSED events.
						if (event.getID() == KeyEvent.KEY_PRESSED)
							keyPressActionHandler(event);
						return false;
					}
				});
	}

	/**
	 * Acts upon KeyEvents that it is passed. ie fails the relevant component.
	 * 
	 * @param event
	 */
	private void keyPressActionHandler(KeyEvent event) {
		if (plantController.getUIData().isMultiplayer()) {
			switch (event.getKeyCode()) {
			case FAIL_PUMP_1:
				plantController.failPump(1);
				break;
			case FAIL_PUMP_2:
				plantController.failPump(2);
				break;
			case FAIL_PUMP_3:
				plantController.failPump(3);
				break;
			case FAIL_TURBINE:
				plantController.failTurbine();
				break;
			case FAIL_OS:
				plantController.failOS();
				break;
			default:
				break;
			}
		}
	}

}
