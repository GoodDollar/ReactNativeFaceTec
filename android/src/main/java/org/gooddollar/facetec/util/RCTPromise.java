package org.gooddollar.facetec.util;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;

import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSDKStatus;

// utlity wrapper to reject JS promise with corresponding status & message
// (via native => JS react bridge promise reference given)
public final class RCTPromise {
  private RCTPromise() {}

  // overloads for the different cases (for SDK or session status, with or without custom error message)
  public static void rejectWith(Promise promise, FaceTecSDKStatus status) {
    rejectWith(promise, status, null);
  }

  public static void rejectWith(Promise promise, FaceTecSDKStatus status, @Nullable String message) {
    rejectWithStatus(promise, status, message);
  }

  public static void rejectWith(Promise promise, FaceTecSessionStatus status) {
    rejectWith(promise, status, null);
  }

  public static void rejectWith(Promise promise, FaceTecSessionStatus status, @Nullable String message) {
    rejectWithStatus(promise, status, message);
  }

  // overloads "aggregator" receiving all params possible
  private static <T extends Enum<T>> void rejectWithStatus(Promise promise, Enum<T> status, @Nullable String message) {
    String reason = message;
    int code = status.ordinal(); // convert enum item to integer code

    if (reason == null) { // if no custom error message specified, use status description (from enum item)
      reason = status.toString();
    }

    // reject with code & message
    promise.reject(String.valueOf(code), reason);
  }
}