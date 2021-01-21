package org.gooddollar.facetec.processors;

import androidx.annotation.Nullable;
import android.content.Context;
import org.json.JSONObject;

import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecFaceScanProcessor;
import com.facetec.sdk.FaceTecFaceScanResultCallback;
import com.facetec.sdk.FaceTecSessionActivity;
import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSessionResult;
import com.facetec.sdk.FaceTecCustomization;

import org.gooddollar.facetec.api.FaceVerification;
import org.gooddollar.facetec.api.NetworkingHelpers;
import org.gooddollar.facetec.api.ProgressRequestBody;
import okhttp3.RequestBody;

import org.gooddollar.facetec.processors.ProcessingSubscriber;
import org.gooddollar.facetec.util.EventEmitter;
import org.gooddollar.facetec.util.Customization;
import org.gooddollar.facetec.util.Permissions;

public class EnrollmentProcessor implements FaceTecFaceScanProcessor {
  private Context context;
  private ProcessingSubscriber subscriber;
  private Permissions permissions;

  private FaceTecFaceScanResultCallback lastResultCallback = null;
  private FaceTecSessionResult lastResult = null;
  private String lastMessage = null;

  private int maxRetries = -1;
  private String enrollmentIdentifier = null;
  private boolean isSuccess = false;

  // TODO: research about unload for BOTH ios/android

  public EnrollmentProcessor(Context context, ProcessingSubscriber subscriber) {
    this.context = context;
    this.subscriber = subscriber;
    this.permissions = new Permissions(context);
  }

  public ProcessingSubscriber getSubscriber() {
    return subscriber;
  }

  public void enroll(String enrollmentIdentifier) {
    enroll(enrollmentIdentifier, -1);
  }

  public void enroll(final String enrollmentIdentifier, @Nullable final Integer maxRetries) {
    final Context ctx = this.context;
    final ProcessingSubscriber subscriber = this.subscriber;

    final FaceVerification.SessionTokenCallback onSessionTokenRetrieved =
      new FaceVerification.SessionTokenCallback() {
        @Override
        public void onSessionTokenReceived(String sessionToken) {
          // when got token successfully - start session
          FaceTecSessionActivity.createAndLaunchSession(ctx, EnrollmentProcessor.this, sessionToken);
          EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
        }

        @Override
        public void onFailure(FaceVerification.APIException exception) {
          subscriber.onSessionTokenError();
        }
      };

    // store enrollmentIdentifier and maxRetries in the corresponding instance vars
    this.enrollmentIdentifier = enrollmentIdentifier;
    this.maxRetries = maxRetries;

    // request camera permissions.
    this.permissions.requestCameraPermissions(new Permissions.PermissionsCallback() {
      @Override
      public void onSuccess() {
        // on premissions granted - issue token
        FaceVerification.getSessionToken(onSessionTokenRetrieved);
      }

      @Override
      public void onFailure() {
        subscriber.onCameraAccessError();
      }
    });
  }

  public void processSessionWhileFaceTecSDKWaits(
    final FaceTecSessionResult sessionResult,
    final FaceTecFaceScanResultCallback faceScanResultCallback
  ) {
    this.lastResult = sessionResult;
    this.lastResultCallback = faceScanResultCallback;

    if (sessionResult.getStatus() != FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY) {
      NetworkingHelpers.cancelPendingRequests();
      faceScanResultCallback.cancel();

      return;
    }

    // notifying that capturing is done
    EventEmitter.dispatch(EventEmitter.UXEvent.CAPTURE_DONE);

    // perform verification
    sendEnrollmentRequest();
    // TODO: see EnrollmentProcessor.swift and EnrollmentProcessor.java from the demo app
    // 9. in onFailure
    // if error is FaceVerification.APIException and getResponse() doesn't returns null = call process enrollment failure helper
    // otherwise - just .cancel()
    // to process enrollment failure (including maxRetries logic - see EnrollmentProcessor.web.js)
    // a) FaceVerification.APIException has JSONObject getResponse() method which returns server response
    // b) we're calling OUR server so it will have { success, error, enrollmentResult: { isLive, isEnroll, ... etc flags } } shape
    // c) look at the web processor fot the logic should be used
    // d) call lastResultCallback.cancel() or retry()
    // e) dispatch FV_RETRY event if retry
  }

  public void onFaceTecSDKCompletelyDone() {
    subscriber.onProcessingComplete(isSuccess, lastResult, lastMessage);
  }

  private RequestBody createEnrollmentRequest(JSONObject payload) {
    final FaceTecFaceScanResultCallback resultCallback = this.lastResultCallback;

    return new ProgressRequestBody(FaceVerification.jsonStringify(payload),
      new ProgressRequestBody.Listener() {
        @Override
        public void onUploadProgressChanged(long bytesWritten, long totalBytes) {
          // get progress while performing the upload
          final float uploaded = ((float) bytesWritten) / ((float) totalBytes);

          // updating the UX, upload progress from 10 to 80%
          resultCallback.uploadProgress(0.1f + 0.7f * uploaded);

          if (bytesWritten == totalBytes) {
            // switch status message to processing once upload completed
            resultCallback.uploadMessageOverride(Customization.resultFacescanProcessingMessage);
          }
        }
      }
    );
  }

  private void sendEnrollmentRequest() {
    final FaceTecFaceScanResultCallback resultCallback = this.lastResultCallback;
    JSONObject payload = new JSONObject();

    // setting initial progress to 0 for freeze progress bar
    resultCallback.uploadProgress(0);

    try {
      payload.put("faceScan", this.lastResult.getFaceScanBase64());
      payload.put("auditTrailImage", this.lastResult.getAuditTrailCompressedBase64()[0]);
      payload.put("lowQualityAuditTrailImage", this.lastResult.getLowQualityAuditTrailCompressedBase64()[0]);
      payload.put("sessionId", this.lastResult.getSessionId());
    } catch(Exception e) {
      this.lastMessage = "Exception raised while attempting to create JSON payload for upload.";
      resultCallback.cancel();
    }

    RequestBody request = createEnrollmentRequest(payload);
    FaceVerification.enroll(this.enrollmentIdentifier, request, new FaceVerification.APICallback() {
      @Override
      public void onSuccess(JSONObject response) {
        String successMessage = Customization.resultSuccessMessage;

        resultCallback.uploadProgress(1);
        EnrollmentProcessor.this.isSuccess = true;

        EnrollmentProcessor.this.lastMessage = successMessage;
        FaceTecCustomization.overrideResultScreenSuccessMessage = successMessage;

        resultCallback.succeed();
      }

      @Override
      public void onFailure(FaceVerification.APIException exception) {
        resultCallback.uploadProgress(1);
        EnrollmentProcessor.this.handleEnrollmentError(exception);
      }
    });
  }

  private void handleEnrollmentError(FaceVerification.APIException exception) {
    JSONObject response = exception.getResponse();

    // by default we'll use exception's message as lastMessage
    this.lastMessage = exception.getMessage();

    if (response != null) {
      // TODO: check response, check is liveness issue, apply retry logic
    }

    this.lastResultCallback.cancel();
  }
}
