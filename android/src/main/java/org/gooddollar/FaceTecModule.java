package org.gooddollar;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import com.facebook.react.modules.core.DeviceEventManagerModule;

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

public class FaceTecModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private EnrollmentProcessor lastProcessor = null;

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
        Promise promise
    ) {
        Activity activity = getCurrentActivity();

        //TODO: see Facetec.swift initializeSDK func or initialize method of FaceTecSDK.web on GoodDapp
        // pass activity as "context" param to the all FaceTect SDK calls

        // 1. get current status. if already initialized - resolve promise with true
        // 2. if licenseText is set call initializeInProductionMode, initializeInDevelopmentMode otherwise
        // 3. in FaceTecSDK.InitializeCallback check 'initialized' argument
        // 4. if initialized:
        //   a) call
        //   FaceVerification.register(serverURL, jwtAccessToken);
        //   FaceTecSDK.setDynamicStrings(Customization.UITextStrings);
        //   b) resolve with true
        // 5. if not initialized - get status, error code - status.ordinal() an error message - status.toString()
        // 6. if status is still never intitialized - it means you tring to initialize on emulator.
        // set corresponding error message (like in swift implementation)
        // 7. reject promise with (status, error mesage), use promise util
        // RCTPromise.rejectWith(promise, status, errorMessage);

        promise.resolve(FaceTecSDK.version());
    }

    @ReactMethod
    public void faceVerification(String enrollmentIdentifier,
        int maxRetries, Promise promise
    ) {
        Activity activity = getCurrentActivity();
        ProcessingSubscriber subscriber = new ProcessingSubscriber(promise);
        EnrollmentProcessor processor = new EnrollmentProcessor(activity, subscriber);

        if (lastProcessor != null) {
            ProcessingSubscriber lastSubscriber = lastProcessor.getSubscriber();

            lastSubscriber.onSessionContextSwitch();
        }

        lastProcessor = processor;
        processor.enroll(enrollmentIdentifier, maxRetries);
    }
}
