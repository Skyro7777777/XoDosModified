package com.xodos.x11.extrakeys;

import android.view.View;

import androidx.annotation.NonNull;

import com.xodos.x11.extrakeys.xodosX11ExtraKeysConstants.EXTRA_KEY_DISPLAY_MAPS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link Class} that defines the info needed by {@link xodosExtraKeysView} to x11 the extra key
 * views.
 *
 * The {@code propertiesInfo} passed to the constructors of this class must be json array of arrays.
 * Each array element of the json array will be considered a separate row of keys.
 * Each key can either be simple string that defines the name of the key or a json dict that defines
 * advance info for the key. The syntax can be `'KEY'` or `{key: 'KEY'}`.
 * For example `HOME` or `{key: 'HOME', ...}.
 *
 * In advance json dict mode, the key can also be a sequence of space separated keys instead of one
 * key. This can be done by replacing `key` key/value pair of the dict with a `macro` key/value pair.
 * The syntax is `{macro: 'KEY COMBINATION'}`. For example {macro: 'HOME RIGHT', ...}.
 *
 * In advance json dict mode, you can define a nested json dict with the `popup` key which will be
 * used as the popup key and will be triggered on swipe up. The syntax can be
 * `{key: 'KEY', popup: 'POPUP_KEY'}` or `{key: 'KEY', popup: {macro: 'KEY COMBINATION', x11: 'Key combo'}}`.
 * For example `{key: 'HOME', popup: {KEY: 'END', ...}, ...}`.
 *
 * In advance json dict mode, the key can also have a custom x11 name that can be used as the
 * text to x11 on the button by defining the `x11` key. The syntax is `{x11: 'DISPLAY'}`.
 * For example {x11: 'Custom name', ...}.
 *
 * Examples:
 * {@code
 * # Empty:
 * []
 *
 * # Single row:
 * [[ESC, TAB, CTRL, ALT, {key: '-', popup: '|'}, DOWN, UP]]
 *
 * # 2 row:
 * [['ESC','/',{key: '-', popup: '|'},'HOME','UP','END','PGUP'],
 * ['TAB','CTRL','ALT','LEFT','DOWN','RIGHT','PGDN']]
 *
 * # Advance:
 * [[
 *   {key: ESC, popup: {macro: "CTRL f d", x11: "tmux exit"}},
 *   {key: CTRL, popup: {macro: "CTRL f BKSP", x11: "tmux ←"}},
 *   {key: ALT, popup: {macro: "CTRL f TAB", x11: "tmux →"}},
 *   {key: TAB, popup: {macro: "ALT a", x11: A-a}},
 *   {key: LEFT, popup: HOME},
 *   {key: DOWN, popup: PGDN},
 *   {key: UP, popup: PGUP},
 *   {key: RIGHT, popup: END},
 *   {macro: "ALT j", x11: A-j, popup: {macro: "ALT g", x11: A-g}},
 *   {key: KEYBOARD, popup: {macro: "CTRL d", x11: exit}}
 * ]]
 * <p>
 * }
 *
 * Aliases are also allowed for the keys that you can pass as {@code extraKeyAliasMap}. Check
 * {@link xodosX11ExtraKeysConstants#CONTROL_CHARS_ALIASES}.
 * <p>
 * Its up to the {@link xodosExtraKeysView.IExtraKeysView} client on how to handle individual key values
 * of an {@link xodosX11ExtraKeyButton}. They are sent as is via
 * {@link xodosExtraKeysView.IExtraKeysView#onExtraKeyButtonClick(View, xodosX11ExtraKeyButton, Button)}. The
 * {@link com.xodos.x11.utils.xodosX11ExtraKeys} which is an implementation of the interface,
 * checks if the key is one of {@link xodosX11ExtraKeysConstants#PRIMARY_KEY_CODES_FOR_STRINGS} and generates
 * a {@link android.view.KeyEvent} for it, and if its not, then converts the key to code points by
 * calling {@link CharSequence#codePoints()} and passes them to the terminal as literal strings.
 * <p>
 * Examples:
 * {@code
 * "ENTER" will trigger the ENTER keycode
 * "LEFT" will trigger the LEFT keycode and be displayed as "←"
 * "→" will input a "→" character
 * "−" will input a "−" character
 * "-_-" will input the string "-_-"
 * }
 *
 * For more info, check https://wiki.xodos.com/wiki/Touch_Keyboard.
 */
public class xodosX11ExtraKeysInfo {

    /**
     * Matrix of buttons to be displayed in {@link xodosExtraKeysView}.
     */
    private final xodosX11ExtraKeyButton[][] mButtons;

    /**
     * Initialize {@link xodosX11ExtraKeysInfo}.
     *
     * @param propertiesInfo The {@link String} containing the info to create the {@link xodosX11ExtraKeysInfo}.
     *                       Check the class javadoc for details.
     * @param style The style to pass to {@link #getCharDisplayMapForStyle(String)} to get the
     *              {@link xodosX11ExtraKeysConstants.ExtraKeyDisplayMap} that defines the x11 text
     *              mapping for the keys if a custom value is not defined by
     *              {@link xodosX11ExtraKeyButton#KEY_DISPLAY_NAME} for a key.
     * @param extraKeyAliasMap The {@link xodosX11ExtraKeysConstants.ExtraKeyDisplayMap} that defines the
     *                           aliases for the actual key names. You can create your own or
     *                           optionally pass {@link xodosX11ExtraKeysConstants#CONTROL_CHARS_ALIASES}.
     */
    public xodosX11ExtraKeysInfo(@NonNull String propertiesInfo, String style,
                                  @NonNull xodosX11ExtraKeysConstants.ExtraKeyDisplayMap extraKeyAliasMap) throws JSONException {
        mButtons = initExtraKeysInfo(propertiesInfo, getCharDisplayMapForStyle(style), extraKeyAliasMap);
    }

    /**
     * Initialize {@link xodosX11ExtraKeysInfo}.
     *
     * @param propertiesInfo The {@link String} containing the info to create the {@link xodosX11ExtraKeysInfo}.
     *                       Check the class javadoc for details.
     * @param extraKeyDisplayMap The {@link xodosX11ExtraKeysConstants.ExtraKeyDisplayMap} that defines the
     *                           x11 text mapping for the keys if a custom value is not defined
     *                           by {@link xodosX11ExtraKeyButton#KEY_DISPLAY_NAME} for a key. You can create
     *                           your own or optionally pass one of the values defined in
     *                           {@link #getCharDisplayMapForStyle(String)}.
     * @param extraKeyAliasMap The {@link xodosX11ExtraKeysConstants.ExtraKeyDisplayMap} that defines the
     *                           aliases for the actual key names. You can create your own or
     *                           optionally pass {@link xodosX11ExtraKeysConstants#CONTROL_CHARS_ALIASES}.
     */
    public xodosX11ExtraKeysInfo(@NonNull String propertiesInfo,
                                  @NonNull xodosX11ExtraKeysConstants.ExtraKeyDisplayMap extraKeyDisplayMap,
                                  @NonNull xodosX11ExtraKeysConstants.ExtraKeyDisplayMap extraKeyAliasMap) throws JSONException {
        mButtons = initExtraKeysInfo(propertiesInfo, extraKeyDisplayMap, extraKeyAliasMap);
    }

    private xodosX11ExtraKeyButton[][] initExtraKeysInfo(@NonNull String propertiesInfo,
                                                          @NonNull xodosX11ExtraKeysConstants.ExtraKeyDisplayMap extraKeyDisplayMap,
                                                          @NonNull xodosX11ExtraKeysConstants.ExtraKeyDisplayMap extraKeyAliasMap) throws JSONException {
        // Convert String propertiesInfo to Array of Arrays
        JSONArray arr = new JSONArray(propertiesInfo);
        Object[][] matrix = new Object[arr.length()][];
        for (int i = 0; i < arr.length(); i++) {
            JSONArray line = arr.getJSONArray(i);
            matrix[i] = new Object[line.length()];
            for (int j = 0; j < line.length(); j++) {
                matrix[i][j] = line.get(j);
            }
        }

        // convert matrix to buttons
        xodosX11ExtraKeyButton[][] buttons = new xodosX11ExtraKeyButton[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            buttons[i] = new xodosX11ExtraKeyButton[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                Object key = matrix[i][j];

                JSONObject jobject = normalizeKeyConfig(key);

                xodosX11ExtraKeyButton button;

                if (!jobject.has(xodosX11ExtraKeyButton.KEY_POPUP)) {
                    // no popup
                    button = new xodosX11ExtraKeyButton(jobject, extraKeyDisplayMap, extraKeyAliasMap);
                } else {
                    // a popup
                    JSONObject popupJobject = normalizeKeyConfig(jobject.get(xodosX11ExtraKeyButton.KEY_POPUP));
                    xodosX11ExtraKeyButton popup = new xodosX11ExtraKeyButton(popupJobject, extraKeyDisplayMap, extraKeyAliasMap);
                    button = new xodosX11ExtraKeyButton(jobject, popup, extraKeyDisplayMap, extraKeyAliasMap);
                }

                buttons[i][j] = button;
            }
        }

        return buttons;
    }

    /**
     * Convert "value" -> {"key": "value"}. Required by
     * {@link xodosX11ExtraKeyButton#xodosX11ExtraKeyButton(JSONObject, xodosX11ExtraKeyButton, xodosX11ExtraKeysConstants.ExtraKeyDisplayMap, xodosX11ExtraKeysConstants.ExtraKeyDisplayMap)}.
     */
    private static JSONObject normalizeKeyConfig(Object key) throws JSONException {
        JSONObject jobject;
        if (key instanceof String) {
            jobject = new JSONObject();
            jobject.put(xodosX11ExtraKeyButton.KEY_KEY_NAME, key);
        } else if (key instanceof JSONObject) {
            jobject = (JSONObject) key;
        } else {
            throw new JSONException("An key in the extra-key matrix must be a string or an object");
        }
        return jobject;
    }

    public xodosX11ExtraKeyButton[][] getMatrix() {
        return mButtons;
    }

    @NonNull
    public static xodosX11ExtraKeysConstants.ExtraKeyDisplayMap getCharDisplayMapForStyle(String style) {
        switch (style) {
            case "arrows-only":
                return EXTRA_KEY_DISPLAY_MAPS.ARROWS_ONLY_CHAR_DISPLAY;
            case "arrows-all":
                return EXTRA_KEY_DISPLAY_MAPS.LOTS_OF_ARROWS_CHAR_DISPLAY;
            case "all":
                return EXTRA_KEY_DISPLAY_MAPS.FULL_ISO_CHAR_DISPLAY;
            case "none":
                return new xodosX11ExtraKeysConstants.ExtraKeyDisplayMap();
            default:
                return EXTRA_KEY_DISPLAY_MAPS.DEFAULT_CHAR_DISPLAY;
        }
    }
}
