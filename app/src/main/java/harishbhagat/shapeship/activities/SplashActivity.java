package harishbhagat.shapeship.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.activity.ComponentActivity;
import android.os.Bundle;
import android.view.View;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Fonts;

public class SplashActivity extends ComponentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SystemBars.keepContentClear(this);

        // Set the fonts
        Fonts.apply(Fonts.black(this), findViewById(R.id.titleText));
        Fonts.apply(Fonts.regular(this), findViewById(R.id.copyrightText));
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
