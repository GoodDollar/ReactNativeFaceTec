package org.gooddollar.processors;

import org.gooddollar.util.EventEmitter;
import org.gooddollar.util.UXEvent;

public class EnrollmentProcessor {
  public EnrollmentProcessor() {
    EventEmitter.dispatch(UXEvent.UI_READY);
  }
}
