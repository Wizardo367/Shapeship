package harishbhagat.shapeship.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HighScoreTest
{
    @Test
    public void isZeroWhenNothingIsSaved()
    {
        assertEquals(0, HighScore.read(new FakePreferences()));
    }

    @Test
    public void readsAScoreSavedByAnOlderVersion()
    {
        final FakePreferences preferences = new FakePreferences();
        preferences.values.put(HighScore.OLD_KEY, 120);
        assertEquals(120, HighScore.read(preferences));
    }

    @Test
    public void savesAHigherScoreUnderTheNewKeyAndDropsTheOldOne()
    {
        final FakePreferences preferences = new FakePreferences();
        preferences.values.put(HighScore.OLD_KEY, 120);

        assertTrue(HighScore.saveIfHigher(preferences, 130));

        assertEquals(130, preferences.values.get(HighScore.KEY));
        assertFalse(preferences.values.containsKey(HighScore.OLD_KEY));
    }

    @Test
    public void keepsTheSavedScoreWhenTheNewOneIsNotHigher()
    {
        final FakePreferences preferences = new FakePreferences();
        preferences.values.put(HighScore.KEY, 200);

        assertFalse(HighScore.saveIfHigher(preferences, 200));
        assertFalse(HighScore.saveIfHigher(preferences, 150));

        assertEquals(200, preferences.values.get(HighScore.KEY));
    }

    /** An in-memory SharedPreferences that only supports ints. */
    private static final class FakePreferences implements SharedPreferences
    {
        final Map<String, Object> values = new HashMap<>();

        @Override public int getInt(String key, int defValue) { return values.containsKey(key) ? (Integer) values.get(key) : defValue; }
        @Override public boolean contains(String key) { return values.containsKey(key); }
        @Override public Map<String, ?> getAll() { return values; }
        @Override public Editor edit() { return new FakeEditor(); }

        @Override public String getString(String key, String defValue) { throw new UnsupportedOperationException(); }
        @Override public Set<String> getStringSet(String key, Set<String> defValues) { throw new UnsupportedOperationException(); }
        @Override public long getLong(String key, long defValue) { throw new UnsupportedOperationException(); }
        @Override public float getFloat(String key, float defValue) { throw new UnsupportedOperationException(); }
        @Override public boolean getBoolean(String key, boolean defValue) { throw new UnsupportedOperationException(); }
        @Override public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }
        @Override public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }

        private final class FakeEditor implements Editor
        {
            private final Map<String, Object> puts = new HashMap<>();
            private final Set<String> removals = new java.util.HashSet<>();

            @Override public Editor putInt(String key, int value) { puts.put(key, value); return this; }
            @Override public Editor remove(String key) { removals.add(key); return this; }
            @Override public boolean commit() { apply(); return true; }
            @Override public void apply() { values.keySet().removeAll(removals); values.putAll(puts); }

            @Override public Editor putString(String key, String value) { throw new UnsupportedOperationException(); }
            @Override public Editor putStringSet(String key, Set<String> values) { throw new UnsupportedOperationException(); }
            @Override public Editor putLong(String key, long value) { throw new UnsupportedOperationException(); }
            @Override public Editor putFloat(String key, float value) { throw new UnsupportedOperationException(); }
            @Override public Editor putBoolean(String key, boolean value) { throw new UnsupportedOperationException(); }
            @Override public Editor clear() { throw new UnsupportedOperationException(); }
        }
    }
}
