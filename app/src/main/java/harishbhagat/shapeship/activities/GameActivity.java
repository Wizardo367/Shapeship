package harishbhagat.shapeship.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.game.GameSurfaceView;

public class GameActivity extends AppCompatActivity
{
    // Create the gameview
    private GameSurfaceView gsv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
}