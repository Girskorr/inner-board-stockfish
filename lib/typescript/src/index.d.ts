type UseStockfishOptions = {
    onOutput?: (output: string) => void;
    onError?: (error: string) => void;
};
/**
 * Hook for using Stockfish
 * @param onOutput - an optional function for reading Stockfish output - callback of (string) => void
 * @param onError - an optional function for reading Stockfish error - callback of (string) => void
 * @returns an array with three functions :
 * --------
 * stockfishLoop
 * Starts Stockfish
 * --------
 * stopStockfish
 * Stops Stockfish
 * --------
 * sendCommandToStockfish
 * Sends a command to stockfish, if stockfish is running
 * @param command {string} the command to send (without the newline at the end)
 * --------
 */
export declare function useStockfish({ onOutput, onError }: UseStockfishOptions): {
    stockfishLoop: () => void;
    stopStockfish: () => void;
    sendCommandToStockfish: (command: string) => void;
};
export {};
//# sourceMappingURL=index.d.ts.map