package harishbhagat.shapeship.audio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AudioTest
{
    private static final float DELTA = 1e-6f;

    @Test
    public void convertsTheSettingToAPlayerVolume()
    {
        assertEquals(0, Audio.toPlayerVolume(0), DELTA);
        assertEquals(0.4f, Audio.toPlayerVolume(40), DELTA);
        assertEquals(1, Audio.toPlayerVolume(100), DELTA);
    }

    @Test
    public void clampsOutOfRangeSettings()
    {
        assertEquals(0, Audio.toPlayerVolume(-20), DELTA);
        assertEquals(1, Audio.toPlayerVolume(150), DELTA);
    }
}
