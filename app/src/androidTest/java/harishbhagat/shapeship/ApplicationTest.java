package harishbhagat.shapeship;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Checks that the app installs and starts under its own package name. */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest
{
    @Test
    public void usesAppPackageName()
    {
        assertEquals("harishbhagat.shapeship",
                InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName());
    }
}
