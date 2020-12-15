package org.gooddollar.util;

import com.facetec.sdk.R;
import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecOverlayCustomization;

import java.util.Map;
import java.util.HashMap;

public class Customization {
  private Customization() {}

  final public static String resultSuccessMessage = "You’re a beautiful\n& unique unicorn!";
  final public static String resultFacescanUploadMessage = "Uploading Your face snapshot to verify";
  final public static String resultFacescanProcessingMessage = "Verifying you're\none of a kind";

  final public static FaceTecCustomization UICustomization = new FaceTecCustomization();
  final public static Map<Integer, String> UITextStrings = new HashMap<>();

  static {
    // customize UI
    FaceTecOverlayCustomization overlay = UICustomization.getOverlayCustomization();

    overlay.showBrandingImage = false;
    // TODO: other customizations

    // override locale strings
    UITextStrings.put(R.string.FaceTec_result_facescan_upload_message, resultFacescanUploadMessage);
    UITextStrings.put(R.string.FaceTec_result_success_message, resultSuccessMessage);
    // TODO: other locale strings. see strings.xml from android sdk archive for R.string.xxx keys names
    // set 'initializing camera' messages to an once-space (" ") string to disable them (null will ne ignored)
  }
}
