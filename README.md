# Stockfish engine library of Inner Board

This repository is the source code of the chess engine library in the Android
app **Inner Board: Blindfold Chess** (`com.girskorr.innerboard`), from version
1.0.0 on. The app plays against [Stockfish](https://stockfishchess.org) 17,
which is free software under the GNU General Public License version 3, and the
engine library is distributed under that license (see [`COPYING`](COPYING)).

The library is [react-native-stockfish](https://github.com/loloof64/ReactNativeStockfish)
0.3.3, which contains Stockfish 17 (`cpp/stockfish`) and the two neural
networks it embeds (`cpp/nn-1111cefa1111.nnue`, `cpp/nn-37f18f62d772.nnue`),
with the changes the app makes:

1. The first commit is the package exactly as published on npm
   (`@loloof64/react-native-stockfish@0.3.3`).
2. The second commit applies the app's patch, also kept as
   [`inner-board/react-native-stockfish+0.3.3.patch`](inner-board/react-native-stockfish+0.3.3.patch):
   the build downloads a network only when it is missing or does not match its
   SHA-256, the library is linked for 16 KB memory pages, the engine can be
   stopped and started again, and on Android one engine and one pair of output
   readers run per process.

The original documentation of the package is in [`README.package.md`](README.package.md).

## Building

The app builds this library from source as part of its Android build:
`android/CMakeLists.txt` compiles Stockfish (`cpp/stockfish/src`), its stream
adapters (`cpp/stockfish/fixes`) and the bridge (`cpp/react-native-stockfish.cpp`,
`android/cpp-adapter.cpp`) into `libreact-native-stockfish.so`, with both
networks embedded. To build it yourself, add this directory as the
`@loloof64/react-native-stockfish` dependency of a React Native app (New
Architecture) and build the app for Android with the NDK.

## Licenses

- Stockfish (`cpp/stockfish`): GNU GPL v3, see `COPYING`. Copyright (C)
  2004-2024 The Stockfish developers, see `cpp/stockfish/AUTHORS`.
- The React Native wrapper: MIT, see `LICENSE`. Copyright (c) 2024-2025
  Laurent Bernabé. The app's changes to it are offered under the same terms.
- The engine library as built into the app, which combines both: GNU GPL v3.
