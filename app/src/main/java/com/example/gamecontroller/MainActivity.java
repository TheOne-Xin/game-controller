package com.example.gamecontroller;

import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements InputManager.InputDeviceListener {
    private final String TAG = "game-controller";

    private int directionPressed = -1; // initialized to -1
    private int dpadAction = -1; // initialized to -1
    //Joystick value
    private float mPreviousLeftJoystickX;
    private float mPreviousLeftJoystickY;
    private float mPreviousRightJoystickX;
    private float mPreviousRightJoystickY;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text_view);
        updateGameControllerCount();
    }

    private void updateGameControllerCount() {
        ArrayList<Integer> gameControllerIds = getGameControllerIds();
        if (gameControllerIds.size() == 0) {
            mTextView.setText("未连接到蓝牙手柄!");
        } else {
            mTextView.setText(String.format(Locale.getDefault(), "已连接到 %d 个蓝牙手柄", gameControllerIds.size()));
        }
    }

    public ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    @Override
    public void onInputDeviceAdded(int i) {
        Log.d(TAG, "onInputDeviceAdded:" + i);
        updateGameControllerCount();
    }

    @Override
    public void onInputDeviceRemoved(int i) {
        Log.d(TAG, "onInputDeviceRemoved:" + i);
        updateGameControllerCount();
    }

    @Override
    public void onInputDeviceChanged(int i) {
        Log.d(TAG, "onInputDeviceChanged:" + i);
        updateGameControllerCount();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchGenericMotionEvent");
        if (isDpadDevice(ev)) {
            int press = getDirectionPressed(ev);
            String keyName = "";
            switch (press) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    keyName = "DPAD_LEFT";
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    keyName = "DPAD_RIGHT";
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    keyName = "DPAD_UP";
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    keyName = "DPAD_DOWN";
                    break;
                default:
                    break;
            }
            if (!keyName.isEmpty()) {
                mTextView.setText(String.format(Locale.getDefault(), "%s:%s", keyName, dpadAction == 0 ? "down" : "up"));
                Log.d(TAG, "key code:" + press + ";action:" + dpadAction);
                if (dpadAction == KeyEvent.ACTION_UP) {
                    directionPressed = -1;
                    dpadAction = -1;
                }
                return true;
            }
        }

        if ((ev.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                ev.getAction() == MotionEvent.ACTION_MOVE) {
            String joystick = "";
            float leftJoystickX = ev.getAxisValue(MotionEvent.AXIS_X);
            if (Float.compare(leftJoystickX, mPreviousLeftJoystickX) != 0) {
                joystick += "leftJoystickX:" + leftJoystickX + ";";
            }
            mPreviousLeftJoystickX = leftJoystickX;

            float leftJoystickY = ev.getAxisValue(MotionEvent.AXIS_Y);
            if (Float.compare(leftJoystickY, mPreviousLeftJoystickY) != 0) {
                joystick += "leftJoystickY:" + leftJoystickY + ";";
            }
            mPreviousLeftJoystickY = leftJoystickY;

            float rightJoystickX = ev.getAxisValue(MotionEvent.AXIS_Z);
            if (Float.compare(rightJoystickX, mPreviousRightJoystickX) != 0) {
                joystick += "rightJoystickX:" + rightJoystickX + ";";
            }
            mPreviousRightJoystickX = rightJoystickX;

            float rightJoystickY = ev.getAxisValue(MotionEvent.AXIS_RZ);
            if (Float.compare(rightJoystickY, mPreviousRightJoystickY) != 0) {
                joystick += "rightJoystickY:" + rightJoystickY + ";";
            }
            mPreviousRightJoystickY = rightJoystickY;

            mTextView.setText(joystick);
            Log.d(TAG, "DeviceId:" + ev.getDeviceId() + ";AXIS_X:" + leftJoystickX + ";AXIS_Y:" + leftJoystickY
                    + ";AXIS_Z:" + rightJoystickX + ";AXIS_RZ:" + rightJoystickY);
            return true;
        }
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            String keyName = "";
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BUTTON_X:
                    keyName = "BUTTON_X";
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    keyName = "BUTTON_Y";
                    break;
                case KeyEvent.KEYCODE_BUTTON_A:
                    keyName = "BUTTON_A";
                    break;
                case KeyEvent.KEYCODE_BUTTON_B:
                    keyName = "BUTTON_B";
                    break;
                case KeyEvent.KEYCODE_BUTTON_L1:
                    keyName = "BUTTON_L1";
                    break;
                case KeyEvent.KEYCODE_BUTTON_R1:
                    keyName = "BUTTON_R1";
                    break;
                case KeyEvent.KEYCODE_BUTTON_L2:
                    keyName = "BUTTON_L2";
                    break;
                case KeyEvent.KEYCODE_BUTTON_R2:
                    keyName = "BUTTON_R2";
                    break;
                case KeyEvent.KEYCODE_BUTTON_START:
                    keyName = "BUTTON_START";
                    break;
                case KeyEvent.KEYCODE_BUTTON_SELECT:
                    keyName = "BUTTON_SELECT";
                    break;
                default:
                    Log.d(TAG, "dispatchKeyEvent-other-KeyCode:" + event.getKeyCode());
                    break;
            }
            mTextView.setText(String.format(Locale.getDefault(), "%s:%s", keyName, dpadAction == 0 ? "down" : "up"));
            Log.d(TAG, "key code:" + event.getKeyCode() + ";action:" + event.getAction());
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private int getDirectionPressed(MotionEvent event) {
        if (!isDpadDevice(event)) {
            return -1;
        }
        // Use the hat axis value to find the D-pad direction
        MotionEvent motionEvent = (MotionEvent) event;
        float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);
        // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
        // LEFT and RIGHT direction accordingly.
        if (Float.compare(xaxis, -1.0f) == 0) {
            directionPressed = KeyEvent.KEYCODE_DPAD_LEFT;
            dpadAction = KeyEvent.ACTION_DOWN;
        } else if (Float.compare(xaxis, 1.0f) == 0) {
            directionPressed = KeyEvent.KEYCODE_DPAD_RIGHT;
            dpadAction = KeyEvent.ACTION_DOWN;
        }
        // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
        // UP and DOWN direction accordingly.
        else if (Float.compare(yaxis, -1.0f) == 0) {
            directionPressed = KeyEvent.KEYCODE_DPAD_UP;
            dpadAction = KeyEvent.ACTION_DOWN;
        } else if (Float.compare(yaxis, 1.0f) == 0) {
            directionPressed = KeyEvent.KEYCODE_DPAD_DOWN;
            dpadAction = KeyEvent.ACTION_DOWN;
        } else {
            dpadAction = KeyEvent.ACTION_UP;
        }
        return directionPressed;
    }

    private static boolean isDpadDevice(InputEvent event) {
        // Check that input comes from a device with directional pads.
        if ((event.getSource() & InputDevice.SOURCE_DPAD)
                != InputDevice.SOURCE_DPAD) {
            return true;
        } else {
            return false;
        }
    }
}
