package org.gooddollar.facetec.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.modules.core.PermissionAwareActivity;

// Utility to request camera permissions
// uses common approach, nothing FV-specific here
public class Permissions implements PermissionListener {
  private Context context;

  private final SparseArray<PermissionsRequest> mRequests = new SparseArray<PermissionsRequest>();
  private int mRequestCode = 0;

  private class PermissionsRequest {
    public boolean[] rationaleStatuses;
    public Callback callback;

    public PermissionsRequest(boolean[] rationaleStatuses, Callback callback) {
      this.rationaleStatuses = rationaleStatuses;
      this.callback = callback;
    }
  }

  public interface PermissionsCallback {
    void onSuccess();
    void onFailure();
  }

  public Permissions(Context context) {
    this.context = context;
  }

  // receives callback class instance should have onSuccess() and onFailure() methods defined
  public void requestCameraPermissions(final PermissionsCallback callback) {
    try {
      final String permission = "android.permission.CAMERA";
      PermissionAwareActivity permissionAwareActivity = getPermissionAwareActivity();

      boolean[] rationaleStatuses = new boolean[] {
        permissionAwareActivity.shouldShowRequestPermissionRationale(permission)
      };

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

            callback.onFailure();
          }
        }
      ));

      permissionAwareActivity.requestPermissions(new String[]{permission}, mRequestCode, this);
      mRequestCode++;
    } catch (Exception e) {
      callback.onFailure();
    }
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    PermissionsRequest request = mRequests.get(requestCode);
    PermissionAwareActivity permissionAwareActivity = getPermissionAwareActivity();

    request.callback.invoke(grantResults, permissionAwareActivity, request.rationaleStatuses);
    mRequests.remove(requestCode);

    return mRequests.size() == 0;
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
