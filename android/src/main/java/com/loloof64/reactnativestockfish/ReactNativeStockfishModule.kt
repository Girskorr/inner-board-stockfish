package com.loloof64.reactnativestockfish

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule

import kotlinx.coroutines.*

@ReactModule(name = ReactNativeStockfishModule.NAME)
class ReactNativeStockfishModule(reactContext: ReactApplicationContext) :
  NativeReactNativeStockfishSpec(reactContext) {

  external fun main()
  external fun openStreams()
  external fun stdoutRead(): String?
  external fun stderrRead(): String?
  external fun stdinWrite(command: String)


  override fun getName(): String {
    return NAME
  }

  init {
    System.loadLibrary("react-native-stockfish")
  }

  // The engine and its stream readers exist once per process. A reloaded JS
  // context gets a new module instance: it takes over the running engine and
  // the readers instead of starting a second engine loop and second readers
  // on the same streams, which would split the engine's input and output
  // between the two (and deliver half of it to the dead context).
  override fun stockfishLoop() {
    synchronized(lock) {
      current = this
      if (!readersStarted) {
        readersStarted = true
        startReader("stockfish-output") { it.stdoutRead() }
        startReader("stockfish-error") { it.stderrRead() }
      }
      if (engineRunning) return
      engineRunning = true
      openStreams()
    }
    engineScope.launch {
      delay(DELAY_MS)
      try {
        main()
      } finally {
        synchronized(lock) { engineRunning = false }
      }
    }
  }

  override fun sendCommandToStockfish(command: String) {
    stdinWrite(command)
  }

  // Ends the engine loop; the readers stay for the next one.
  override fun stopStockfish() {
    sendCommandToStockfish("quit\n")
  }

  companion object {
    const val NAME = "ReactNativeStockfish"
    private const val DELAY_MS = 100L

    private val lock = Any()
    private val engineScope = CoroutineScope(Dispatchers.Default)
    private val readerScope = CoroutineScope(Dispatchers.IO)
    @Volatile private var current: ReactNativeStockfishModule? = null
    private var engineRunning = false
    private var readersStarted = false

    private fun startReader(
      event: String,
      read: (ReactNativeStockfishModule) -> String?,
    ) {
      readerScope.launch {
        while (true) {
          val module = current
          if (module == null || module.reactApplicationContext.currentActivity == null) {
            delay(DELAY_MS)
            continue
          }
          // The JNI read blocks until a fragment arrives. Drain ready output
          // immediately; delaying every fragment can exceed the UCI deadline.
          val output = read(module)
          if (output == null) {
            delay(DELAY_MS)
            continue
          }
          // A reload may have happened while the read was blocked: the line
          // goes to the JS context that is current now.
          (current ?: module).reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(event, output)
        }
      }
    }
  }
}
