package org.gooddollar.facetec.processors;

import java.lang.Throwable;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import org.gooddollar.facetec.util.RCTPromise;

import com.facetec.sdk.FaceTecSessionResult;
import com.facetec.sdk.FaceTecSessionStatus;

public class ProcessingSubscriber {
  private Promise promise;

  public ProcessingSubscriber(Promise promise) {
    this.promise = promise;
  }

  public void onProcessingComplete(boolean isSuccess, @Nullable FaceTecSessionResult sessionResult, @Nullable String sessionMessage) {
    if (isSuccess == true) {
      promise.resolve(sessionMessage);
      return;
    }

    if (sessionResult == null) {
      throwUnexpectedError("Session could not be completed due to an unexpected issue during the network request.");
      return;
    }

    RCTPromise.rejectWith(promise, sessionResult.getStatus(), sessionMessage);
  }

  public void onSessionTokenError(@Nullable Throwable exception) {
    String message = "Session could not be started due to an unexpected issue during the network request";

    throwUnexpectedError(message + (exception == null ? "." : exception.getMessage()));
  }

  public void onSessionContextSwitch() {
    RCTPromise.rejectWith(promise, FaceTecSessionStatus.CONTEXT_SWITCH);
  }

  public void onCameraAccessError() {
    RCTPromise.rejectWith(promise, FaceTecSessionStatus.CAMERA_PERMISSION_DENIED);
  }

  private void throwUnexpectedError(String message) {
    RCTPromise.rejectWith(promise, FaceTecSessionStatus.UNKNOWN_INTERNAL_ERROR, message);
  }
}
