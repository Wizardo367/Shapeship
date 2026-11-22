package harishbhagat.shapeship.activities;

import android.content.Intent;
import androidx.activity.ComponentActivity;
import android.os.Bundle;
import android.view.View;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Fonts;

public class HelpActivity extends ComponentActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        SystemBars.keepContentClear(this);

        // Set the fonts
        Fonts.apply(Fonts.black(this), findViewById(R.id.titleText));
        Fonts.apply(Fonts.regular(this), findViewById(R.id.helpTextPara1), findViewById(R.id.helpTextPara2));
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
