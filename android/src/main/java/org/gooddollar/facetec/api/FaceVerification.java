package org.gooddollar.facetec.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import org.gooddollar.facetec.api.NetworkingHelpers;

public final class FaceVerification {
  public static final String unexpectedMessage = "An unexpected issue during the face verification API call";

  private FaceVerification() {}
  private final static OkHttpClient http = NetworkingHelpers.getApiClient();
  private static String _jwtAccessToken;
  private static String _serverURL;

  private static String succeedProperty = "success";
  private static String errorMessageProperty = "error";
  private static String sessionTokenProperty = "session_token";

  public static class APIException extends IOException {
    JSONObject response = null;

    public APIException(String message, @Nullable JSONObject response) {
      super(message);

      this.response = response;
    }

    APIException(Throwable cause, @Nullable JSONObject response) {
      super(cause);

      this.response = response;
    }

    public JSONObject getResponse() {
      return response;
    }
  }

  public static void register(String serverURL, String jwtAccessToken) {
    _serverURL = serverURL;
    _jwtAccessToken = jwtAccessToken;
  }

  public static RequestBody jsonStringify(JSONObject body) {
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString());
  }
}
