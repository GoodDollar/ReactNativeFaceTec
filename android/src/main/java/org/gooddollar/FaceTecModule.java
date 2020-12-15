package org.gooddollar;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;
import java.util.HashMap;

import org.gooddollar.processors.EnrollmentProcessor;

import org.gooddollar.util.UXEvent;
import org.gooddollar.util.EventEmitter;
import org.gooddollar.util.Customization;

import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecSessionStatus;
import com.facetec.sdk.FaceTecSDKStatus;

public class FaceTecModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public FaceTecModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
    }

    @Override
    public void initialize() {
        super.initialize();

        EventEmitter.register(reactContext);
        FaceTecSDK.setCustomization(Customization.UICustomization);
    }

    @Override
    public String getName() {
        return "FaceTec";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        final Map<String, Integer> faceTecSDKStatus = new HashMap<>();
        final Map<String, Integer> faceTecSessionStatus = new HashMap<>();

        // SDK statuses
        // common statuses (status names are aligned with the web sdk)
        faceTecSDKStatus.put("NeverInitialized",  FaceTecSDKStatus.NEVER_INITIALIZED.ordinal());
        faceTecSDKStatus.put("Initialized",  FaceTecSDKStatus.INITIALIZED.ordinal());
        // TODO:
        // 1. check all the statuses names from the web SDK
        // 2. find corresponding statuses from FaceTecSDKStatus (they could have a bit different names)
        // 3. list all them here
        // native-specific statuses
        // 4. list statuses aren't matched with the native ones here, transforming their names to the PascalCase

        // 5.you could use FaceTec.swift (where i did that for IOS SDK v8) as an example. but don't use it as is
        // because statuses are changed a bit from v8 to v9


        // Session statuses
        // 6. the same for session statuses
        // common statuses (status names are aligned with the web sdk)
        faceTecSessionStatus.put("SessionCompletedSuccessfully",  FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY.ordinal());
        // native-specific statuses

        // 7. finally, update kindOfTheIssue.js in that PR https://github.com/GoodDollar/GoodDAPP/pull/2785
        // by removing some old statuses and adding new ones. i did that for web only statuses
        // byt there're also some native statuses we should also process some specific way
        // (e.g. no camera access, wrong orientation, license issue, cancelled etc)
        // for example in v8 we had deviceInReversePortraitMode native-only status
        // which i've included to kindOfTheIssue to process it as the wrong orientation too

        // aggregating all constants in a single object literal exported to JS
        constants.put("FaceTecUxEvent", UXEvent.toMap());
        constants.put("FaceTecSDKStatus", faceTecSDKStatus);
        constants.put("FaceTecSessionStatus", faceTecSessionStatus);
        return constants;
    }

    @ReactMethod
    public void initializeSDK(String serverURL, String jwtAccessToken,
        String licenseKey, String encryptionKey, String licenseText,
        Promise promise
    ) {
        FaceTecSDK.setDynamicStrings(Customization.UITextStrings);
        promise.resolve(FaceTecSDK.version());
    }

    @ReactMethod
    public void faceVerification(String enrollmentIdentifier,
        int maxRetries, Promise promise
    ) {
        new EnrollmentProcessor();
        promise.resolve(null);
    }
}
