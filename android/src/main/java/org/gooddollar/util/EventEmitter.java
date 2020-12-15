package org.gooddollar.util;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;

import org.gooddollar.util.UXEvent;

public final class EventEmitter {
  private EventEmitter() {}
  private static DeviceEventManagerModule.RCTDeviceEventEmitter rctEventEmitter;

  public static void register(ReactApplicationContext reactContext) {
    rctEventEmitter = reactContext.getJSModule(
      DeviceEventManagerModule.RCTDeviceEventEmitter.class
    );
  }

  public static void dispatch(UXEvent event) {
    dispatch(event, null);
  }

  public static void dispatch(UXEvent event, @Nullable WritableMap body) {
    rctEventEmitter.emit(event.eventName(), body);
  }
}
