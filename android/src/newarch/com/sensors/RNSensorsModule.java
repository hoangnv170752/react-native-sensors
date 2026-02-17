package com.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;

@ReactModule(name = RNSensorsModule.NAME)
public class RNSensorsModule extends RNSensorsSpec implements SensorEventListener, TurboModule {

  public static final String NAME = "RNSensors";

  private final ReactApplicationContext reactContext;
  private final SensorManager sensorManager;
  private Sensor sensor;
  private double lastReading = (double) System.currentTimeMillis();
  private int interval;
  private int logLevel = 0;
  private String sensorName;
  private int sensorType;
  private float[] rotation = new float[9];
  private float[] orientation = new float[3];
  private float[] quaternion = new float[4];

  private int listenerCount = 0;

  public RNSensorsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.sensorManager = (SensorManager)reactContext.getSystemService(reactContext.SENSOR_SERVICE);
    this.sensorName = NAME;
    this.sensorType = Sensor.TYPE_ACCELEROMETER;
    this.sensor = this.sensorManager.getDefaultSensor(this.sensorType);
  }

  public RNSensorsModule(ReactApplicationContext reactContext, String sensorName, int sensorType) {
    super(reactContext);
    this.reactContext = reactContext;
    this.sensorType = sensorType;
    this.sensorName = sensorName;
    this.sensorManager = (SensorManager)reactContext.getSystemService(reactContext.SENSOR_SERVICE);
    this.sensor = this.sensorManager.getDefaultSensor(this.sensorType);
  }

  @Override
  @NonNull
  public String getName() {
    return this.sensorName;
  }

  @Override
  @ReactMethod
  public void isAvailable(Promise promise) {
    if (this.sensor == null) {
      promise.reject(new RuntimeException("No " + this.sensorName + " found"));
      return;
    }
    promise.resolve(null);
  }

  @Override
  @ReactMethod
  public void setUpdateInterval(double newInterval) {
    this.interval = (int) newInterval;
  }

  @Override
  @ReactMethod
  public void setLogLevel(double newLevel) {
    this.logLevel = (int) newLevel;
  }

  @Override
  @ReactMethod
  public void startUpdates() {
    sensorManager.registerListener(this, sensor, this.interval * 1000);
  }

  @Override
  @ReactMethod
  public void stopUpdates() {
    sensorManager.unregisterListener(this);
  }

  private static double sensorTimestampToEpochMilliseconds(long elapsedTime) {
    return System.currentTimeMillis() + ((elapsedTime-SystemClock.elapsedRealtimeNanos())/1000000L);
  }

  private void sendEvent(String eventName, @Nullable WritableMap params) {
    try {
      this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
    } catch (RuntimeException e) {
      Log.e("ERROR", "java.lang.RuntimeException: Trying to invoke Javascript before CatalystInstance has been set!");
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if(this.listenerCount <= 0) {
      return;
    }

    int currentType = sensorEvent.sensor.getType();
    if(currentType != this.sensorType) {
      return;
    }

    double tempMs = (double) System.currentTimeMillis();
    if (tempMs - lastReading >= interval) {
      lastReading = tempMs;
      WritableMap map = Arguments.createMap();

      switch (currentType)
      {
        case Sensor.TYPE_ACCELEROMETER:
        case Sensor.TYPE_GRAVITY:
        case Sensor.TYPE_GYROSCOPE:
        case Sensor.TYPE_MAGNETIC_FIELD:
          map.putDouble("x", sensorEvent.values[0]);
          map.putDouble("y", sensorEvent.values[1]);
          map.putDouble("z", sensorEvent.values[2]);
          break;

        case Sensor.TYPE_PRESSURE:
          map.putDouble("pressure", sensorEvent.values[0]);
          break;

        case Sensor.TYPE_ROTATION_VECTOR:
          SensorManager.getQuaternionFromVector(quaternion, sensorEvent.values);
          SensorManager.getRotationMatrixFromVector(rotation, sensorEvent.values);
          SensorManager.getOrientation(rotation, orientation);

          map.putDouble("qw", quaternion[0]);
          map.putDouble("qx", quaternion[1]);
          map.putDouble("qy", quaternion[2]);
          map.putDouble("qz", quaternion[3]);

          map.putDouble("yaw", orientation[0]);
          map.putDouble("pitch", orientation[1]);
          map.putDouble("roll", orientation[2]);
          break;

        default:
          Log.e("ERROR", "Sensor type '" + currentType + "' not implemented!");
          return;
      }

      map.putDouble("timestamp", sensorTimestampToEpochMilliseconds(sensorEvent.timestamp));
      this.sendEvent(this.sensorName, map);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  @Override
  @ReactMethod
  public void addListener(String eventName) {
    this.listenerCount += 1;
  }

  @Override
  @ReactMethod
  public void removeListeners(double count) {
    this.listenerCount -= (int) count;
    if (this.sensorManager != null && this.listenerCount <= 0) {
      stopUpdates();
    }
  }
}
