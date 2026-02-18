import { NativeEventEmitter, NativeModules, TurboModuleRegistry } from "react-native";
import { Observable } from "rxjs";
import { share } from "rxjs/operators";
import * as RNSensors from "./rnsensors";

function getModule(name) {
  return TurboModuleRegistry
    ? TurboModuleRegistry.get(name)
    : NativeModules[name];
}

const AccNative = getModule('RNSensorsAccelerometer');
const GyroNative = getModule('RNSensorsGyroscope');
const MagnNative = getModule('RNSensorsMagnetometer');
const BarNative = getModule('RNSensorsBarometer');
const OrientNative = getModule('RNSensorsOrientation');
const GravNative = getModule('RNSensorsGravity');

const listenerKeys = new Map([
  ["accelerometer", "RNSensorsAccelerometer"],
  ["gyroscope", "RNSensorsGyroscope"],
  ["magnetometer", "RNSensorsMagnetometer"],
  ["barometer", "RNSensorsBarometer"],
  ["orientation", "RNSensorsOrientation"],
  ["gravity", "RNSensorsGravity"],
]);

const nativeApis = new Map([
  ["accelerometer", AccNative],
  ["gyroscope", GyroNative],
  ["magnetometer", MagnNative],
  ["barometer", BarNative],
  ["orientation", OrientNative],
  ["gravity", GravNative],
]);

const eventEmitterSubscription = new Map([
  ["accelerometer", null],
  ["gyroscope", null],
  ["magnetometer", null],
  ["barometer", null],
  ["orientation", null],
  ["gravity", null],
]);

function createSensorObservable(sensorType) {
  return Observable.create(function subscribe(observer) {
    let isSensorAvailable = false;

    const unsubscribeCallback = () => {
      if (!isSensorAvailable) return;
      if (eventEmitterSubscription.get(sensorType)) eventEmitterSubscription.get(sensorType).remove();
      // stop the sensor
      RNSensors.stop(sensorType);
    };

    RNSensors.isAvailable(sensorType).then(
      () => {
        isSensorAvailable = true;

        const emitter = new NativeEventEmitter(nativeApis.get(sensorType));

        eventEmitterSubscription.set(
          sensorType,
          emitter.addListener(listenerKeys.get(sensorType), (data) => {
            observer.next(data);
          })
        );

        // Start the sensor manager
        RNSensors.start(sensorType);
      },
      () => {
        observer.error(`Sensor ${sensorType} is not available`);
      }
    );

    return unsubscribeCallback;
  }).pipe(makeSingleton());
}

// As we only have one sensor we need to share it between the different consumers
function makeSingleton() {
  return (source) => source.pipe(share());
}

const accelerometer = createSensorObservable("accelerometer");
const gyroscope = createSensorObservable("gyroscope");
const magnetometer = createSensorObservable("magnetometer");
const barometer = createSensorObservable("barometer");
const orientation = createSensorObservable("orientation");
const gravity = createSensorObservable("gravity");

export default {
  gyroscope,
  accelerometer,
  magnetometer,
  barometer,
  orientation,
  gravity,
};
