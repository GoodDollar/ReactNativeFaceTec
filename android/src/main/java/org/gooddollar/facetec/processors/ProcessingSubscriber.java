package org.gooddollar.facetec.processors;

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
      String results = "" ;
      if(sessionResult != null) {
        results += sessionResult.getFaceScanBase64() + "," + sessionResult.getAuditTrailCompressedBase64()[0];
      } else {
        results = "";
      }
      promise.resolve(results);
      return;
    }

    if (sessionResult == null) {
      onSessionTokenError();
      return;
    }

    RCTPromise.rejectWith(promise, sessionResult.getStatus(), sessionMessage);
  }

  public void onSessionTokenError() {
    String message = "Session could not be started due to an unexpected issue during the network request.";

    RCTPromise.rejectWith(promise, FaceTecSessionStatus.UNKNOWN_INTERNAL_ERROR, message);
  }

  public void onSessionContextSwitch() {
    RCTPromise.rejectWith(promise, FaceTecSessionStatus.CONTEXT_SWITCH);
  }

  public void onCameraAccessError() {
    RCTPromise.rejectWith(promise, FaceTecSessionStatus.CAMERA_PERMISSION_DENIED);
  }
}
