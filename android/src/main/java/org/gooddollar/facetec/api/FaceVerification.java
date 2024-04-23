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

import org.gooddollar.facetec.api.NetworkingHelpers;

// API client
public final class FaceVerification {
  public static final String unexpectedMessage = "An unexpected issue during the face verification API call";

  private FaceVerification() {}
  // get & store global http client instance
  private final static OkHttpClient http = NetworkingHelpers.getApiClient();
  private static String _jwtAccessToken;
  private static String _serverURL;

  private static String succeedProperty = "success";
  private static String errorMessageProperty = "error";
  private static String sessionTokenProperty = "sessionToken";

  // API exception class encapsulating JSON response object
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

  // Basic API callback object having 
  //   - onSuccess() (receiving json response)
  //   - onFailure() (receiving APIException wrapping exception and error response)
  public interface APICallback extends CallbackBase {
    void onSuccess(JSONObject response);
  }

  // Session token callback object
  //   - the same onFailure()
  //   - onSessionTokenReceived() receiving token as string onstead of onSuccess()
  public interface SessionTokenCallback extends CallbackBase {
    void onSessionTokenReceived(String sessionToken);
  }

  // configures client with GoodServer url and JWT
  public static void register(String serverURL, String jwtAccessToken) {
    _serverURL = serverURL;
    _jwtAccessToken = jwtAccessToken;
  }

  // JSON.stringify-like helper to send requests with body
  public static RequestBody jsonStringify(JSONObject body) {
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString());
  }

  // opens FV session and returns sessionToken
  public static void getSessionToken(final SessionTokenCallback callback) {
    // build request POST <goodserver>/verify/face/session with empty body 
    Request tokenRequest = createRequest("/verify/face/session", "post", new JSONObject());

    // send it
    sendRequest(tokenRequest, new APICallback() {
      @Override
      public void onSuccess(JSONObject response) {
        try {
          if (response.has(sessionTokenProperty) == false) {
            // throw if no token prop in response
            throw new APIException("FaceTec API response is empty", response);
          }

          // otherwise read token and call onSessionTokenReceived callback
          callback.onSessionTokenReceived(response.getString(sessionTokenProperty));
        } catch (APIException exception) { // if got APIException - just call onFailure
          callback.onFailure(exception);
        } catch (Exception exception) { // otherwise wrap any Java exception with APIException and response (if any)
          callback.onFailure(new APIException(exception, response));
        }
      }

      @Override
      public void onFailure(APIException exception) {
        callback.onFailure(exception);
      }
    });
  }

  // different enroll() overloads to support send plain object or RequestBody instance and covering optional params
  public static void enroll(String enrollmentIdentifier, JSONObject payload, final APICallback callback) {
    enroll(enrollmentIdentifier, jsonStringify(payload), null, callback);
  }

  public static void enroll(String enrollmentIdentifier, RequestBody customRequest, final APICallback callback) {
    enroll(enrollmentIdentifier, customRequest, null, callback);
  }

  public static void enroll(String enrollmentIdentifier, JSONObject payload, @Nullable Integer timeout, final APICallback callback) {
    enroll(enrollmentIdentifier, jsonStringify(payload), timeout, callback);
  }

  // full enroll() implementation
  public static void enroll(String enrollmentIdentifier, RequestBody customRequest, @Nullable Integer timeout, final APICallback callback) {
    // build request PUT <goodserver>/verify/face/<enrollment id> { faceScan, auditTrailImage, lowQualityAuditTrailImage, sessionId, fvSigner }
    Request enrollmentRequest = createRequest("/verify/face/" + enrollmentIdentifier, "put", customRequest);

    // send it, pass callback object with onSuccess/onFailure
    sendRequest(enrollmentRequest, timeout, callback);
  }

  // request factory helper
  private static Request createRequest(String url, @Nullable String method, @Nullable RequestBody body) {
    Request.Builder request = new Request.Builder()
      .url(_serverURL + url) // combine url, set headers
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + _jwtAccessToken);

    // set method
    switch (method) {
      case "post":
        request.post(body);
        break;
      case "put":
        request.put(body);
        break;
    }

    // build & return
    return request.build();
  }

  // send request overloads for different methods & body types
  private static Request createRequest(String url, String method, JSONObject body) {
    RequestBody requestBody = jsonStringify(body);

    return createRequest(url, method, requestBody);
  }

  private static void sendRequest(Request request, final APICallback requestCallback) {
    sendRequest(request, null, requestCallback);
  }

  // full send request implenetations
  private static void sendRequest(Request request, @Nullable Integer timeout, final APICallback requestCallback) {
    // use global http client instance by default
    OkHttpClient httpClient = http;

    if (timeout != null) { // if timeout been set - create new client instance configured with timeout
      httpClient = NetworkingHelpers.setTimeouts(http.newBuilder(), timeout, TimeUnit.MILLISECONDS).build();
    }

    // send request
    httpClient.newCall(request).enqueue(new Callback() {
      // response/error handling the same as in the web (as we interact with the same GoodServer)
      // the same props are read & checked, the same cases processed
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          String responseString = response.body().string();
          response.body().close();

          JSONObject responseJSON = new JSONObject(responseString);

          // if (!('success' in response.data))
          if (responseJSON.has(succeedProperty) == false) {
            throw new APIException(unexpectedMessage, responseJSON);
          }

          String errorMessage = null;
          boolean didSucceed = responseJSON.getBoolean(succeedProperty);

          // if (response.data.success)
          if (didSucceed == true) {
            requestCallback.onSuccess(responseJSON);
            return;
          }

          // if (response.data.error)
          if (responseJSON.has(errorMessageProperty) == true) {
            errorMessage = responseJSON.getString(errorMessageProperty);
          }

          if (errorMessage == null) {
            errorMessage = unexpectedMessage;
          }

          throw new APIException(errorMessage, responseJSON);
        } catch (APIException exception) {
          requestCallback.onFailure(exception); // do not re-wrap if APIException thrown
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
