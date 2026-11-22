package harishbhagat.shapeship.activities;

import android.content.Intent;
import androidx.activity.ComponentActivity;
import android.os.Bundle;
import android.view.View;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Fonts;

public class PauseActivity extends ComponentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
        SystemBars.keepContentClear(this);

        // Set the fonts
        Fonts.apply(Fonts.black(this), findViewById(R.id.titleText));
        Fonts.apply(Fonts.regular(this), findViewById(R.id.playText), findViewById(R.id.returnText));
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
