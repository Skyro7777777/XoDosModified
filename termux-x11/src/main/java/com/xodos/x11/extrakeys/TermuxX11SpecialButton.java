package com.xodos.x11.extrakeys;

import androidx.annotation.NonNull;

import java.util.HashMap;

/** The {@link Class} that implements special buttons for {@link xodosExtraKeysView}. */
public class xodosX11SpecialButton {

    private static final HashMap<String, xodosX11SpecialButton> map = new HashMap<>();

    public static final xodosX11SpecialButton CTRL = new xodosX11SpecialButton("CTRL");
    public static final xodosX11SpecialButton ALT = new xodosX11SpecialButton("ALT");
    public static final xodosX11SpecialButton SHIFT = new xodosX11SpecialButton("SHIFT");
    public static final xodosX11SpecialButton META = new xodosX11SpecialButton("META");
    public static final xodosX11SpecialButton FN = new xodosX11SpecialButton("FN");

    /** The special button key. */
    private final String key;

    /**
     * Initialize a {@link xodosX11SpecialButton}.
     *
     * @param key The unique key name for the special button. The key is registered in {@link #map}
     *            with which the {@link xodosX11SpecialButton} can be retrieved via a call to
     *            {@link #valueOf(String)}.
     */
    public xodosX11SpecialButton(@NonNull final String key) {
        this.key = key;
        map.put(key, this);
    }

    /** Get {@link #key} for this {@link xodosX11SpecialButton}. */
    public String getKey() {
        return key;
    }

    /**
     * Get the {@link xodosX11SpecialButton} for {@code key}.
     *
     * @param key The unique key name for the special button.
     */
    public static xodosX11SpecialButton valueOf(String key) {
        return map.get(key);
    }

    @NonNull
    @Override
    public String toString() {
        return key;
    }

}
