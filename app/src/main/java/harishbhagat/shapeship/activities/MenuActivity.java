package harishbhagat.shapeship.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

import harishbhagat.shapeship.R;

public class MenuActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set the fonts
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/seguibl.ttf");

        TextView titleText = (TextView) findViewById(R.id.titleText);
        TextView highScoreTitleText = (TextView) findViewById(R.id.highScoreTitle);
        TextView scoreText = (TextView) findViewById(R.id.scoreText);
        titleText.setTypeface(boldTypeface);
        highScoreTitleText.setTypeface(boldTypeface);
        scoreText.setTypeface(boldTypeface);

        // Get high-score
        SharedPreferences preferences = this.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);
        Integer existingScore = preferences.getInt(getString(R.string.high_score), 0);

        scoreText.setText(new DecimalFormat("000000").format(existingScore));
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
