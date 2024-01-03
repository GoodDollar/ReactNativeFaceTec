package org.gooddollar.facetec.processors;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecFaceScanProcessor;
import com.facetec.sdk.FaceTecFaceScanResultCallback;
import com.facetec.sdk.FaceTecSessionActivity;
import com.facetec.sdk.FaceTecSessionResult;
import com.facetec.sdk.FaceTecSessionStatus;

import org.gooddollar.facetec.api.FaceVerification;
import org.gooddollar.facetec.api.NetworkingHelpers;
import org.gooddollar.facetec.api.ProgressRequestBody;
import org.gooddollar.facetec.util.Customization;
import org.gooddollar.facetec.util.EventEmitter;
import org.gooddollar.facetec.util.Permissions;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class EnrollmentProcessor implements FaceTecFaceScanProcessor {
  private Context context;
  private ProcessingSubscriber subscriber;
  private Permissions permissions;

  private FaceTecFaceScanResultCallback lastResultCallback = null;
  private FaceTecSessionResult lastResult = null;
  private String lastMessage = null;

  private Integer timeout = null;
  private int maxRetries = -1;
  private int retryAttempt = 0;
  private String enrollmentIdentifier = null;
  private String v1Identifier = null;
  private String chainId = null;
  private boolean isSuccess = false;

  // TODO: research about unload for BOTH ios/android

  public EnrollmentProcessor(Context context, ProcessingSubscriber subscriber) {
    this.context = context;
    this.subscriber = subscriber;
    permissions = new Permissions(context);
  }

  public ProcessingSubscriber getSubscriber() {
    return subscriber;
  }

  public void enroll(final String enrollmentIdentifier, final String v1Identifier) {
    enroll(enrollmentIdentifier, v1Identifier, null, null, null);
  }
  
  public void enroll(final String enrollmentIdentifier, final String v1Identifier, final String chainId) {
    enroll(enrollmentIdentifier, v1Identifier, chainId, null, null);
  }

  public void enroll(final String enrollmentIdentifier, final String v1Identifier, final String chainId, final Integer maxRetries) {
    enroll(enrollmentIdentifier, v1Identifier, chainId, maxRetries, null);
  }

  public void enroll(final String enrollmentIdentifier, final String v1Identifier, @Nullable final String chainId, @Nullable final Integer maxRetries, @Nullable final Integer timeout) {
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
          Log.w("enroll", exception);
          subscriber.onSessionTokenError();
        }
      };

    // store enrollmentIdentifier, maxRetries and timeout in the corresponding instance vars
    this.enrollmentIdentifier = enrollmentIdentifier;
    this.v1Identifier = v1Identifier;
    this.chainId = chainId;

    if ((maxRetries != null) && (maxRetries >= 0)) {
      this.maxRetries = maxRetries;
    }

    if ((timeout != null) && (timeout > 0)) {
      this.timeout = timeout;
    }

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
    lastResult = sessionResult;
    lastResultCallback = faceScanResultCallback;

    if (sessionResult.getStatus() != FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY) {
      NetworkingHelpers.cancelPendingRequests();
      faceScanResultCallback.cancel();

      return;
    }

    // notifying that capturing is done
    EventEmitter.dispatch(EventEmitter.UXEvent.CAPTURE_DONE);

    // perform verification
    sendEnrollmentRequest();
  }

  public void onFaceTecSDKCompletelyDone() {
    subscriber.onProcessingComplete(isSuccess, lastResult, lastMessage);
  }

  private RequestBody createEnrollmentRequest(JSONObject payload) {
    final FaceTecFaceScanResultCallback resultCallback = lastResultCallback;


    return new ProgressRequestBody(FaceVerification.jsonStringify(payload),
      new ProgressRequestBody.Listener() {
        @Override
        public void onUploadProgressChanged(long bytesWritten, long totalBytes) {
          // get progress while performing the upload
          final float uploaded = ((float) bytesWritten) / ((float) totalBytes);

          // updating the UX, upload progress from 10 to 80%
          resultCallback.uploadProgress(0.1f + 0.7f * uploaded);
        }
      }
    );
  }

  private void sendEnrollmentRequest() {
    final FaceTecFaceScanResultCallback resultCallback = lastResultCallback;
    JSONObject payload = new JSONObject();

    // setting initial progress to 0 for freeze progress bar
    resultCallback.uploadProgress(0);

    try {
      payload.put("facescan", lastResult.getFaceScanBase64());
      payload.put("audit_trail_image", lastResult.getAuditTrailCompressedBase64()[0]);
    } catch(Exception e) {
      lastMessage = "Exception raised while attempting to create JSON payload for upload.";
      resultCallback.cancel();
    }

    RequestBody request = createEnrollmentRequest(payload);
    FaceVerification.enroll(enrollmentIdentifier, request, timeout, new FaceVerification.APICallback() {
      @Override
      public void onSuccess(JSONObject response) {
        String successMessage = Customization.resultSuccessMessage;
        JSONObject enrollmentResult = getEnrollmentResult(response);
        String resultBlob = enrollmentResult.optString("scan_results_blob");

        resultCallback.uploadProgress(1);

        if (resultBlob.isEmpty()) {
          FaceVerification.APIException exception = new FaceVerification.APIException(
            FaceVerification.unexpectedMessage, response
          );

          EnrollmentProcessor.this.handleEnrollmentError(exception);
          return;
        }

        EnrollmentProcessor.this.isSuccess = true;
        EnrollmentProcessor.this.lastMessage = successMessage;

        resultCallback.succeed();
        resultCallback.proceedToNextStep(resultBlob);
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
    lastMessage = exception.getMessage();

    if (response != null) {
      JSONObject enrollmentResult = getEnrollmentResult(response);

      // if isDuplicate is strictly true, that means we have dup face
      boolean isDuplicateIssue = enrollmentResult.optBoolean("isDuplicate", false);
      boolean is3DMatchIssue = enrollmentResult.optBoolean("isNotMatch", false);
      boolean isEnrolled = enrollmentResult.optBoolean("isEnrolled", false);
      // in JS code we're checking for false === isLive strictly. so if no isLive flag in the response,
      // we assume that liveness check was successfull. That's why we're setting true as fallback value
      boolean isLivenessIssue = enrollmentResult.optBoolean("isLive", true);
      // getting result Blob to use in the retry case
      String resultBlob = enrollmentResult.optString("resultBlob");

      // if there's no duplicate / 3d match issues but we have
      // liveness issue strictly - we'll check for possible session retry
      if (isLivenessIssue && (resultBlob != null) && !isDuplicateIssue && !is3DMatchIssue) {
        // if haven't reached retries threshold or max retries is disabled
        // (is null or < 0) we'll ask to retry capturing
        if ((maxRetries < 0) || (retryAttempt < maxRetries)) {
          // increasing retry attempts counter
          retryAttempt += 1;
          // showing reason
          lastResultCallback.uploadMessageOverride(lastMessage);
          // notifying about retry
          lastResultCallback.proceedToNextStep(resultBlob);

          // dispatching retry event
          WritableMap eventData = Arguments.createMap();

          eventData.putString("reason", lastMessage);
          eventData.putBoolean("match3d", !is3DMatchIssue);
          eventData.putBoolean("liveness", !isLivenessIssue);
          eventData.putBoolean("duplicate", isDuplicateIssue);
          eventData.putBoolean("enrolled", isEnrolled);

          EventEmitter.dispatch(EventEmitter.UXEvent.FV_RETRY, eventData);
          return;
        }
      }
    }

    lastResultCallback.cancel();
  }

  private JSONObject getEnrollmentResult(JSONObject response) {
    if (response == null) {
      return new JSONObject();
    }

    return response;
  }
}
