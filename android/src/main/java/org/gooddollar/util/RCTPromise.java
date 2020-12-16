package org.gooddollar.util;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;

import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSDKStatus;

public final class RCTPromise {
  private RCTPromise() {}

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

  private static <T extends Enum<T>> void rejectWithStatus(Promise promise, Enum<T> status, @Nullable String message) {
    String reason = message;
    int code = status.ordinal();

    if (reason == null) {
      reason = status.toString();
    }

    promise.reject(String.valueOf(code), reason);
  }
}