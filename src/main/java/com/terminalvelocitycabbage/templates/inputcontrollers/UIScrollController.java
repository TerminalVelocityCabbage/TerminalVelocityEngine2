package com.terminalvelocitycabbage.templates.inputcontrollers;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.controller.ControlGroup;
import com.terminalvelocitycabbage.engine.client.input.controller.GroupedController2f;
import com.terminalvelocitycabbage.templates.events.UIScrollEvent;
import org.joml.Vector2f;

public class UIScrollController extends GroupedController2f {

    public UIScrollController(ControlGroup up, ControlGroup down) {
        super(up, down);
    }

    @Override
    public void act() {
        if (getSummedAmount() == 0) return;
        ClientBase.getInstance().getEventDispatcher().dispatchEvent(new UIScrollEvent(new Vector2f(0, getSummedAmount())));
    }
}
