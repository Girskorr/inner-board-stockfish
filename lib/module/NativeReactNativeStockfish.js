"use strict";

import { TurboModuleRegistry, NativeModules, NativeEventEmitter } from 'react-native';
const {
  ReactNativeStockfish
} = NativeModules;
if (!ReactNativeStockfish) {
  throw new Error('ReactNativeStockfish native module is not linked. IOS users: ensure that you have run `pod install` and rebuilt the app.');
}
const eventEmitter = new NativeEventEmitter(ReactNativeStockfish);
export const _subscribeToStockfishOutput = callback => {
  const subscription = eventEmitter.addListener('stockfish-output', callback);
  return () => subscription.remove();
};
export const _subscribeToStockfishError = callback => {
  const subscription = eventEmitter.addListener('stockfish-error', callback);
  return () => subscription.remove();
};
export default TurboModuleRegistry.getEnforcing('ReactNativeStockfish');
//# sourceMappingURL=NativeReactNativeStockfish.js.map