package org.gooddollar.facetec.processors;

import androidx.annotation.Nullable;
import android.content.Context;
import org.json.JSONObject;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

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

// Implements face verification flow. Based on the class from the FaceTec demo app
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

  // instantiated with content context (activity) and subscriber  
  public EnrollmentProcessor(Context context, ProcessingSubscriber subscriber) {
    this.context = context;
    this.subscriber = subscriber;
    permissions = new Permissions(context);
  }

  public ProcessingSubscriber getSubscriber() {
    return subscriber;
  }

  // different enroll() overloads covering params defaults
  public void enroll(final String enrollmentIdentifier, final String v1Identifier) {
    enroll(enrollmentIdentifier, v1Identifier, null, null, null);
  }
  
  public void enroll(final String enrollmentIdentifier, final String v1Identifier, final String chainId) {
    enroll(enrollmentIdentifier, v1Identifier, chainId, null, null);
  }

  public void enroll(final String enrollmentIdentifier, final String v1Identifier, final String chainId, final Integer maxRetries) {
    enroll(enrollmentIdentifier, v1Identifier, chainId, maxRetries, null);
  }

  // starts FV session
  public void enroll(final String enrollmentIdentifier, final String v1Identifier, @Nullable final String chainId, @Nullable final Integer maxRetries, @Nullable final Integer timeout) {
    final Context ctx = this.context;
    final ProcessingSubscriber subscriber = this.subscriber;

    // get session token callback
    final FaceVerification.SessionTokenCallback onSessionTokenRetrieved =
      new FaceVerification.SessionTokenCallback() {
        @Override
        public void onSessionTokenReceived(String sessionToken) {
          // when got token successfully - show FV UI
          FaceTecSessionActivity.createAndLaunchSession(ctx, EnrollmentProcessor.this, sessionToken);
          EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
        }

        @Override
        public void onFailure(FaceVerification.APIException exception) {
          // otherwise reject with specific error
          subscriber.onSessionTokenError(exception);
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
        // on premissions granted - get session token, pas callbacks
        FaceVerification.getSessionToken(onSessionTokenRetrieved);
      }

      @Override
      public void onFailure() {
        // otherwise reject with specific error
        subscriber.onCameraAccessError();
      }
    });
  }

  // capturing done/failed callback
  // logic the same as on the web
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

  // done callback
  // logic the same as on the web
  public void onFaceTecSDKCompletelyDone() {
    // recalls processing subscriber with success slate and last result/message
    subscriber.onProcessingComplete(isSuccess, lastResult, lastMessage);
  }

  // enrollment request factory helper
  private RequestBody createEnrollmentRequest(JSONObject payload) {
    final FaceTecFaceScanResultCallback resultCallback = lastResultCallback;

    // create request with send progress listener
    return new ProgressRequestBody(FaceVerification.jsonStringify(payload),
      new ProgressRequestBody.Listener() {
        // send from total listener, the logic same as on web
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

  // send request helper, processing logic same as on web
  private void sendEnrollmentRequest() {
    final FaceTecFaceScanResultCallback resultCallback = lastResultCallback;
    JSONObject payload = new JSONObject();

    // setting initial progress to 0 for freeze progress bar
    resultCallback.uploadProgress(0);

    try {
      // same request as on web
      // { faceScan, auditTrailImage, lowQualityAuditTrailImage, sessionId, fvSigner }
      payload.put("faceScan", lastResult.getFaceScanBase64());
      payload.put("auditTrailImage", lastResult.getAuditTrailCompressedBase64()[0]);
      payload.put("lowQualityAuditTrailImage", lastResult.getLowQualityAuditTrailCompressedBase64()[0]);
      payload.put("sessionId", lastResult.getSessionId());
      payload.put("fvSigner", this.v1Identifier);

      // if no chainId then DO NOT send chainId in body
      if (this.chainId != null) {
        payload.put("chainId", this.chainId);
      }
    } catch(Exception e) {
      lastMessage = "Exception raised while attempting to create JSON payload for upload.";
      resultCallback.cancel();
    }

    RequestBody request = createEnrollmentRequest(payload);
    FaceVerification.enroll(enrollmentIdentifier, request, timeout, new FaceVerification.APICallback() {
      @Override
      public void onSuccess(JSONObject response) { // same logic as on web
        String successMessage = Customization.resultSuccessMessage;
        JSONObject enrollmentResult = getEnrollmentResult(response);
        String resultBlob = enrollmentResult.optString("resultBlob"); // response.data.resultBlob

        resultCallback.uploadProgress(1);

        // no result blob - throw unknown error
        if (resultBlob == null) {
          FaceVerification.APIException exception = new FaceVerification.APIException(
            FaceVerification.unexpectedMessage, response
          );

          EnrollmentProcessor.this.handleEnrollmentError(exception);
          return;
        }

        // GoodServer returs only success blob
        // any specific error as dup or low quality 
        // throws exception and is processed at onFailure
        EnrollmentProcessor.this.isSuccess = true; // set success sate & message
        EnrollmentProcessor.this.lastMessage = successMessage;
        FaceTecCustomization.overrideResultScreenSuccessMessage = successMessage; // show unicorn greeting at UI

        // finish flow with OK
        resultCallback.succeed();
        resultCallback.proceedToNextStep(resultBlob);
      }

      @Override
      public void onFailure(FaceVerification.APIException exception) {
        resultCallback.uploadProgress(1); // on any error set procressbar complete
        EnrollmentProcessor.this.handleEnrollmentError(exception); // and handle error
      }
    });
  }

  // handles enrollment error. logic the same as on web
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

  //  reads response.data.enrollmentResult from GoodServer enroll response
  private JSONObject getEnrollmentResult(JSONObject response) {
    JSONObject enrollmentResult = response.optJSONObject("enrollmentResult");

    if (enrollmentResult == null) {
      return new JSONObject();
    }

    return enrollmentResult;
  }
}
