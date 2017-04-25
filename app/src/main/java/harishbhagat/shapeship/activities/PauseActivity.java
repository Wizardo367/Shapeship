package harishbhagat.shapeship.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import harishbhagat.shapeship.R;

public class PauseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        // Set the fonts
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/seguibl.ttf");
        Typeface regularTypeface = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");

        TextView titleText = (TextView) findViewById(R.id.titleText);
        TextView playText = (TextView) findViewById(R.id.playText);
        TextView returnText = (TextView) findViewById(R.id.returnText);

        titleText.setTypeface(boldTypeface);
        playText.setTypeface(regularTypeface);
        returnText.setTypeface(regularTypeface);
    }

    /** Returns the user to the game */
    public void returnToGame(View view)
    {
        this.finish();
    }

    /** Returns the user to the main menu. */
    public void returnToMenu(View view)
    {
        // Free up memory
        this.finish();
        Intent intent = new Intent(this, MenuActivity.class);
        // Clear all previous activites
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Show the activity
        startActivity(intent);
    }
}
