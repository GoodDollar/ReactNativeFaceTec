package org.gooddollar;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.Map;
import java.util.HashMap;

public class FaceTecModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public FaceTecModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "FaceTec";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        final Map<String, String> FaceTecUxEvent = new HashMap<>();
        final Map<String, Integer> FaceTecSDKStatus = new HashMap<>();
        final Map<String, Integer> FaceTecSessionStatus = new HashMap<>();

        constants.put("FaceTecUxEvent", FaceTecUxEvent);
        constants.put("FaceTecSDKStatus", FaceTecSDKStatus);
        constants.put("FaceTecSessionStatus", FaceTecSessionStatus);
        return constants;
    }

    @ReactMethod
    public void initialize(String serverURL, String jwtAccessToken,
        String licenseKey, String encryptionKey, String licenseText,
        Promise promise
    ) {
        promise.resolve(null);
    }

    @ReactMethod
    public void faceVerification(String enrollmentIdentifier,
        int maxRetries, Promise promise
    ) {
        promise.resolve(null);
    }
}
