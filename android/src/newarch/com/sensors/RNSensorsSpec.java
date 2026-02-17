package com.sensors;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public abstract class RNSensorsSpec extends ReactContextBaseJavaModule {

  public RNSensorsSpec(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @ReactMethod
  public abstract void isAvailable(Promise promise);

  @ReactMethod
  public abstract void setUpdateInterval(double interval);

  @ReactMethod
  public abstract void setLogLevel(double level);

  @ReactMethod
  public abstract void startUpdates();

  @ReactMethod
  public abstract void stopUpdates();

  @ReactMethod
  public abstract void addListener(String eventName);

  @ReactMethod
  public abstract void removeListeners(double count);
}
