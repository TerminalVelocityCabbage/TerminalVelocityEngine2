package com.terminalvelocitycabbage.templates.inputcontrollers;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.controller.BooleanController;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.templates.events.UIClickEvent;
import org.joml.Vector2f;

public class UIClickController extends BooleanController {

    private final MouseInput.Button button;
    private final Control[] controls;

    public UIClickController(MouseInput.Button button, Control... controls) {
        super(ButtonAction.PRESSED, false, controls);
        this.button = button;
        this.controls = controls;
    }

    @Override
    public void act() {
        if (isEnabled()) {
            boolean justPressed = false;
            for (Control control : controls) {
                if (control.isPressed() && !control.isHolding()) {
                    justPressed = true;
                    break;
                }
            }

            if (justPressed) {
                var listener = ClientBase.getInstance().getInputCallbackListener();
                Vector2f pos = new Vector2f((float)listener.getMouseX(), (float)listener.getMouseY());
                ClientBase.getInstance().getEventDispatcher().dispatchEvent(new UIClickEvent(pos, button));
            }
        }
    }
}
