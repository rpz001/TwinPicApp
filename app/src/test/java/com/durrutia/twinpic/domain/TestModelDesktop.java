package com.durrutia.twinpic.domain;

import android.provider.Settings;

import com.durrutia.twinpic.BaseTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.robolectric.RuntimeEnvironment;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing version Desktop via Robolectric.
 *
 * @author Diego P. Urrutia Astorga
 * @version 20161102
 */
@Slf4j
public class TestModelDesktop extends BaseTest {

    /**
     * Timming
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(120);

    /**
     * Testing
     */
    @Test
    public void testDatabase() {

        Settings.Secure.putString(RuntimeEnvironment.application.getContentResolver(),
                Settings.Secure.ANDROID_ID,
                "D3v1c31d"
        );

        com.durrutia.twinpic.Test.testDatabase(RuntimeEnvironment.application);

    }


}
