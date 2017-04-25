package harishbhagat.shapeship.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import java.util.HashMap;
import java.util.Vector;

import harishbhagat.shapeship.R;

/**
 * Used to manage music and sound effects.
 *
 * Created by Harish on 24/04/2016.
 */
public class Audio
{
    // Member(s)

    private static Audio instance = new Audio();

    private static HashMap<Sound, Integer> mediaPlayerReferences;
    private static Vector<MediaPlayer> mediaPlayers;

    private static SharedPreferences preferences;

    // Constructor(s)

    /**
     * Default constructor.
     */
    private Audio()
    {
        // Initialisation
        mediaPlayerReferences = new HashMap<Sound, Integer>();
        mediaPlayers = new Vector<MediaPlayer>();
    }

    // Function(s)

    /**
     * Used to retrieve the id number of a sound.
     */
    private static Integer getSoundID(Sound sound)
    {
        if (sound == Sound.BACKGROUND_MUSIC)
            return R.raw.space_trip;
        else if (sound == Sound.EXPLOSION)
            return R.raw.explosion;
        else if (sound == Sound.SHOOT)
             return R.raw.shoot;
        else
            // Error
            return null;
    }

    /**
     * Used to retrieve the class instance
     */
    public static Audio getInstance()
    {
        return instance;
    }

    /**
     * Used to retireve the volume of the background music or general sound effects. Returns 100 if not found.
     */
    public static int getVolume(Context context, AudioType audioType)
    {
        // Get the key
        String key = (audioType== AudioType.MUSIC) ? context.getString(R.string.musicVolumeID) : context.getString(R.string.sfxVolumeID);
        // Get the volume
        preferences = context.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);
        return preferences.getInt(key, 100);
    }

    /**
     * Used to load a sound
     */
    public static boolean loadSound(Context context, Sound sound)
    {
        // Get the sound ID
        Integer soundID = getSoundID(sound);
        if (soundID == null) return false;

        // Load the sound
        mediaPlayers.add(MediaPlayer.create(context, soundID));

        // Reference the media player
        mediaPlayerReferences.put(sound, mediaPlayers.size() - 1);

        return true;
    }

    /**
     * Used to set the volume of the background music or general sound effects.
     */
    public static boolean setVolume(Context context, AudioType audioType, final int volume)
    {
        boolean musicPlayerFound = false;

        // Set the volume
        for (MediaPlayer mediaPlayer : mediaPlayers)
        {
            if (audioType == AudioType.MUSIC)
            {
                if (mediaPlayer == mediaPlayers.get(mediaPlayerReferences.get(Sound.BACKGROUND_MUSIC)))
                {
                    float formattedVolume = (float) volume / 100;
                    mediaPlayer.setVolume(formattedVolume, formattedVolume);
                    musicPlayerFound = true;
                }
                if (musicPlayerFound) break;
            }
            else if (audioType == AudioType.SFX)
            {
                if (mediaPlayer != mediaPlayers.get(mediaPlayerReferences.get(Sound.BACKGROUND_MUSIC)))
                {
                    float formattedVolume = (float) volume / 100;
                    mediaPlayer.setVolume(formattedVolume, formattedVolume);
                }
            }
            if (musicPlayerFound) break;
        }

        return true;
    }

    /** Used to pause a sound. */
    public static boolean pauseSound(Sound sound)
    {
        // Get reference
        final Integer reference = mediaPlayerReferences.get(sound);
        // Check for failure
        if (reference == null) return false;
        // Pause the sound
        mediaPlayers.get(reference).pause();
        return true;
    }

    /** Used to play a pre-loaded sound. */
    public static boolean playSound(Sound sound)
    {
        // Get reference
        final Integer reference = mediaPlayerReferences.get(sound);
        // Check for failure
        if (reference == null) return false;
        // Play the sound
        mediaPlayers.get(reference).start();
        return true;
    }

    /** Used to release a sound from memory. */
    public static boolean releaseSound(Sound sound)
    {
        // Get reference
        final Integer reference = mediaPlayerReferences.get(sound);
        // Check for failure
        if (reference == null) return false;
        // Release the sound
        mediaPlayers.get(reference).release();
        return true;
    }
}
