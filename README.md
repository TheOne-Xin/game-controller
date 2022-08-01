# 前言
有些手游是支持手柄操作的，通过连接蓝牙手柄，可以极大的提升游戏的用户体验，Android 中提供了用于检测和处理来自蓝牙手柄的用户输入的 API：[处理控制器操作](https://developer.android.com/training/game-controllers/controller-input)。
# 1 检测蓝牙手柄是否已连接
如需验证某个已连接的输入设备是否是蓝牙手柄，请调用 getSources() 以获取该设备支持的输入来源类型的组合位字段。
- SOURCE_GAMEPAD ：表示输入设备具有游戏手柄按钮（例如，BUTTON_A）。
- SOURCE_DPAD ：表示输入设备具有方向键按钮（例如，DPAD_UP）。
- SOURCE_JOYSTICK ：表示输入设备具有模拟控制摇杆（例如，记录沿 AXIS_X 和 AXIS_Y 的移动的操纵杆）。

通过以下方法，可以检查连接的输入设备是否是蓝牙手柄。如果是，则该方法会检索蓝牙手柄的设备 ID。然后，可以将每个设备 ID 与游戏中的一位玩家相关联，并分别处理每位已连接的玩家的游戏操作。

```java
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
```
如需检测已连接的蓝牙手柄是否支持特定键码或轴代码，请使用以下方法：
- 在 Android 4.4（API 级别 19）或更高版本中，通过调用 hasKeys(int...) 来确定已连接的蓝牙手柄是否支持某个键码。
- 在 Android 3.1（API 级别 12）或更高版本中，可以找到已连接的蓝牙手柄支持的所有可用轴，具体方法为：首先调用 getMotionRanges()。然后，在返回的每个 InputDevice.MotionRange 对象上，调用 getAxis() 以获取其轴 ID。
# 2 手柄的输入事件
在系统级别，Android 会将来自蓝牙手柄的输入事件代码报告为 Android 键码和轴值。在游戏中，可以接收这些键码和轴值，并将它们转换为特定的游戏内操作。

当玩家以物理方式将一个蓝牙手柄连接到其 Android 设备或以无线方式将这两者配对时，系统会自动将该手柄检测为输入设备，并开始报告其输入事件。通过在处于活动状态的 Activity 或处于焦点的 View 中实现以下回调方法（选择 Activity 或 View 实现回调，不要同时用这两者实现回调），游戏可以接收报告的输入事件：
- 在 **Activity** 中：
  - dispatchGenericMotionEvent(android.view. MotionEvent)可进行调用以处理一般的动作事件（例如，操纵杆移动）。
  - dispatchKeyEvent(android.view.KeyEvent)可进行调用以处理按键事件（例如，按下或松开游戏手柄或方向键按钮）。
- 在 **View** 中：
  - onGenericMotionEvent(android.view.MotionEvent)可进行调用以处理一般的动作事件（例如，操纵杆移动）。
  - onKeyDown(int, android.view.KeyEvent)可进行调用以处理物理键（例如，游戏手柄或方向键按钮）按下事件。
  - onKeyUp(int, android.view.KeyEvent)可进行调用以处理物理键（例如，游戏手柄或方向键按钮）松开事件。

回调提供的以下对象，可以获取输入事件的相关信息：
- **KeyEvent**
描述方向键和游戏手柄按钮事件的对象。按键事件带有一个键码，该键码会指示触发的具体按钮（例如，DPAD_DOWN 或 BUTTON_A）。您可以通过调用 getKeyCode() 获取该键码，也可以通过按键事件回调（例如，onKeyDown()）获取该键码。
- **MotionEvent**
描述来自操纵杆和肩部扳机移动的输入的对象。动作事件带有一个操作代码和一组轴值。操作代码指定了发生的状态变化（例如，操纵杆被移动）。轴值描述了特定物理控件的位置及其他移动属性（例如，AXIS_X 或 AXIS_RTRIGGER）。您可以通过调用 getAction() 获取操作代码，通过调用 getAxisValue() 获取轴值。
# 3 处理手柄按钮按下操作
游戏手柄按钮按下操作生成的常见键码包括 BUTTON_A、BUTTON_B、BUTTON_SELECT 和 BUTTON_START。当按下方向键交叉按钮的中心部位时，一些游戏手柄还会触发 DPAD_CENTER 键码。可以通过调用 getKeyCode() 获取该键码：

```java
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
            Log.d(TAG, "key code:" + event.getKeyCode() + ";action:" + event.getAction());
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
```
也可以通过按键事件回调（例如 onKeyDown()）获取该键码：

```java
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            String keyName = "";
            switch (keyCode) {
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
                default:
                    break;
            }
            Log.d(TAG, "key code:" + keyCode + ";action:" + event.getAction());
        }
        return super.onKeyDown(keyCode, event);
    }
```
常见的游戏手柄按钮使用的游戏操作：
| 游戏操作 | 按钮键码 |
|--|--|
| 在主菜单中启动游戏，或在游戏过程中暂停/取消暂停 | BUTTON_START |
| 显示菜单 | BUTTON_SELECT 和 KEYCODE_MENU |
| 返回到菜单中的上一项 | BUTTON_B |
| 确认选择，或执行主要游戏操作 | BUTTON_A 和 DPAD_CENTER |
# 4 处理手柄方向键输入
方向键是许多手柄中常用的物理控件。Android 将方向键“上”和“下”按下操作报告为 AXIS_HAT_Y 事件，范围为 -1.0（上）至 1.0（下）；将方向键“左”或“右”按下操作报告为 AXIS_HAT_X 事件，范围为 -1.0（左）至 1.0（右）。
| 游戏操作 | 方向键键码 | 帽子轴代码 |
|--|--|--|
| 上移 | KEYCODE_DPAD_UP | AXIS_HAT_Y（对于 0 至 -1.0 之间的值） |
| 下移 | KEYCODE_DPAD_DOWN | AXIS_HAT_Y（对于 0 至 1.0 之间的值） |
| 左移 | KEYCODE_DPAD_LEFT | AXIS_HAT_X（对于 0 至 -1.0 之间的值） |
| 右移 | KEYCODE_DPAD_RIGHT | AXIS_HAT_X（对于 0 至 1.0 之间的值） |
可以在 Activity 的 dispatchGenericMotionEvent 中处理，示例代码如下：

```java
    private int directionPressed = -1; // initialized to -1
    private int dpadAction = -1; // initialized to -1    

	@Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
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
                Log.d(TAG, "key code:" + press + ";action:" + dpadAction);
                if (dpadAction == KeyEvent.ACTION_UP) {
                    directionPressed = -1;
                    dpadAction = -1;
                }
                return true;
            }
        }
        return super.dispatchGenericMotionEvent(ev);
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
```
# 5 处理手柄操纵杆移动
当玩家移动游戏手柄上的操纵杆时，Android 会报告一个 MotionEvent，其中包含 ACTION_MOVE 操作代码和更新后的操纵杆轴位置。可以使用 MotionEvent 提供的数据来确定是否发生了移动事件。

许多游戏手柄都有左右两个操纵杆。对于左摇杆，Android 会将水平移动报告为 AXIS_X 事件，将垂直移动报告为 AXIS_Y 事件。对于右摇杆，Android 会将水平移动报告为 AXIS_Z 事件，将垂直移动报告为 AXIS_RZ 事件。

可以在 Activity 的 dispatchGenericMotionEvent 中处理，示例代码如下：

```java
    //Joystick value
    private float mPreviousLeftJoystickX;
    private float mPreviousLeftJoystickY;
    private float mPreviousRightJoystickX;
    private float mPreviousRightJoystickY;

	@Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
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

            Log.d(TAG, "DeviceId:" + ev.getDeviceId() + ";AXIS_X:" + leftJoystickX + ";AXIS_Y:" + leftJoystickY
                    + ";AXIS_Z:" + rightJoystickX + ";AXIS_RZ:" + rightJoystickY);
            return true;
        }
        return super.dispatchGenericMotionEvent(ev);
    }
```
有些手柄具有左肩部扳机和右肩部扳机。如果存在这些扳机，Android 会将左扳机按下操作报告为 AXIS_LTRIGGER 事件，将右扳机按下操作报告为 AXIS_RTRIGGER 事件。