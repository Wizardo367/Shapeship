package harishbhagat.shapeship.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import harishbhagat.shapeship.R;

public class HelpActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Set the fonts
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/seguibl.ttf");
        Typeface regularTypeface = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");

        TextView titleText = (TextView) findViewById(R.id.titleText);
        TextView para1 = (TextView) findViewById(R.id.helpTextPara1);
        TextView para2 = (TextView) findViewById(R.id.helpTextPara2);

        titleText.setTypeface(boldTypeface);
        para1.setTypeface(regularTypeface);
        para2.setTypeface(regularTypeface);
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
