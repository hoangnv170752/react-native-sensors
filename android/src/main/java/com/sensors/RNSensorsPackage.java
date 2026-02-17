package com.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.hardware.Sensor;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

public class RNSensorsPackage implements ReactPackage {

    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
      List<NativeModule> modules = new ArrayList<>();
      modules.add(new RNSensor(reactContext, "RNSensorsGyroscope", Sensor.TYPE_GYROSCOPE));
      modules.add(new RNSensor(reactContext, "RNSensorsAccelerometer", Sensor.TYPE_ACCELEROMETER));
      modules.add(new RNSensor(reactContext, "RNSensorsGravity", Sensor.TYPE_GRAVITY));
      modules.add(new RNSensor(reactContext, "RNSensorsMagnetometer", Sensor.TYPE_MAGNETIC_FIELD));
      modules.add(new RNSensor(reactContext, "RNSensorsBarometer", Sensor.TYPE_PRESSURE));
      modules.add(new RNSensor(reactContext, "RNSensorsOrientation", Sensor.TYPE_ROTATION_VECTOR));
      return modules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
      return Collections.emptyList();
    }
}
