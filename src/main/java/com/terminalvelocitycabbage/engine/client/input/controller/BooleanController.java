package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;

/**
 * An {@link Controller} useful for input types that are representative of on or off or held, like a fire button, or
 * a bow and arrow hold then release controller.
 */
public abstract non-sealed class BooleanController extends Controller {

    ButtonAction action;
    float amount;

    /**
     * @param action The {@link ButtonAction} that represents the type of control that this controller interprets.
     * @param defaultState The state that this controller is initialized with, usually false or 0
     * @param controls The actual {@link Control}s that inform the state of this controller.
     */
    public BooleanController(ButtonAction action, boolean defaultState, Control[] controls) {
        this.action = action;
        this.amount = defaultState ? 1.0f : 0.0f;
        this.controlGroups = new ControlGroup[1];
        this.controlGroups[0] = new ControlGroup(controls);
    }

    @Override
    public void preProcess() {
        super.preProcess();
        //Before this controller is processed make sure to reset the state to 0
        amount = 0.0f;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex) {
        if (switch (action) {
            case PRESSED -> kkc.isPressed();
            case RELEASED -> kkc.isReleased();
            case REPEAT -> kkc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex) {
        if (switch (action) {
            case PRESSED -> gbc.isPressed();
            case RELEASED -> gbc.isReleased();
            case REPEAT -> gbc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc, int groupIndex) {
        if (switch (action) {
            case PRESSED -> mbc.isPressed();
            case RELEASED -> mbc.isReleased();
            case REPEAT -> mbc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex) {
        if (switch (action) {
            //Make sure to allow for a bit of noise in the control values in case a gamepad axis can't max out.
            case PRESSED -> gpac.getAmount() > 0.95f;
            case RELEASED -> gpac.getAmount() < 0.05f;
            case REPEAT -> gpac.getHoldTime() > 0;
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processMouseMovementControls(MouseMovementControl mmc, int groupIndex) {
        amount = mmc.hasMoved() ? 1.0f : 0.0f;
    }

    @Override
    protected void processMouseScrollControls(MouseScrollControl msc, int groupIndex) {
        amount = msc.hasScrolled() ? 1.0f : 0.0f;
    }

    /**
     * @return Whether this controller is enabled.
     */
    public boolean isEnabled() {
        return amount > 0.95f;
    }
}
