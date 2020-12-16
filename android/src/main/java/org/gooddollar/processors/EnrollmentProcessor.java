package org.gooddollar.processors;

import org.gooddollar.util.EventEmitter;

public class EnrollmentProcessor {
  public EnrollmentProcessor() {
    EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
  }
}
