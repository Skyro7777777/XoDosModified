package com.xodos.x11.extrakeys;

import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/** The {@link Class} that maintains a state of a {@link xodosX11SpecialButton} */
public class xodosX11SpecialButtonState {

    /** If special button has been created for the {@link xodosExtraKeysView}. */
    boolean isCreated = false;
    /** If special button is active. */
    boolean isActive = false;
    /** If special button is locked due to long hold on it and should not be deactivated if its
     * state is read. */
    boolean isLocked = false;

    List<Button> buttons = new ArrayList<>();

    xodosExtraKeysView mxodosExtraKeysView;

    /**
     * Initialize a {@link xodosX11SpecialButtonState} to maintain state of a {@link xodosX11SpecialButton}.
     *
     * @param xodosExtraKeysView The {@link xodosExtraKeysView} instance in which the {@link xodosX11SpecialButton}
     *                      is to be registered.
     */
    public xodosX11SpecialButtonState(xodosExtraKeysView xodosExtraKeysView) {
        mxodosExtraKeysView = xodosExtraKeysView;
    }

    /** Set {@link #isCreated}. */
    public void setIsCreated(boolean value) {
        isCreated = value;
    }

    /** Set {@link #isActive}. */
    public void setIsActive(boolean value) {
        isActive = value;
        for (Button button : buttons) {
            button.setTextColor(value ? mxodosExtraKeysView.getButtonActiveTextColor() : mxodosExtraKeysView.getButtonTextColor());
        }
    }

    /** Set {@link #isLocked}. */
    public void setIsLocked(boolean value) {
        isLocked = value;
    }
}
