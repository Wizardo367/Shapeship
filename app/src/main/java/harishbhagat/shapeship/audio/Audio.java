package harishbhagat.shapeship.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.EnumMap;
import java.util.Map;

import harishbhagat.shapeship.R;

/**
 * Plays the background music and the sound effects.
 *
 * The sound effects are short, so they're decoded once into a SoundPool, which plays them with
 * little delay and can overlap them. They stay loaded for the life of the app, so an explosion
 * that starts as the game ends still finishes on the menu. The music streams from a MediaPlayer,
 * which the game releases when it closes.
 *
 * Created by Harish on 24/04/2016.
 */
public final class Audio
{
    // --- Member(s)

    /** The most sound effects that play at once. */
    private static final int MAX_STREAMS = 4;

    // Holds the application context only, which lives as long as the app does
    @SuppressLint("StaticFieldLeak")
    private static Audio instance;

    private final Context context;
    private final SoundPool soundPool;
    private final Map<Sound, Integer> soundIds = new EnumMap<>(Sound.class);
    private MediaPlayer music;

    // Set on the UI thread and read on the game thread
    private volatile float musicVolume = 1;
    private volatile float effectsVolume = 1;

    // --- Constructor(s)

    private Audio(final Context context)
    {
        this.context = context;
        final AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(MAX_STREAMS).setAudioAttributes(attributes).build();
        soundIds.put(Sound.EXPLOSION, soundPool.load(context, R.raw.explosion, 1));
        soundIds.put(Sound.SHOOT, soundPool.load(context, R.raw.shoot, 1));
    }

    // --- Function(s)

    /** Returns the app's audio, loading the sound effects the first time. */
    public static synchronized Audio get(final Context context)
    {
        if (instance == null) instance = new Audio(context.getApplicationContext());
        return instance;
    }

    /** Converts a volume setting from 0 to 100 into a player volume from 0 to 1. */
    static float toPlayerVolume(final int percent)
    {
        return Math.max(0, Math.min(percent, 100)) / 100f;
    }

    // --- Method(s)

    /** Reads the music and sound effect volumes chosen in Settings; both default to 100. */
    public void loadVolumes(final SharedPreferences preferences)
    {
        musicVolume = toPlayerVolume(preferences.getInt(context.getString(R.string.musicVolumeID), 100));
        effectsVolume = toPlayerVolume(preferences.getInt(context.getString(R.string.sfxVolumeID), 100));
        if (music != null) music.setVolume(musicVolume, musicVolume);
    }

    /** Plays a sound effect. One that hasn't finished loading is skipped. */
    public void play(final Sound sound)
    {
        final Integer id = soundIds.get(sound);
        if (id != null) soundPool.play(id, effectsVolume, effectsVolume, 1, 0, 1f);
    }

    /** Starts the background music, or carries on from where it was paused. It loops. */
    public void startMusic()
    {
        if (music == null)
        {
            music = MediaPlayer.create(context, R.raw.space_trip);
            if (music == null) return;
            music.setLooping(true);
        }
        music.setVolume(musicVolume, musicVolume);
        music.start();
    }

    /** Pauses the background music. */
    public void pauseMusic()
    {
        if (music != null && music.isPlaying()) music.pause();
    }

    /** Stops the background music and frees its player; the next game starts the track again. */
    public void releaseMusic()
    {
        if (music == null) return;
        music.release();
        music = null;
    }
}
