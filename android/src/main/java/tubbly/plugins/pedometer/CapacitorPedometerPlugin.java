package tubbly.plugins.pedometer;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// @CapacitorPlugin(name = "CapacitorPedometer")
// public class CapacitorPedometerPlugin extends Plugin {

//     private CapacitorPedometer implementation = new CapacitorPedometer();

//     @PluginMethod
//     public void echo(PluginCall call) {
//         String value = call.getString("value");

//         JSObject ret = new JSObject();
//         ret.put("value", implementation.echo(value));
//         call.resolve(ret);
//     }
// }
// android/src/main/java/com/example/plugin/StepCounterPlugin.java

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(name = "CapacitorPedometer", permissions = {
        // Required for Android 10 (API 29) and above for accessing physical activity
        // sensors.
        // For older Android versions, no specific permission is needed for
        // TYPE_STEP_COUNTER.
        @Permission(strings = { Manifest.permission.ACTIVITY_RECOGNITION }, alias = "activityRecognition")
})
public class CapacitorPedometerPlugin extends Plugin implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isCounting = false;
    private int initialStepCount = 0; // Steps at the moment we start counting
    private int currentSteps = 0; // Steps since startCounting() was called

    @Override
    public void load() {
        super.load();
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor == null) {
                Log.e("StepCounterPlugin", "Step Counter sensor not available on this device.");
            }
        } else {
            Log.e("StepCounterPlugin", "SensorManager not available.");
        }
    }

    @PluginMethod
    public void startCounting(PluginCall call) {
        if (stepCounterSensor == null) {
            call.reject("Step Counter sensor not available on this device.");
            return;
        }

        // Request permission if needed (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)) {
            requestPermissionForAlias("activityRecognition", call, "activityRecognitionPermsCallback");
            return;
        }

        // If permission is already granted or not needed for this Android version
        startSensorListening(call);
    }

    @PermissionCallback
    private void activityRecognitionPermsCallback(PluginCall call) {
        if (getPermissionState("activityRecognition") == PermissionState.GRANTED) {
            startSensorListening(call);
        } else {
            call.reject("Permission denied for ACTIVITY_RECOGNITION.");
        }
    }

    private void startSensorListening(PluginCall call) {
        if (!isCounting) {
            // Register the listener. Sensor.TYPE_STEP_COUNTER gives total steps since last
            // reboot.
            // We need to calculate steps taken since startCounting().
            // SENSOR_DELAY_NORMAL is a good balance for power consumption.
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isCounting = true;
            initialStepCount = -1; // Indicate that we need to capture the initial reading
            currentSteps = 0; // Reset current steps for this session
            call.resolve();
            Log.d("StepCounterPlugin", "Started step counting.");
        } else {
            call.resolve(); // Already counting
            Log.d("StepCounterPlugin", "Already counting steps.");
        }
    }

    @PluginMethod
    public void stopCounting(PluginCall call) {
        if (isCounting) {
            sensorManager.unregisterListener(this);
            isCounting = false;
            call.resolve();
            Log.d("StepCounterPlugin", "Stopped step counting.");
        } else {
            call.resolve(); // Not counting
            Log.d("StepCounterPlugin", "Not counting steps, nothing to stop.");
        }
    }

    @PluginMethod
    public void getStepCount(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("count", currentSteps);
        call.resolve(ret);
    }

    // MARK: - SensorEventListener Implementation

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalStepsSinceBoot = (int) event.values[0];

            if (initialStepCount == -1) {
                // First reading after starting, set this as our baseline
                initialStepCount = totalStepsSinceBoot;
                Log.d("StepCounterPlugin", "Initial step count captured: " + initialStepCount);
            }

            // Calculate steps taken since startCounting()
            currentSteps = totalStepsSinceBoot - initialStepCount;

            Log.d("StepCounterPlugin",
                    "Total steps since boot: " + totalStepsSinceBoot + ", Current session steps: " + currentSteps);

            // Emit an event to the JavaScript side with the updated step count
            JSObject ret = new JSObject();
            ret.put("count", currentSteps);
            notifyListeners("stepCountChange", ret);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used for step counter, but required by SensorEventListener interface
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        // Ensure the sensor listener is unregistered when the plugin is destroyed
        if (isCounting) {
            sensorManager.unregisterListener(this);
            isCounting = false;
        }
    }
}
