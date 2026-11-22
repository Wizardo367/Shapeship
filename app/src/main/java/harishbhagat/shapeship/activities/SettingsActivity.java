package harishbhagat.shapeship.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.activity.ComponentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Fonts;

public class SettingsActivity extends ComponentActivity implements SeekBar.OnSeekBarChangeListener
{
    // Member(s)

    private boolean musicChanged;
    private boolean sfxChanged;
    private SeekBar musicSeekBar;
    private SeekBar sfxSeekBar;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SystemBars.keepContentClear(this);

        // Variables
        musicChanged = false;
        sfxChanged = false;

        preferences = this.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);

        // Set the fonts
        Fonts.apply(Fonts.black(this), findViewById(R.id.titleText));
        Fonts.apply(Fonts.regular(this),
                findViewById(R.id.musicText), findViewById(R.id.sfxText), findViewById(R.id.helpText));

        // Get music and sfx seekbars
        musicSeekBar = (SeekBar) findViewById(R.id.musicVolume);
        sfxSeekBar = (SeekBar) findViewById(R.id.sfxVolume);

        musicSeekBar.setOnSeekBarChangeListener(this);
        sfxSeekBar.setOnSeekBarChangeListener(this);

        // Set their current status
        final int musicProgress = preferences.getInt(getString(R.string.musicVolumeID), 100);
        final int sfxProgress = preferences.getInt(getString(R.string.sfxVolumeID), 100);

        musicSeekBar.setProgress(musicProgress);
        sfxSeekBar.setProgress(sfxProgress);
    }

    /** Overridden method for handling seekbar value changes. */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        final int seekBarID = seekBar.getId();

        if (seekBarID == R.id.musicVolume)
            musicChanged = true;
        else if (seekBarID == R.id.sfxVolume)
            sfxChanged = true;
    }

    /** Overridden method for handling the start of a seekbar touch. */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    /** Overridden method for handling the end of a seekbar touch. */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        if (musicChanged)
        {
            // Modify preference
            final String musicKey = this.getString(R.string.musicVolumeID);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(musicKey, seekBar.getProgress());
            editor.commit();
            musicChanged = false;
        }
        else if (sfxChanged)
        {
            // Modify preference
            final String sfxKey = this.getString(R.string.sfxVolumeID);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(sfxKey, seekBar.getProgress());
            editor.commit();
            sfxChanged = false;
        }
    }

    /** Returns the user to the main menu. */
    public void returnToMenu(View view)
    {
        // Free up memory
        this.finish();
        Intent intent = new Intent(this, MenuActivity.class);
        // Show the activity
        startActivity(intent);
    }
}
