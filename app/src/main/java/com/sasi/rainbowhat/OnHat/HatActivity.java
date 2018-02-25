package com.sasi.rainbowhat.OnHat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import java.io.IOException;

import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.bmx280.Bmx280;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

public class HatActivity extends Activity {

    private static final String TAG = HatActivity.class.getSimpleName();

    Button buttonA, buttonB, buttonC;
    Apa102 ledstrip = null;
    Bmx280 sensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAlphanumericDisplay("nice");
        setupTemperatureSensor();
        setupButtons();

        // Light up the rainbow
        try {
            ledstrip = RainbowHat.openLedStrip();
            ledstrip.setBrightness(1);
            int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
            for (int i = 0; i < rainbow.length; i++) {
                rainbow[i] = Color.HSVToColor(255, new float[]{i * 360.f / rainbow.length, 1.0f, 1.0f});
            }
            ledstrip.write(rainbow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupButtons() {
        // Detect when button 'A' is pressed.
        try {
            buttonA = RainbowHat.openButtonA();

            buttonA.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    Log.d(TAG, "button A pressed:" + pressed);
                    setupAlphanumericDisplay("SASI");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Detect when button 'B' is pressed.
        try {
            buttonB = RainbowHat.openButtonB();

            buttonB.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    Log.d(TAG, "button B pressed:" + pressed);
                    displayCurrentTemperature();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Detect when button 'C' is pressed.
        try {
            buttonC = RainbowHat.openButtonC();

            buttonC.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    Log.d(TAG, "button C pressed:" + pressed);
                    setupAlphanumericDisplay("BHUVI");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTemperatureSensor() {
        // Log the current temperature
        try {
            sensor = RainbowHat.openSensor();
            sensor.setTemperatureOversampling(Bmx280.OVERSAMPLING_1X);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentTemperature() {
        // Log the current temperature
        try {
            if(sensor != null) {
                Log.d(TAG, "temperature:" + sensor.readTemperature());

                setupAlphanumericDisplay(String.valueOf(sensor.readTemperature()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupAlphanumericDisplay(String displayText) {
        // Display a string on the segment display.
        AlphanumericDisplay segment = null;
        try {
            segment = RainbowHat.openDisplay();
            segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
            segment.display(displayText);
            segment.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the device when done.
            try {
                if (segment != null) {
                    segment.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the device button A when done.
        try {
            buttonA.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the device button B when done.
        try {
            buttonC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the device when done.
        try {
            ledstrip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the device when done.
        try {
            sensor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
