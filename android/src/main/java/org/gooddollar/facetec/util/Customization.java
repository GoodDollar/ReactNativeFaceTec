package org.gooddollar.facetec.util;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.Nullable;

import com.facetec.sdk.R;
import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecOverlayCustomization;
import com.facetec.sdk.FaceTecCancelButtonCustomization;
import com.facetec.sdk.FaceTecFeedbackCustomization;
import com.facetec.sdk.FaceTecOvalCustomization;
import com.facetec.sdk.FaceTecFrameCustomization;
import com.facetec.sdk.FaceTecGuidanceCustomization;
import com.facetec.sdk.FaceTecResultScreenCustomization;
import com.facetec.sdk.FaceTecVocalGuidanceCustomization;

import java.util.Map;
import java.util.HashMap;

import org.gooddollar.facetec.R.drawable;

public class Customization {
  private Customization() {}

  final public static String resultSuccessMessage = "You’re a beautiful\n& unique unicorn!";
  final public static String resultFacescanUploadMessage = "Uploading Your face snapshot to verify";
  final public static String resultFacescanProcessingMessage = "Verifying you're\none of a kind";

  final public static FaceTecCustomization UICustomization = basicCustomizationFactory();
  final public static FaceTecCustomization LowLightModeCustomization = basicCustomizationFactory();
  final public static FaceTecCustomization DynamicModeCustomization = basicCustomizationFactory();
  final public static Map<Integer, String> UITextStrings = new HashMap<>();

  final private static int black = Color.BLACK;
  final private static int white = Color.WHITE;
  final private static int whiteTransparent = 0x00ffffff;
  final private static int whiteSemiTransparent = 0x80ffffff;
  final private static int green = Color.GREEN;
  final private static int darkGray = Color.DKGRAY;
  final private static int lightGray = Color.LTGRAY;

  final private static int primary = 0xff00afff;
  final private static int gray50Percent = 0xffcbcbcb;

  final private static int defaultCornerRadius = 5;
  final private static int buttonCornerRadius = 12;

  // "sans-serif" family resolves to the Roboto font
  final private static Typeface defaultFont = Typeface.create("sans-serif", Typeface.NORMAL);
  final private static Typeface mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL);
  final private static Typeface boldFont = Typeface.create("sans-serif", Typeface.BOLD);

  static {
    // customize Dynamic dimming & low light mode UI
    FaceTecGuidanceCustomization lowLightGuidance = LowLightModeCustomization.getGuidanceCustomization();
    FaceTecGuidanceCustomization dynamicDimmingGuidance = DynamicModeCustomization.getGuidanceCustomization();
    FaceTecCancelButtonCustomization dynamicDimmingButton = DynamicModeCustomization.getCancelButtonCustomization();

    // Dynamic dimming customizations
    dynamicDimmingButton.customImage = drawable.facetec_cancel_white;
    dynamicDimmingGuidance.readyScreenHeaderTextColor = black;
    dynamicDimmingGuidance.readyScreenSubtextTextColor = black;

    // Low light customizations
    lowLightGuidance.readyScreenHeaderTextColor = white;
    lowLightGuidance.readyScreenSubtextTextColor = white;

    // override locale strings
    UITextStrings.put(R.string.FaceTec_result_success_message, resultSuccessMessage);
    UITextStrings.put(R.string.FaceTec_result_facescan_upload_message, resultFacescanUploadMessage);

    UITextStrings.put(R.string.FaceTec_retry_instruction_message_1, "Hold Your Camera at Eye Level.");
    UITextStrings.put(R.string.FaceTec_retry_instruction_message_2, "Light Your Face Evenly.");
    UITextStrings.put(R.string.FaceTec_retry_instruction_message_3, "Avoid Smiling & Back Light");

    UITextStrings.put(R.string.FaceTec_instructions_message_ready_1, "Please Frame Your Face In The Small");
    UITextStrings.put(R.string.FaceTec_instructions_message_ready_2, "Oval, Then The Big Oval");
  }

  final private static FaceTecCustomization basicCustomizationFactory() {
    FaceTecCustomization customization = new FaceTecCustomization();

    // customize UI
    FaceTecOverlayCustomization overlay = customization.getOverlayCustomization();
    FaceTecCancelButtonCustomization cancelButton = customization.getCancelButtonCustomization();
    FaceTecFeedbackCustomization feedback = customization.getFeedbackCustomization();
    FaceTecOvalCustomization oval = customization.getOvalCustomization();
    FaceTecFrameCustomization frame = customization.getFrameCustomization();
    FaceTecGuidanceCustomization guidance = customization.getGuidanceCustomization();
    FaceTecVocalGuidanceCustomization vocalGuidance = customization.vocalGuidanceCustomization;
    FaceTecResultScreenCustomization resultScreen = customization.getResultScreenCustomization();

    // removing branding image from overlay
    overlay.showBrandingImage = false;
    overlay.backgroundColor = whiteSemiTransparent;

    // setting custom location & image of cancel button
    cancelButton.setLocation(FaceTecCancelButtonCustomization.ButtonLocation.TOP_RIGHT);
    cancelButton.customImage = drawable.facetec_cancel;

    // configuring feedback bar typography & border radius
    feedback.backgroundColors = primary;
    feedback.cornerRadius = defaultCornerRadius;
    feedback.textColor = white;
    feedback.textFont = boldFont;
    feedback.elevation = 5;

    // setting oval border color & width
    oval.strokeWidth = 6;
    oval.strokeColor = primary;
    oval.progressColor1 = green;
    oval.progressColor2 = green;

    // frame (zoom's popup) customizations
    // setting frame border, radius & elevation
    frame.borderColor = whiteTransparent;
    frame.cornerRadius = defaultCornerRadius;
    frame.borderWidth = 0;
    frame.elevation = 19;

    // setting Zoom UI background color
    frame.backgroundColor = white;

    // guidance screens ("frame your face", "retry" etc) customizations
    // setting setting Zoom UI default text color
    guidance.foregroundColor = darkGray;

    // customizing buttons
    guidance.buttonFont = defaultFont;
    guidance.buttonBorderWidth = 0;
    guidance.buttonCornerRadius = buttonCornerRadius;
    guidance.buttonTextNormalColor = white;
    guidance.buttonTextHighlightColor = white;
    guidance.buttonTextDisabledColor = white;
    guidance.buttonBackgroundNormalColor = primary;
    guidance.buttonBackgroundHighlightColor = primary;
    guidance.buttonBackgroundDisabledColor = primary;

    // customizing header / subtext
    guidance.headerFont = mediumFont;

    // subtext
    guidance.subtextFont = defaultFont;

    // configuring guidance images on retry screen
    guidance.retryScreenOvalStrokeColor = primary;
    guidance.retryScreenImageBorderColor = primary;
    guidance.retryScreenImageBorderWidth = 4;
    guidance.retryScreenImageCornerRadius = defaultCornerRadius;

    // customizing result screen - progress bar & success animation
    resultScreen.foregroundColor = darkGray;
    resultScreen.messageFont = defaultFont;
    resultScreen.showUploadProgressBar = true;
    resultScreen.uploadProgressFillColor = primary;
    resultScreen.uploadProgressTrackColor = lightGray;
    resultScreen.resultAnimationBackgroundColor = white;
    resultScreen.resultAnimationForegroundColor = primary;
    resultScreen.customActivityIndicatorImage = drawable.facetec_activity_indicator;
    resultScreen.customActivityIndicatorRotationInterval = 3000;

    // disable voice help
    vocalGuidance.mode = FaceTecVocalGuidanceCustomization.VocalGuidanceMode.NO_VOCAL_GUIDANCE;

    return customization;
  }
}
