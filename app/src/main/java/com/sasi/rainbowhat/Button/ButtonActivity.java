package com.sasi.rainbowhat.Button;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.sasi.rainbowhat.Commons.BoardDefaults;

import java.io.IOException;

public class ButtonActivity extends Activity {


    private PeripheralManagerService service;
    private ButtonInputDriver buttonInputDriver;
    private Gpio blueLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initService();
        initButton();
        initBlueLed();
    }

    private void initService() {
        // Service
        service = new PeripheralManagerService();
    }

    private void initBlueLed() {
        // Led Gpio
        String blue_led_pin_name = BoardDefaults.getGPIOForLED(BoardDefaults.BLUE);
        try {
            blueLedGpio = service.openGpio(blue_led_pin_name);
            blueLedGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            blueLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButton() {
        // Button
        String button_pin_name = BoardDefaults.getGPIOForButton(BoardDefaults.BLUE);
        try {
            buttonInputDriver = new ButtonInputDriver(button_pin_name, Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_SPACE);

            // Register the button driver.
            buttonInputDriver.register();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_SPACE) {
            setBlueLedValue(true);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_SPACE) {
            setBlueLedValue(false);
        }

        return super.onKeyUp(keyCode, event);
    }

    private void setBlueLedValue(boolean bool) {
        if(blueLedGpio != null) {
            try {
                blueLedGpio.setValue(bool);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(service != null) {
            service = null;
        }

        if(buttonInputDriver != null) {
            buttonInputDriver.unregister();
            try {
                buttonInputDriver.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            buttonInputDriver = null;
        }

        if(blueLedGpio != null) {
            try {
                blueLedGpio.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            blueLedGpio = null;
        }
    }
}
