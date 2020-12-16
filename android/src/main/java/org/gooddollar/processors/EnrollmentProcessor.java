package org.gooddollar.processors;

import androidx.annotation.Nullable;
import android.content.Context;

import com.facetec.sdk.FaceTecFaceScanProcessor;
import com.facetec.sdk.FaceTecFaceScanResultCallback;
import com.facetec.sdk.FaceTecSessionActivity;
import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSessionResult;

import org.gooddollar.processors.ProcessingSubscriber;
import org.gooddollar.util.EventEmitter;

public class EnrollmentProcessor implements FaceTecFaceScanProcessor {
  private Context context;
  private ProcessingSubscriber subscriber;

  private FaceTecFaceScanResultCallback lastResultCallback = null;
  private FaceTecSessionResult lastResult = null;
  private String lastMessage = null;

  private int maxRetries = -1;
  private String enrollmentIdentifier = null;
  private boolean isSuccess = false;

  public EnrollmentProcessor(Context context, ProcessingSubscriber subscriber) {
    this.context = context;
    this.subscriber = subscriber;
  }

  public void enroll(String enrollmentIdentifier) {
    enroll(enrollmentIdentifier, -1);
  }

  public void enroll(String enrollmentIdentifier, @Nullable Integer maxRetries) {
    EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
    subscriber.onProcessingComplete(true, null, "You're an amazing unicorn!");
  }

  public void processSessionWhileFaceTecSDKWaits(final FaceTecSessionResult sessionResult, final FaceTecFaceScanResultCallback faceScanResultCallback) {

  }
}
