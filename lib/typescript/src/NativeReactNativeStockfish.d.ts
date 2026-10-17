import type { TurboModule } from 'react-native';
export declare const _subscribeToStockfishOutput: (callback: (output: string) => void) => () => void;
export declare const _subscribeToStockfishError: (callback: (output: string) => void) => () => void;
export interface Spec extends TurboModule {
    stockfishLoop(): void;
    sendCommandToStockfish(command: string): void;
    stopStockfish(): void;
}
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeReactNativeStockfish.d.ts.map