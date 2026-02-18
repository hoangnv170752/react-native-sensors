// @flow strict-local
import type {TurboModule} from 'react-native/Libraries/TurboModule/RCTExport';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  isAvailable(): Promise<boolean>;
  setUpdateInterval(interval: number): void;
  setLogLevel(level: number): void;
  getUpdateInterval(cb: (error: Object, interval: number) => void): void;
  getData(cb: (error: Object, data: Object) => void): void;
  startUpdates(): void;
  stopUpdates(): void;
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default (TurboModuleRegistry.get<Spec>('RNSensorsMagnetometer'): ?Spec);
