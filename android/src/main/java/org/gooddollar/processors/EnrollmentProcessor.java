package org.gooddollar.processors;

import androidx.annotation.Nullable;
import android.content.Context;

import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecFaceScanProcessor;
import com.facetec.sdk.FaceTecFaceScanResultCallback;
import com.facetec.sdk.FaceTecSessionActivity;
import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSessionResult;

import org.gooddollar.api.FaceVerification;
import org.gooddollar.processors.ProcessingSubscriber;
import org.gooddollar.util.EventEmitter;
import org.gooddollar.util.Customization;

import okhttp3.RequestBody;
import org.json.JSONObject;

public class EnrollmentProcessor implements FaceTecFaceScanProcessor {
  private Context context;
  private ProcessingSubscriber subscriber;

  private FaceTecFaceScanResultCallback lastResultCallback = null;
  private FaceTecSessionResult lastResult = null;
  private String lastMessage = null;

  private int maxRetries = -1;
  private String enrollmentIdentifier = null;
  private boolean isSuccess = false;

  // TODO: see EnrollmentProcessor.swift
  // add helper methods for
  // 1. request camera permissions

  // 2. start session
  // a) get session token using api.FaceVerification. if fails - call subscriber.onCameraAccessError()
  // b) instantiate FaceTecSessionActivity with session token
  // c) dispatch UI_READY event

  // 3. process enrollment failure (including maxRetries logic - see EnrollmentProcessor.web.js)
  // a) FaceVerification.APIException has JSONObject getResponse() method which returns server response
  // b) we're calling OUR server so it will have { success, error, enrollmentResult: { isLive, isEnroll, ... etc flags } } shape
  // c) look at the web processor fot the logic should be used
  // d) call lastResultCallback.cancel() or retry()
  // e) dispatch FV_RETRY event if retry

  public EnrollmentProcessor(Context context, ProcessingSubscriber subscriber) {
    this.context = context;
    this.subscriber = subscriber;
  }

  public ProcessingSubscriber getSubscriber() {
    return subscriber;
  }

  public void enroll(String enrollmentIdentifier) {
    enroll(enrollmentIdentifier, -1);
  }

  public void enroll(String enrollmentIdentifier, @Nullable Integer maxRetries) {
    // 1. request camera permissions. if fails - call subscriber.onCameraAccessError()

    // 2. store enrollmentIdentifier and maxRetries in the corresponding instance vars
    this.enrollmentIdentifier = enrollmentIdentifier;
    this.maxRetries = maxRetries;

    // 3. call start session
    FaceVerification.getSessionToken(new FaceVerification.SessionTokenCallback() {
      @Override
      public void onSessionTokenReceived(String sessionToken) {
        FaceTecSessionActivity.createAndLaunchSession(context, EnrollmentProcessor.this, sessionToken);
        EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
        subscriber.onProcessingComplete(true, null, Customization.resultSuccessMessage);
      }

      @Override
      public void onFailure(FaceVerification.APIException exception) {
        exception.printStackTrace();
        subscriber.onCameraAccessError();
      }
    });


  }

  public void processSessionWhileFaceTecSDKWaits(final FaceTecSessionResult sessionResult, final FaceTecFaceScanResultCallback faceScanResultCallback) {
    // TODO: see EnrollmentProcessor.swift and EnrollmentProcessor.java from the demo app
    // 1. check for cancellation/timeout. call cancel() and cancelPendingRequests() and return in that case
    // 2. store sessionResult and faceScanResultCallback in the corresponding instance vars
    // 3. prepare params as the JSONObject. incluide also sessionResult.getSessionId() as 'sessionId'
    // 4. convert it RequestBody via RequestBody customRequest = FaceVerification.jsonStringify(params);
    // 5. create ProgressRequestBody with listener, like in the demo app. but as the first arg pass customRequest retrieved on the prev step
    // 6. when upload progress reaches 100% override upload message to Customization.resultFacescanProcessingMessage
    // 7. use enroll's overload accepting RequestBody second arg. pass ProgressRequestBody from the prev step to it
    // 8. in onSuccess
    // a) call .succeed()
    // b) FaceTecCustomization.overrideResultScreenSuccessMessage = Customization.resultSuccessMessage
    // c) set lastMessage Customization.resultSuccessMessage
    // d) set isSuccess to true
    // e) call FaceTecSDK.unload() to free resources.
    // also read the docs / test - pribably it should be done always even in FV failed
    // in that case call unload from onFaceTecSDKCompletelyDone
    // 9. in onFailure
    // if error is FaceVerification.APIException and getResponse() doesn't returns null = call process enrollment failure helper
    // otherwise - just .cancel()
  }

  public void onFaceTecSDKCompletelyDone() {
    subscriber.onProcessingComplete(isSuccess, lastResult, lastMessage);
  }
}
