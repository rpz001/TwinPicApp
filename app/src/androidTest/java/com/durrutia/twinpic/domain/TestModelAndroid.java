package com.durrutia.twinpic.domain;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing version Android.
 *
 * @author Diego P. Urrutia Astorga
 * @version 20161102
 */
@RunWith(AndroidJUnit4.class)
@Slf4j
public class TestModelAndroid {

    /**
     * Timming
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(120);


    /**
     * Testing de la base de datos
     */
    @Test
    public void testDatabase() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        com.durrutia.twinpic.Test.testDatabase(appContext);

    }

}
