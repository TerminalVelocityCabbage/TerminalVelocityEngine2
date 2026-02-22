package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.MouseButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.MouseScrollControl;
import com.terminalvelocitycabbage.engine.client.input.controller.ControlGroup;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.templates.events.InputHandlerRegistrationEvent;
import com.terminalvelocitycabbage.templates.inputcontrollers.UIClickController;
import com.terminalvelocitycabbage.templates.inputcontrollers.UIScrollController;

import static com.terminalvelocitycabbage.editor.TerminalVelocityEngineEditor.ID;

public class EditorInput {

    public static void init(EventDispatcher eventDispatcher) {

        eventDispatcher.listenToEvent(InputHandlerRegistrationEvent.EVENT, e -> {
            InputHandlerRegistrationEvent event = (InputHandlerRegistrationEvent) e;

            var inputHandler = event.getInputHandler();

            Control leftClickControl = inputHandler.registerControlListener(new MouseButtonControl(MouseInput.Button.LEFT_CLICK));
            Control mouseScrollUpControl = inputHandler.registerControlListener(new MouseScrollControl(MouseInput.ScrollDirection.UP, 1f));
            Control mouseScrollDownControl = inputHandler.registerControlListener(new MouseScrollControl(MouseInput.ScrollDirection.DOWN, 1f));

            inputHandler.registerController(ID, "ui_click", new UIClickController(MouseInput.Button.LEFT_CLICK, leftClickControl));
            inputHandler.registerController(ID, "scroll", new UIScrollController(
                    new ControlGroup(mouseScrollUpControl),
                    new ControlGroup(mouseScrollDownControl)
            ));
        });
    }

}
