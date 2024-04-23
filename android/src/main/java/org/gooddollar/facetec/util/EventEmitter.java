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

  // event type enum
  // defined as enum class 
  // as have string (non - int) values
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

    // helper method used to export enum to JS constants
    // without specifying all items manually
    public static Map<String, String> toMap() {
      Map<String, String> enumMap = new HashMap<>();

      for (UXEvent enumItem : UXEvent.values()) {
        enumMap.put(enumItem.name(), enumItem.eventName());
      }

      return enumMap;
    }
  }

  // connects EventEmitter to the react context
  public static void register(ReactApplicationContext reactContext) {
    // fetches event emitter from context and stores to the class var
    // this reference will be used to send events from native to JS
    rctEventEmitter = reactContext.getJSModule(
      DeviceEventManagerModule.RCTDeviceEventEmitter.class
    );
  }

  // overload to send event without data
  public static void dispatch(UXEvent event) {
    dispatch(event, null);
  }

  // send event with data. just re-calls native => js event emitter
  public static void dispatch(UXEvent event, @Nullable WritableMap body) {
    rctEventEmitter.emit(event.eventName(), body);
  }
}
