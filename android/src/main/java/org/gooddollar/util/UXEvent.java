package org.gooddollar.util;

import java.util.Map;
import java.util.HashMap;

public enum UXEvent {
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
