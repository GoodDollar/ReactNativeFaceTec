package org.gooddollar.api;

import java.io.IOException;
import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import org.gooddollar.api.NetworkingHelpers;

public final class FaceTecAPI {
  private FaceTecAPI() {}
  private final static OkHttpClient http = NetworkingHelpers.getApiClient();
  private static String _jwtAccessToken;
  private static String _serverURL;

  private static String unexpectedMessage = "An unexpected issue during the face verification API call";
  private static String succeedProperty = "success";
  private static String errorMessageProperty = "error";
  private static String sessionTokenProperty = "sessionToken";

  public static class Exception extends IOException {
    JSONObject response = null;

    Exception(String message, @Nullable JSONObject response) {
      super(message);

      this.response = response;
    }

    Exception(Throwable cause, @Nullable JSONObject response) {
      super(cause);

      this.response = response;
    }

    public JSONObject getResponse() {
      return response;
    }
  }

  interface CallbackBase {
    void onFailure(Exception exception);
  }

  public static interface Callback extends CallbackBase {
    void onSuccess(JSONObject response);
  }

  public static interface SessionTokenCallback extends CallbackBase {
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
    Request tokenRequest = createRequest("/verify/face/session", "post", new JSONObject());

    sendRequest(tokenRequest, new Callback() {
      @Override
      public void onSuccess(JSONObject response) {
        try {
          if (response.has(sessionTokenProperty) == false) {
            throw new Exception("FaceTec API response is empty", response);
          }

          callback.onSessionTokenReceived(response.getString(sessionTokenProperty));
        } catch (Exception exception) {
          callback.onFailure(exception);
        } catch (java.lang.Exception exception) {
          callback.onFailure(new Exception(exception, response));
        }
      }

      @Override
      public void onFailure(Exception exception) {
        callback.onFailure(exception);
      }
    });
  }

  public static void enroll(String enrollmentIdentifier, JSONObject payload, final Callback callback) {
    enroll(enrollmentIdentifier, jsonStringify(payload), callback);
  }

  public static void enroll(String enrollmentIdentifier, RequestBody customRequest, final Callback callback) {
    Request enrollmentRequest = createRequest("/verify/face/" + enrollmentIdentifier, "put", customRequest);

    sendRequest(enrollmentRequest, callback);
  }

  private static Request createRequest(String url, @Nullable String method, @Nullable RequestBody body) {
    Request.Builder request = new Request.Builder()
      .url(_serverURL + url)
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer: " + _jwtAccessToken);

    switch (method) {
      case "post":
        request.post(body);
        break;
      case "put":
        request.put(body);
        break;
      case "delete":
        request.delete();
        break;
      case "get":
      default:
        request.get();
        break;
    }

    return request.build();
  }

  private static Request createRequest(String url, String method, JSONObject body) {
    RequestBody requestBody = jsonStringify(body);

    return createRequest(url, method, requestBody);
  }

  private static void sendRequest(Request request, final Callback requestCallback) {
    http.newCall(request).enqueue(new okhttp3.Callback() {
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          String responseString = response.body().string();
          response.body().close();

          JSONObject responseJSON = new JSONObject(responseString);

          if (responseJSON.has(succeedProperty) == false) {
            throw new Exception(unexpectedMessage, responseJSON);
          }

          String errorMessage = null;
          boolean didSucceed = responseJSON.getBoolean(succeedProperty);

          if (didSucceed == true) {
            requestCallback.onSuccess(responseJSON);
            return;
          }

          if (responseJSON.has(errorMessageProperty) == true) {
            errorMessage = responseJSON.getString(errorMessageProperty);
          }

          if (errorMessage == null) {
            errorMessage = unexpectedMessage;
          }

          throw new Exception(errorMessage, responseJSON);
        } catch (Exception exception) {
          requestCallback.onFailure(exception);
        } catch (java.lang.Exception exception) {
          requestCallback.onFailure(new Exception(exception, null));
        }
      }

      @Override
      public void onFailure(Call call, IOException e) {
        requestCallback.onFailure(new Exception(e, null));
      }
    });
  }
}
