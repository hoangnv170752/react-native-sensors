// Inspired by https://github.com/pwmckenna/react-native-motion-manager

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNSensorsSpec/RNSensorsSpec.h>
#endif

#import <CoreMotion/CoreMotion.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
@interface RNSensorsBarometer : RCTEventEmitter <NativeSensorsBarometerSpec>
#else
@interface RNSensorsBarometer : RCTEventEmitter <RCTBridgeModule>
#endif
{
    CMAltimeter *_altimeter;
    int logLevel;
    bool hasListeners;
}

- (void) isAvailableWithResolver:(RCTPromiseResolveBlock) resolve
         rejecter:(RCTPromiseRejectBlock) reject;
- (void) setUpdateInterval:(double) interval;
- (void) getUpdateInterval:(RCTResponseSenderBlock) cb;
- (void) setLogLevel:(int) level;
- (void) getData:(RCTResponseSenderBlock) cb;
- (void) startUpdates;
- (void) stopUpdates;

@end
