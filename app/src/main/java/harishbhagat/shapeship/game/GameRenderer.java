package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Textures;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.model.Asteroid;
import harishbhagat.shapeship.model.Bullet;
import harishbhagat.shapeship.model.Game;
import harishbhagat.shapeship.model.Player;
import harishbhagat.shapeship.physics.Body;

/**
 * Draws a game. Each vector is drawn into a bitmap once, at the size it has on this screen,
 * and the game uses the bitmaps' sizes as the sizes of what they draw.
 */
final class GameRenderer
{
    // --- Member(s)

    private final Bitmap background;
    private final Bitmap ship;
    private final Bitmap bullet;
    private final Bitmap asteroid;
    private final Bitmap pauseButton;
    private final ScoreDisplay scoreDisplay;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Matrix matrix = new Matrix(); /** Reused for each body drawn. */

    // --- Constructor(s)

    /** Draws the textures for a screen of the given size in pixels. */
    GameRenderer(final Context context, final Typeface typeface, final int width, final int height)
    {
        // Scale the background to fill the screen, once
        final Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_0);
        background = Bitmap.createScaledBitmap(original, width, height, true);
        if (background != original) original.recycle();

        ship = Textures.render(context, R.drawable.shapeship, Textures.pixels(width, Player.WIDTH_FRACTION));
        bullet = Textures.render(context, R.drawable.bullet, Textures.pixels(width, Bullet.WIDTH_FRACTION));
        asteroid = Textures.render(context, R.drawable.asteroid, Textures.pixels(width, Asteroid.WIDTH_FRACTION));
        pauseButton = Textures.render(context, R.drawable.pause_button, Textures.pixels(width, Game.PAUSE_BUTTON_WIDTH));

        scoreDisplay = new ScoreDisplay(typeface, width);
    }

    // --- Function(s)

    /** Returns the sizes of the textures, for the game to use as the sizes of what they draw. */
    Game.Sizes sizes()
    {
        return new Game.Sizes(size(ship), size(bullet), size(asteroid), size(pauseButton));
    }

    private static Vector2 size(final Bitmap bitmap)
    {
        return new Vector2(bitmap.getWidth(), bitmap.getHeight());
    }

    // --- Method(s)

    /** Draws the game. */
    void draw(final Canvas canvas, final Game game)
    {
        // The background is opaque and fills the screen, so it replaces the previous frame
        canvas.drawBitmap(background, 0, 0, null);

        for (Body body : game.asteroids())
            draw(canvas, asteroid, body);

        draw(canvas, ship, game.player());

        for (Body body : game.bullets())
            draw(canvas, bullet, body);

        scoreDisplay.draw(canvas, game.score());
        draw(canvas, pauseButton, game.pauseButton());
    }

    /** Draws a texture centred on a body and turned with it. The texture is the body's size, so it's never scaled. */
    private void draw(final Canvas canvas, final Bitmap texture, final Body body)
    {
        final Vector2 position = body.getPosition();
        matrix.setTranslate(position.x - texture.getWidth() / 2f, position.y - texture.getHeight() / 2f);
        if (body.getRotation() != 0) matrix.postRotate(body.getRotation(), position.x, position.y);
        canvas.drawBitmap(texture, matrix, paint);
    }
}
