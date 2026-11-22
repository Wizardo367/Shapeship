package harishbhagat.shapeship.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.core.content.ContextCompat;

import harishbhagat.shapeship.model.Tilt;

/**
 * Reads how the phone is tilted, for steering the ship. It prefers the gravity sensor, which
 * the system already smooths, and falls back to the accelerometer with its own smoothing.
 * Readings arrive on the UI thread and are read on the game thread.
 */
final class TiltSensor implements SensorEventListener
{
    // --- Member(s)

    /** How much of each new accelerometer reading to blend in; the rest is the previous reading. */
    private static final float SMOOTHING = 0.2f;

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private final boolean smooth;

    private int quarterTurns = 0;
    private float sideways = 0;
    private volatile float steering = 0;

    // --- Constructor(s)

    TiltSensor(final Context context)
    {
        this.context = context;
        sensorManager = ContextCompat.getSystemService(context, SensorManager.class);
        final Sensor gravity = sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensor = gravity != null ? gravity : sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        smooth = gravity == null;
    }

    // --- Function(s)

    /** Returns the steering from -1 (full speed left) to 1 (full speed right). */
    float steering()
    {
        return steering;
    }

    // --- Method(s)

    /** Starts reading the sensor. Call on the UI thread when the game resumes. */
    void start()
    {
        sideways = 0;
        steering = 0;
        if (sensor == null) return;
        // The screen is locked to portrait, but some tablets' sensors are laid out for landscape
        quarterTurns = ContextCompat.getDisplayOrDefault(context).getRotation();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /** Stops reading the sensor, which saves battery while the game is paused. */
    void stop()
    {
        if (sensor != null) sensorManager.unregisterListener(this);
        steering = 0;
    }

    // --- SensorEventListener

    @Override
    public void onSensorChanged(final SensorEvent event)
    {
        final float reading = Tilt.sideways(quarterTurns, event.values[0], event.values[1]);
        sideways = smooth ? sideways + SMOOTHING * (reading - sideways) : reading;
        steering = Tilt.steering(sideways);
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {}
}
