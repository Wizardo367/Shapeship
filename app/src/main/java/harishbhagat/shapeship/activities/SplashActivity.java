package harishbhagat.shapeship.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import harishbhagat.shapeship.R;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Set the fonts
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/seguibl.ttf");
        Typeface regularTypeface = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");

        TextView titleText = (TextView) findViewById(R.id.titleText);
        TextView copyrightText = (TextView) findViewById(R.id.copyrightText);
        titleText.setTypeface(boldTypeface);
        copyrightText.setTypeface(regularTypeface);
    }

    /** Show the menu when the button is clicked. */
    public void showMenu(View view)
    {
        // Free up memory
        this.finish();
        Intent intentMenu = new Intent(this, MenuActivity.class);
        // Show the activity
        startActivity(intentMenu);
    }
}
