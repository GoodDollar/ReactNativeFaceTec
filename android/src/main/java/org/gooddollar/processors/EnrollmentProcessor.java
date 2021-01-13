package org.gooddollar.processors;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.PermissionAwareActivity;

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

public class EnrollmentProcessor implements FaceTecFaceScanProcessor, PermissionListener {
  private Context context;
  private ProcessingSubscriber subscriber;

  private FaceTecFaceScanResultCallback lastResultCallback = null;
  private FaceTecSessionResult lastResult = null;
  private String lastMessage = null;

  private int maxRetries = -1;
  private String enrollmentIdentifier = null;
  private boolean isSuccess = false;

  private final SparseArray<PermissionsRequest> mRequests = new SparseArray<PermissionsRequest>();
  private int mRequestCode = 0;

  private interface PermissionsCallback {
    void onSuccess();
  }

  private interface SessionCallback {
    void onSuccess(String sessionToken);
  }

  private class PermissionsRequest {
    public boolean[] rationaleStatuses;
    public Callback callback;

    public PermissionsRequest(boolean[] rationaleStatuses, Callback callback) {
      this.rationaleStatuses = rationaleStatuses;
      this.callback = callback;
    }
  }

  // TODO: see EnrollmentProcessor.swift
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

  public void enroll(final String enrollmentIdentifier, @Nullable final Integer maxRetries) {
    final Context ctx = this.context;

    final SessionCallback onSessionTokenRetrieved = new SessionCallback() {
      @Override
      public void onSuccess(String sessionToken) {
        // when got token successfully - start session
        FaceTecSessionActivity.createAndLaunchSession(ctx, EnrollmentProcessor.this, sessionToken);
        EventEmitter.dispatch(EventEmitter.UXEvent.UI_READY);
      }
    };

    final PermissionsCallback onCameraAccessGranted = new PermissionsCallback() {
      @Override
      public void onSuccess() {
        // on premissions granted - issue token
        EnrollmentProcessor.this.startSession(onSessionTokenRetrieved);
      }
    };

    // store enrollmentIdentifier and maxRetries in the corresponding instance vars
    this.enrollmentIdentifier = enrollmentIdentifier;
    this.maxRetries = maxRetries;

    // request camera permissions.
    requestCameraPermissions(onCameraAccessGranted);
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

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    PermissionsRequest request = mRequests.get(requestCode);

    request.callback.invoke(grantResults, getPermissionAwareActivity(), request.rationaleStatuses);
    mRequests.remove(requestCode);
    return mRequests.size() == 0;
  }

  private void requestCameraPermissions(final PermissionsCallback callback) {
    try {
      final String permission = "android.permission.CAMERA";
      PermissionAwareActivity permissionAwareActivity = getPermissionAwareActivity();
      boolean[] rationaleStatuses = new boolean[] { permissionAwareActivity.shouldShowRequestPermissionRationale(permission) };

      mRequests.put(mRequestCode, new PermissionsRequest(
        rationaleStatuses,
        new Callback() {
          @Override
          public void invoke(Object... args) {
            int[] results = (int[]) args[0];

            // check if permission has been granted, if not reject with error
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
              callback.onSuccess();
              return;
            }

            subscriber.onCameraAccessError();
          }
        }
      ));

      permissionAwareActivity.requestPermissions(new String[]{permission}, mRequestCode, this);
      mRequestCode++;
    } catch (Exception e) {
      subscriber.onCameraAccessError();
    }
  }

  private void startSession(final SessionCallback callback) {
    FaceVerification.getSessionToken(new FaceVerification.SessionTokenCallback() {
      @Override
      public void onSessionTokenReceived(String sessionToken) {
        callback.onSuccess(sessionToken);
      }

      @Override
      public void onFailure(FaceVerification.APIException exception) {
        subscriber.onCameraAccessError();
      }
    });
  }

  private PermissionAwareActivity getPermissionAwareActivity() {
    Context ctx = this.context;

    if (ctx == null) {
      throw new IllegalStateException(
        "Tried to use permissions API while not attached to an Activity.");
    }

    if (ctx instanceof PermissionAwareActivity) {
      return (PermissionAwareActivity) ctx;
    }

    throw new IllegalStateException(
      "Tried to use permissions API but the host Activity doesn't implement PermissionAwareActivity."
    );
  }
}
