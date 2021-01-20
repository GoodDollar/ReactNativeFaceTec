package org.gooddollar.facetec.util;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;

import java.util.Map;
import java.util.HashMap;

public final class EventEmitter {
  private EventEmitter() {}
  private static DeviceEventManagerModule.RCTDeviceEventEmitter rctEventEmitter;

  public static enum UXEvent {
    UI_READY("onUIReady"),
    CAPTURE_DONE("onCaptureDone"),
    FV_RETRY("onRetry");

    private final String eventName;

    UXEvent(String eventName) {
      this.eventName = eventName;
    }

    public String eventName() {
      return eventName;
    }

    public static Map<String, String> toMap() {
      Map<String, String> enumMap = new HashMap<>();

      for (UXEvent enumItem : UXEvent.values()) {
        enumMap.put(enumItem.name(), enumItem.eventName());
      }

      return enumMap;
    }
  }

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
