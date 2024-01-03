package org.gooddollar.facetec.api;


import android.util.Log;

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

import org.gooddollar.facetec.api.NetworkingHelpers;

public final class FaceVerification {
  public static final String unexpectedMessage = "An unexpected issue during the face verification API call";

  private FaceVerification() {}
  private final static OkHttpClient http = NetworkingHelpers.getApiClient();
  private static String _jwtAccessToken;
  private static String _serverURL;
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

  interface CallbackBase {
    void onFailure(APIException exception);
  }

  public interface APICallback extends CallbackBase {
    void onSuccess(JSONObject response);
  }

  public interface SessionTokenCallback extends CallbackBase {
    void onSessionTokenReceived(String sessionToken);
  }

  public static void register(String serverURL, String jwtAccessToken) {
    _serverURL = serverURL;
    _jwtAccessToken = jwtAccessToken;
  }

  public static RequestBody jsonStringify(JSONObject body) {
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString());
  }

  public static void getSessionToken(final SessionTokenCallback callback) {
    Request tokenRequest = createRequest("/session_token", "get", new JSONObject());

    sendRequest(tokenRequest, new APICallback() {
      @Override
      public void onSuccess(JSONObject response) {
        try {
          if (response.has(sessionTokenProperty) == false) {
            throw new APIException("FaceTec API response is empty", response);
          }

          callback.onSessionTokenReceived(response.getString(sessionTokenProperty));
        } catch (APIException exception) {
          callback.onFailure(exception);
        } catch (Exception exception) {
          callback.onFailure(new APIException(exception, response));
        }
      }

      @Override
      public void onFailure(APIException exception) {
        Log.w("getSessionToken", exception);
        callback.onFailure(exception);
      }
    });
  }

  public static void enroll(String enrollmentIdentifier, JSONObject payload, final APICallback callback) {
    enroll(enrollmentIdentifier, jsonStringify(payload), null, callback);
  }

  public static void enroll(String enrollmentIdentifier, RequestBody customRequest, final APICallback callback) {
    enroll(enrollmentIdentifier, customRequest, null, callback);
  }

  public static void enroll(String enrollmentIdentifier, JSONObject payload, @Nullable Integer timeout, final APICallback callback) {
    enroll(enrollmentIdentifier, jsonStringify(payload), timeout, callback);
  }

  public static void enroll(String enrollmentIdentifier, RequestBody customRequest, @Nullable Integer timeout, final APICallback callback) {
    Request enrollmentRequest = createRequest("", "post", customRequest);

    sendRequest(enrollmentRequest, timeout, callback);
  }

  private static Request createRequest(String url, @Nullable String method, @Nullable RequestBody body) {
    Request.Builder request = new Request.Builder()
      .url(_serverURL + url)
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + _jwtAccessToken);

    switch (method) {
      case "post":
        request.post(body);
        break;
      case "put":
        request.put(body);
        break;
    }

    return request.build();
  }

  private static Request createRequest(String url, String method, JSONObject body) {
    RequestBody requestBody = jsonStringify(body);

    return createRequest(url, method, requestBody);
  }

  private static void sendRequest(Request request, final APICallback requestCallback) {
    sendRequest(request, null, requestCallback);
  }

  private static void sendRequest(Request request, @Nullable Integer timeout, final APICallback requestCallback) {
    OkHttpClient httpClient = http;

    if (timeout != null) {
      httpClient = NetworkingHelpers.setTimeouts(http.newBuilder(), timeout, TimeUnit.MILLISECONDS).build();
    }

    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          String responseString = response.body().string();
          response.body().close();

          JSONObject responseJSON = new JSONObject(responseString);

          requestCallback.onSuccess(responseJSON);
        } catch (APIException exception) {
          requestCallback.onFailure(exception);
        } catch (Exception exception) {
          requestCallback.onFailure(new APIException(exception, null));
        }
      }

      @Override
      public void onFailure(Call call, IOException e) {
        requestCallback.onFailure(new APIException(e, null));
      }
    });
  }
}
