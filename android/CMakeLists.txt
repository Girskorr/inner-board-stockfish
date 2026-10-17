cmake_minimum_required(VERSION 3.4.1)
project(ReactNativeStockfish)

set (CMAKE_VERBOSE_MAKEFILE ON)
set (CMAKE_CXX_STANDARD 17)

# Include stockfish NNUE
# Stockfish names each network after the first 12 hex digits of its SHA-256.
# Download a network only when the cached copy is missing or does not match,
# verify it, and give up after a stalled transfer instead of hanging the
# build or embedding a truncated file. Each ABI configures in parallel, so
# each downloads to its own temporary file before the atomic rename.
function(fetch_nnue name sha256)
  set(target "${CMAKE_CURRENT_SOURCE_DIR}/../cpp/${name}")
  set(actual "")
  if(EXISTS "${target}")
    file(SHA256 "${target}" actual)
  endif()
  if(NOT actual STREQUAL sha256)
    set(part "${target}.${ANDROID_ABI}.part")
    file(DOWNLOAD "https://tests.stockfishchess.org/api/nn/${name}" "${part}"
         EXPECTED_HASH SHA256=${sha256}
         INACTIVITY_TIMEOUT 60 TIMEOUT 1800 STATUS status SHOW_PROGRESS)
    list(GET status 0 code)
    if(NOT code EQUAL 0)
      file(REMOVE "${part}")
      message(FATAL_ERROR "Downloading Stockfish network ${name} failed: ${status}")
    endif()
    file(RENAME "${part}" "${target}")
  endif()
  file(COPY "${target}" DESTINATION ${CMAKE_BINARY_DIR})
endfunction()
fetch_nnue(nn-1111cefa1111.nnue 1111cefa11116b77161bd4b14dab4c50f26e5920c756f4861592be3dcd6de174)
fetch_nnue(nn-37f18f62d772.nnue 37f18f62d772f3107e1d6aaca3898c130c3c86f2ab63e6555fbbca20635a899d)

# Include stockfish sources and fixes
file(GLOB_RECURSE stockfishCppPaths "../cpp/stockfish/src/*.cpp")
file(GLOB_RECURSE stockfishFixesPaths "../cpp/stockfish/fixes/*.cpp")


add_library(react-native-stockfish            SHARED
            ../cpp/react-native-stockfish.cpp
            cpp-adapter.cpp
            ${stockfishCppPaths}
            ${stockfishFixesPaths}
)

# --- 16 KB page size support (Android 15+) ------------------------------------
# Without this, the .so LOAD segments align to 4 KB and fail the ELF alignment
# check ("not 16 KB compatible") on 16 KB-page devices/emulators. Every other
# native lib in the app (RN 0.81 / Expo 54 / NDK 27) is already 16 KB-aligned;
# Stockfish was the lone 4 KB outlier. Force the linker max page size to 16 KB.
target_link_options(react-native-stockfish PRIVATE "-Wl,-z,max-page-size=16384")

# Specifies a path to native header files.
include_directories(
            ../cpp
            ../cpp/stockfish/fixes
            ../cpp/stockfish/src
            ../cpp/stockfish/src/incbin
            ../cpp/stockfish/src/nnue
            ../cpp/stockfish/src/nnue/features
            ../cpp/stockfish/src/nnue/layers
            ../cpp/stockfish/src/syzygy
)