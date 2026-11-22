package harishbhagat.shapeship.activities;

import androidx.activity.ComponentActivity;
import android.os.Bundle;

import harishbhagat.shapeship.game.GameSurfaceView;

public class GameActivity extends ComponentActivity
{
    // Create the gameview
    private GameSurfaceView gsv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create the game loop
        gsv = new GameSurfaceView(this);
        setContentView(gsv);
        SystemBars.keepContentClear(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gsv.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gsv.resume();
    }

    @Override
    protected void onDestroy()
    {
        gsv.release();
        super.onDestroy();
    }
}