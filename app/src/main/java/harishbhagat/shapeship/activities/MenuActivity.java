package harishbhagat.shapeship.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.activity.ComponentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Fonts;
import harishbhagat.shapeship.game.HighScore;
import harishbhagat.shapeship.model.ScoreFormat;

public class MenuActivity extends ComponentActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SystemBars.keepContentClear(this);

        // Set the fonts
        final TextView scoreText = findViewById(R.id.scoreText);
        Fonts.apply(Fonts.black(this), findViewById(R.id.titleText), findViewById(R.id.highScoreTitle), scoreText);

        // Get high-score
        SharedPreferences preferences = this.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);
        final int existingScore = HighScore.read(preferences);

        scoreText.setText(ScoreFormat.format(existingScore));
    }

    /** Start the game when the play button is . */
    public void startGame(View view)
    {
        // Free up memory
        this.finish();
        Intent intent = new Intent(this, GameActivity.class);
        // Show the activity
        startActivity(intent);
    }

    /** Show the help screen when the help button is clicked. */
    public void showHelp(View view)
    {
        // Free up memory
        this.finish();
        Intent intent = new Intent(this, HelpActivity.class);
        // Show the activity
        startActivity(intent);
    }

    /** Show the settings screen when the settings button is clicked. */
    public void showSettings(View view)
    {
        // Free up memory
        this.finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        // Show the activity
        startActivity(intent);
    }
}
