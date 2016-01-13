package by.matveev.rorty;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.List;


public class Prefs {

    // Keys
    public static final String KEY_PREFS = "by.matveev.rorty.prefs";

    public static final String KEY_LEVEL = KEY_PREFS + "level";

    private static Preferences prefs;

    private static List<PrefsListener> listeners = new ArrayList<>();

    private Prefs() {
    }

    public static void addListener(PrefsListener listener) {
        listeners.add(listener);
    }

    public static void purge() {
        listeners.clear();
    }

    public static void init() {
        prefs = Gdx.app.getPreferences(KEY_PREFS);
    }

    public static void setString(String key, String value) {
        prefs.putString(key, value);
        prefs.flush();
        notifyChanged(key);
    }

    public static void setInt(String key, int value) {
        prefs.putInteger(key, value);
        prefs.flush();
        notifyChanged(key);
    }

    public static void setLong(String key, long value) {
        prefs.putLong(key, value);
        prefs.flush();
        notifyChanged(key);
    }

    public static long getLong(String key, long fallback) {
        try {
            return prefs.getLong(key, fallback);
        } catch (Throwable t) {

        }
        return fallback;
    }

    public static void toggle(String key) {
        setBoolean(key, !getBoolean(key));
    }

    public static void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
        prefs.flush();
        notifyChanged(key);
    }

    public static boolean getBoolean(String key) {
        return prefs.getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean fallback) {
        return prefs.getBoolean(key, fallback);
    }

    public static String getString(String key) {
        return prefs.getString(key);
    }

    public static String getString(String key, String fallback) {
        return prefs == null ? fallback : prefs.getString(key, fallback);
    }

    public static int getInt(String key, int fallback) {
        try {
            return prefs.getInteger(key, fallback);
        } catch (Throwable t) {

        }
        return fallback;
    }


    private static void notifyChanged(String key) {
        for (PrefsListener listener : listeners) {
            listener.changed(key);
        }
    }

    public static void removeListener(PrefsListener listener) {
        listeners.add(listener);
    }

    public interface PrefsListener {
        public void changed(String key);
    }

}