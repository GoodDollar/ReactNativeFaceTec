package org.gooddollar;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.PermissionAwareActivity;

import java.util.Map;
import java.util.HashMap;

import org.gooddollar.api.FaceVerification;
import org.gooddollar.processors.EnrollmentProcessor;
import org.gooddollar.processors.ProcessingSubscriber;

import org.gooddollar.util.EventEmitter;
import org.gooddollar.util.Customization;
import org.gooddollar.util.RCTPromise;

import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSDKStatus;

public class FaceTecModule extends ReactContextBaseJavaModule implements PermissionListener {
    private final ReactApplicationContext reactContext;
    private EnrollmentProcessor lastProcessor = null;

    private final SparseArray<Request> mRequests;
    private int mRequestCode = 0;

    private final ActivityEventListener activityListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (lastProcessor == null) {
                return;
            }

            lastProcessor.onFaceTecSDKCompletelyDone();
            lastProcessor = null;
        }
    };

    public FaceTecModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
        reactContext.addActivityEventListener(activityListener);
        mRequests = new SparseArray<Request>();
    }

    @Override
    public void initialize() {
        super.initialize();

        EventEmitter.register(reactContext);
        FaceTecSDK.setCustomization(Customization.UICustomization);
    }

    @Override
    public String getName() {
        return "FaceTecModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        final Map<String, Integer> faceTecSDKStatus = new HashMap<>();
        final Map<String, Integer> faceTecSessionStatus = new HashMap<>();

        // SDK statuses
        final Object[][] sdkStatuses = {
            // common statuses (status names are aligned with the web sdk)
            {"NeverInitialized",  FaceTecSDKStatus.NEVER_INITIALIZED},
            {"Initialized",  FaceTecSDKStatus.INITIALIZED},
            {"NetworkIssues", FaceTecSDKStatus.NETWORK_ISSUES},
            {"InvalidDeviceKeyIdentifier",  FaceTecSDKStatus.INVALID_DEVICE_KEY_IDENTIFIER},
            {"VersionDeprecated",  FaceTecSDKStatus.VERSION_DEPRECATED},
            {"DeviceNotSupported",  FaceTecSDKStatus.DEVICE_NOT_SUPPORTED},
            {"DeviceInLandscapeMode",  FaceTecSDKStatus.DEVICE_IN_LANDSCAPE_MODE},
            {"DeviceInReversePortraitMode", FaceTecSDKStatus.DEVICE_IN_REVERSE_PORTRAIT_MODE},
            {"DeviceLockedOut",  FaceTecSDKStatus.DEVICE_LOCKED_OUT},
            {"KeyExpiredOrInvalid",  FaceTecSDKStatus.KEY_EXPIRED_OR_INVALID},
            // native-specific statuses
            {"GracePeriodExceeded", FaceTecSDKStatus.GRACE_PERIOD_EXCEEDED},
            {"EncryptionKeyInvalid", FaceTecSDKStatus.ENCRYPTION_KEY_INVALID},
        };

        // Session statuses
        final Object[][] sessionStatuses = {
            // common statuses (status names are aligned with the web sdk)
            {"UserCancelled",  FaceTecSessionStatus.USER_CANCELLED},
            {"SessionCompletedSuccessfully",  FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY},
            {"Timeout",  FaceTecSessionStatus.TIMEOUT},
            {"UnknownInternalError",  FaceTecSessionStatus.UNKNOWN_INTERNAL_ERROR},
            {"ContextSwitch",  FaceTecSessionStatus.CONTEXT_SWITCH},
            {"LockedOut",  FaceTecSessionStatus.LOCKED_OUT},
            {"LandscapeModeNotAllowed",  FaceTecSessionStatus.LANDSCAPE_MODE_NOT_ALLOWED},
            {"MissingGuidanceImages",  FaceTecSessionStatus.MISSING_GUIDANCE_IMAGES},
            {"UserCancelledViaClickableReadyScreenSubtext",  FaceTecSessionStatus.USER_CANCELLED_VIA_CLICKABLE_READY_SCREEN_SUBTEXT},
            {"NonProductionModeDeviceKeyIdentifierInvalid",  FaceTecSessionStatus.NON_PRODUCTION_MODE_KEY_INVALID},
            {"CameraNotEnabled",  FaceTecSessionStatus.CAMERA_INITIALIZATION_ISSUE},
            // native-specific statuses
            {"CameraPermissionDenied",  FaceTecSessionStatus.CAMERA_PERMISSION_DENIED},
            {"NonProductionModeNetworkRequired",  FaceTecSessionStatus.NON_PRODUCTION_MODE_NETWORK_REQUIRED},
            {"GracePeriodExceeded",  FaceTecSessionStatus.GRACE_PERIOD_EXCEEDED},
            {"UserCancelledViaHardwareButton",  FaceTecSessionStatus.USER_CANCELLED_VIA_HARDWARE_BUTTON},
            {"SessionUnsuccessful",  FaceTecSessionStatus.SESSION_UNSUCCESSFUL},
            {"EncryptionKeyInvalid",  FaceTecSessionStatus.ENCRYPTION_KEY_INVALID},
            {"ReversePortraitNotAllowed",  FaceTecSessionStatus.REVERSE_PORTRAIT_NOT_ALLOWED},
        };

        // put statuses to the maps
        for (Object[] pair : sdkStatuses) {
            String key = (String) pair[0];
            FaceTecSDKStatus value = (FaceTecSDKStatus) pair[1];

            faceTecSDKStatus.put(key, value.ordinal());
        }

        for (Object[] pair : sessionStatuses) {
            String key = (String) pair[0];
            FaceTecSessionStatus value = (FaceTecSessionStatus) pair[1];

            faceTecSessionStatus.put(key, value.ordinal());
        }

        // aggregating all constants in a single object literal exported to JS
        constants.put("FaceTecUxEvent", EventEmitter.UXEvent.toMap());
        constants.put("FaceTecSDKStatus", faceTecSDKStatus);
        constants.put("FaceTecSessionStatus", faceTecSessionStatus);
        return constants;
    }

    @ReactMethod
    public void initializeSDK(String serverURL, String jwtAccessToken,
          String licenseKey, String encryptionKey, String licenseText,
          final Promise promise
    ) {
        final Activity activity = getCurrentActivity();
        FaceTecSDKStatus status = FaceTecSDK.getStatus(activity);

        switch (status) {
            case INITIALIZED:
            case DEVICE_IN_LANDSCAPE_MODE:
            case DEVICE_IN_REVERSE_PORTRAIT_MODE:
                // status is already initialized - resolve promise with true
                FaceTecSDK.setDynamicStrings(Customization.UITextStrings);
                promise.resolve(true);
                break;
            case NEVER_INITIALIZED:
            case NETWORK_ISSUES:
                FaceVerification.register(serverURL, jwtAccessToken);

                // based on licenseText value, init in prod|dev mode
                if (!(licenseText == null || licenseText.isEmpty())) {
                    FaceTecSDK.initializeInProductionMode(activity, licenseText, licenseKey, encryptionKey, onInitializationAttempt(activity, promise));
                    return;
                }

                FaceTecSDK.initializeInDevelopmentMode(activity, licenseKey, encryptionKey, onInitializationAttempt(activity, promise));
                break;
            default:
                RCTPromise.rejectWith(promise, status);
        }
    }

    @ReactMethod
    public void faceVerification(final String enrollmentIdentifier,
                                 final int maxRetries, Promise promise
    ) {
        Activity activity = getCurrentActivity();
        final ProcessingSubscriber subscriber = new ProcessingSubscriber(promise);
        final EnrollmentProcessor processor = new EnrollmentProcessor(activity, subscriber);

        if (lastProcessor != null) {
            ProcessingSubscriber lastSubscriber = lastProcessor.getSubscriber();

            lastSubscriber.onSessionContextSwitch();
        }

        lastProcessor = processor;

        try {
            final String permission = "android.permission.CAMERA";
            PermissionAwareActivity permissionAwareActivity = getPermissionAwareActivity();
            boolean[] rationaleStatuses = new boolean[1];
            rationaleStatuses[0] = permissionAwareActivity.shouldShowRequestPermissionRationale(permission);

            mRequests.put(mRequestCode, new Request(
                    rationaleStatuses,
                    new Callback() {
                        @Override
                        public void invoke(Object... args) {
                            int[] results = (int[]) args[0];

                            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                                processor.enroll(enrollmentIdentifier, maxRetries);
                            } else {
                                PermissionAwareActivity activity = (PermissionAwareActivity) args[1];
                                boolean[] rationaleStatuses = (boolean[]) args[2];

                                if (rationaleStatuses[0] &&
                                        !activity.shouldShowRequestPermissionRationale(permission)) {
                                    subscriber.onCameraAccessError();
                                } else {
                                    subscriber.onCameraAccessError();
                                }
                            }
                        }
                    }
            ));

            permissionAwareActivity.requestPermissions(new String[] {permission}, mRequestCode, this);
            mRequestCode++;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            subscriber.onCameraAccessError();
        }
    }

    private FaceTecSDK.InitializeCallback onInitializationAttempt(final Activity activity, final Promise promise) {
        // unique callback for both prod|dev init
        return new FaceTecSDK.InitializeCallback() {
            @Override
            public void onCompletion(final boolean successful) {
                // the value of successful determines if the sdk has been initialized or not
                if(successful) {
                    // status is already initialized - resolve promise with true
                    FaceTecSDK.setDynamicStrings(Customization.UITextStrings);
                    promise.resolve(true);
                    return;
                }

                FaceTecSDKStatus sdkStatus = FaceTecSDK.getStatus(activity);
                String newLine = System.getProperty("line.separator");
                String customMessage = null;

                // if status still hasn't been initialized it means user is using an emulator
                if (sdkStatus == FaceTecSDKStatus.NEVER_INITIALIZED) {
                    customMessage = "Initialize wasn't attempted as Android Emulator has been detected." + newLine + "FaceTec SDK could be ran on the real devices only";
                }

                RCTPromise.rejectWith(promise, sdkStatus, customMessage);
            }
        };
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Request request = mRequests.get(requestCode);
        request.callback.invoke(grantResults, getPermissionAwareActivity(), request.rationaleStatuses);
        mRequests.remove(requestCode);
        return mRequests.size() == 0;
    }

    private PermissionAwareActivity getPermissionAwareActivity() {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            throw new IllegalStateException(
                    "Tried to use permissions API while not attached to an " + "Activity.");
        } else if (!(activity instanceof PermissionAwareActivity)) {
            throw new IllegalStateException(
                    "Tried to use permissions API but the host Activity doesn't"
                            + " implement PermissionAwareActivity.");
        }
        return (PermissionAwareActivity) activity;
    }

    private class Request {
        public boolean[] rationaleStatuses;
        public Callback callback;

        public Request(boolean[] rationaleStatuses, Callback callback) {
            this.rationaleStatuses = rationaleStatuses;
            this.callback = callback;
        }
    }
}
