#import <UIKit/UIKit.h>

/** Represents the resolution options for the returned ZoOm Audit Trail Image(s) */
typedef NS_ENUM(NSInteger, ZoomAuditTrailType) {
    /** Configures ZoOm to disable returning Audit Trail Images. */
    ZoomAuditTrailTypeDisabled = 0,
    /** Configures ZoOm to return the fullest resolution image possible. */
    ZoomAuditTrailTypeFullResolution = 1,
    /** Configures Zoom to return images of height 640. */
    ZoomAuditTrailTypeHeight640 = 2,
};

/** Represents the options for the blur effect styles for the ZoOm Frame (area outside of ZoOm Oval). */
typedef NS_ENUM(NSInteger, ZoomBlurEffectStyle) {
    /** The blur effect will be off/disabled. */
    ZoomBlurEffectOff = 0,
    /** The blur effect will be default style (ONLY AVAILABLE ON IOS 10+) */
    ZoomBlurEffectStyleRegular = 1,
    /** The blur effect will have a light/white-tint style */
    ZoomBlurEffectStyleLight = 2,
    /** The blur effect will have a extra light/white-tint style */
    ZoomBlurEffectStyleExtraLight = 3,
    /** The blur effect will have a dark/black-tint style */
    ZoomBlurEffectStyleDark = 4,
    /** The blur effect will have a prominent style (ONLY AVAILABLE ON IOS 10+) */
    ZoomBlurEffectStyleProminent = 5,
};

/** Represents the options for placement of the ZoOm Cancel Button. */
typedef NS_ENUM(NSInteger, ZoomCancelButtonLocation) {
    /** ZoOm Cancel Button will appear in the top left. */
    ZoomCancelButtonLocationTopLeft = 0,
    /** ZoOm Cancel Button will appear in the top right. */
    ZoomCancelButtonLocationTopRight = 1,
    /** ZoOm Cancel Button will be disabled and hidden. */
    ZoomCancelButtonLocationDisabled = 2,
    /** ZoOm Cancel Button will be appear at the location and size specified by cancelButtonCustomization.customLocation. */
    ZoomCancelButtonLocationCustom = 3,
};

/** Represents the options for the behavior of iPhone X's view when frame size ratio is set to 1. */
typedef NS_ENUM(NSInteger, ZoomFullScreenBehavior) {
    /** ZoOm will handle the look of the view */
    ZoomFullScreenBehaviorAutomatic = 0,
    /** Developer is in full control of the look of ZoOm */
    ZoomFullScreenBehaviorManual = 1,
};

/** Represents the options for the transition animation used when dismissing the ZoOm Interface. */
typedef NS_ENUM(NSInteger, ZoomExitAnimationStyle) {
    /** Default. Quick fade out. */
    ZoomExitAnimationStyleNone = 0,
    /** Frame will fade out as oval and frame expand out quickly. */
    ZoomExitAnimationStyleRippleOut = 1,
    /** Frame will slowly fade out as oval and frame slowly expand out.*/
    ZoomExitAnimationStyleRippleOutSlow = 2,
};

@protocol ZoomSDKProtocol;

__attribute__((visibility("default")))
@interface Zoom: NSObject
@property (nonatomic, class, readonly, strong) id <ZoomSDKProtocol> _Nonnull sdk;
@end

@protocol ZoomFaceBiometricMetrics;
@protocol ZoomIDScanMetrics;
@class NSDate;

/** Represents the possible state of camera permissions. */
typedef NS_ENUM(NSInteger, ZoomCameraPermissionStatus) {
    /** The user has not yet been asked for permission to use the camera */
     ZoomCameraPermissionStatusNotDetermined = 0,
    /** The user denied the app permission to use the camera or manually revoked the app’s camera permission.
     From this state, permission can only be modified by the user from System ‘Settings’ context. */
    ZoomCameraPermissionStatusDenied = 1,
    /** The camera permission on this device has been disabled due to policy.
     From this state, permission can only be modified by the user from System ‘Settings’ context or contacting the system administrator. */
    ZoomCameraPermissionStatusRestricted = 2,
    /** The user granted permission to use the camera. */
    ZoomCameraPermissionStatusAuthorized = 3,
};

@class UIColor;
@class CAGradientLayer;
@class ZoomGuidanceCustomization;
@class ZoomGuidanceImagesCustomization;
@class ZoomOvalCustomization;
@class ZoomFeedbackCustomization;
@class ZoomCancelButtonCustomization;
@class ZoomFrameCustomization;
@class ZoomResultScreenCustomization;
@class ZoomOverlayCustomization;
@class ZoomIDScanCustomization;
@class ZoomSessionTimerCustomization;

/**
 * Class used to customize the look and feel of the ZoOm Interface.
 * ZoOm ships with a default ZoOm theme but has a variety of variables that you can use to configure ZoOm to your application's needs.
 * To customize the ZoOm Interface, simply create an instance of ZoomCustomization and set some, or all, of the variables.
 */
__attribute__((visibility("default")))
@interface ZoomCustomization : NSObject
/** Customize the timers used during the ZoOm Session and Identity Check Screens. */
@property (nonatomic, strong) ZoomSessionTimerCustomization * _Nonnull sessionTimerCustomization;
/** Customize the ZoOm Identity Check Screens. */
@property (nonatomic, strong) ZoomIDScanCustomization * _Nonnull idScanCustomization;
/** Customize the ZoOm Overlay, separating the ZoOm Interface from the presenting application context. */
@property (nonatomic, strong) ZoomOverlayCustomization * _Nonnull overlayCustomization;
/** Customize the New User Guidance and Retry Screens. */
@property (nonatomic, strong) ZoomGuidanceCustomization * _Nonnull guidanceCustomization;
/** Customize the Result Screen. */
@property (nonatomic, strong) ZoomResultScreenCustomization * _Nonnull resultScreenCustomization;
/** Customize the ZoOm Oval and the ZoOm Progress Spinner animations. */
@property (nonatomic, strong) ZoomOvalCustomization * _Nonnull ovalCustomization;
/** Customize the ZoOm Feedback Bar. */
@property (nonatomic, strong) ZoomFeedbackCustomization * _Nonnull feedbackCustomization;
/** Customize the ZoOm Cancel Button. */
@property (nonatomic, strong) ZoomCancelButtonCustomization * _Nonnull cancelButtonCustomization;
/** Customize the ZoOm Frame. */
@property (nonatomic, strong) ZoomFrameCustomization * _Nonnull frameCustomization;

/**
 * Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.
 */
@property (nonatomic) float mainInterfaceEntryTransitionTime DEPRECATED_MSG_ATTRIBUTE("Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.");

/**
 * Customize the transition out animation for an unsuccessful ZoOm Session.
 * Default is ZoomExitAnimationStyleNone.
 */
@property (nonatomic) enum ZoomExitAnimationStyle exitAnimationUnsuccess;
/**
 * Customize the transition out animation for a successful ZoOm Session.
 * Default is ZoomExitAnimationStyleNone.
 */
@property (nonatomic) enum ZoomExitAnimationStyle exitAnimationSuccess;

/**
 * This function allows special runtime control of the success message shown when the success animation occurs.
 * Please note that you can also customize this string via the standard customization/localization methods provided by ZoOm.
 * Special runtime access is enabled to this text because the developer may wish to change this text depending on ZoOm's mode of operation.
 * Default is "Success"
 */
+ (NSString * _Nullable) overrideResultScreenSuccessMessage;
+ (void) setOverrideResultScreenSuccessMessage:(NSString * _Nonnull)value;

@property (nonatomic) NSDictionary* _Nullable featureFlagsMap;

- (nonnull instancetype)init;
- (nonnull instancetype)initWithFeatureFlagsMap:(NSDictionary* _Nullable)featureFlagsMap  NS_SWIFT_NAME(init(featureFlagsMap:));
+ (nonnull instancetype)new;
@end


/** Represents results of a Zoom face biometric comparison */
@protocol ZoomFaceBiometricMetrics <NSObject>
/** Returns the Audit Trail Images as an array of base64 encoded JPG images. This should be considered the new default method of getting the Audit Trail Images.
 *  There are multiple advantages of using this new function. auditTrailCompressedBase64 provides a consistent API across all supported ZoOm platforms to
 *  get a compressed set of images that will not overly load user bandwidth and also provides images that are usable in the Audit Trail Verification API.
 */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable auditTrailCompressedBase64;
/** Returns the Audit Trail Images.*/
@property (nonatomic, readonly, copy) NSArray<UIImage *> * _Nullable auditTrail DEPRECATED_MSG_ATTRIBUTE("This functionality is being deprecated and will not exist in a future release of ZoOm. Developers should instead use the auditTrailCompressedBase64 function. There are multiple advantages of using this new function. auditTrailCompressedBase64 provides a consistent API across all supported ZoOm platforms to get a compressed set of images that will not overly load user bandwidth and also provides images that are usable in the Audit Trail Verification API.");
/** The Low Quality Audit Trail is a collection of images from the session that are likely partly responsible for the session not succeeding.
 *  The Low Quality Audit Trail finds images that can be displayed to the user that will be intuitively indicative of the reason for the session not completing successfully.
 */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable lowQualityAuditTrailCompressedBase64;
/** A collection of the audit trails captured during the face analysis.  This parameter is nil unless Zoom.sdk.auditTrailType is set to something other than Disabled. */
@property (nonatomic, readonly, copy) NSArray<NSArray<UIImage *>*> * _Nullable auditTrailHistory DEPRECATED_MSG_ATTRIBUTE("The ZoomFaceMapProcessor gives developers access to Audit Trail Images for each session and thus this function is not needed anymore and will be removed in an upcoming version of the ZoOm SDK.");
/** ZoOm Biometric FaceMap. */
@property (nonatomic, readonly, copy) NSData * _Nullable faceMap;
/** ZoOm Biometric FaceMapBase64. */
@property (nonatomic, readonly, copy) NSString * _Nullable faceMapBase64;
/** ZoOm Biometric FaceMap. */
@property (nonatomic, readonly, copy) NSData * _Nullable zoomFacemap DEPRECATED_MSG_ATTRIBUTE("Use 'faceMap'");
@end // ZoomFaceBiometricMetrics

/** Represents results of Zoom ID Scan */
@protocol ZoomIDScanMetrics <NSObject>
/** High resolution samples of the front images of the Photo ID that can be used for Auditing and Identity Verification. */
@property (nonatomic, readonly, copy) NSArray<UIImage *> * _Nullable frontImages;
/** High resolution samples of the front images as Base64 of the Photo ID that can be used for Auditing and Identity Verification. */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable frontImagesBase64 DEPRECATED_MSG_ATTRIBUTE("This API will be removed in an upcoming version of the ZoOm SDK. Please use frontImagesCompressedBase64 instead.");
/** High resolution samples of the front images as compressed Base64 of the Photo ID that can be used for Auditing and Identity Verification. */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable frontImagesCompressedBase64;
/** High resolution samples of the back images of the Photo ID that can be used for Auditing and Identity Verification. When ID Type is passport, this object will return as any empty array. */
@property (nonatomic, readonly, copy) NSArray<UIImage *> * _Nullable backImages;
/** High resolution samples of the back images as Base64 of the Photo ID that can be used for Auditing and Identity Verification. */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable backImagesBase64 DEPRECATED_MSG_ATTRIBUTE("This API will be removed in an upcoming version of the ZoOm SDK. Please use backImagesCompressedBase64 instead.");
/** High resolution samples of the back images as compressed Base64 of the Photo ID that can be used for Auditing and Identity Verification. */
@property (nonatomic, readonly, copy) NSArray<NSString *> * _Nullable backImagesCompressedBase64;
/** ZoOm ID Scan Data. */
@property (nonatomic, readonly, copy) NSData * _Nullable idScan;
/** ZoOm ID Scan Data as base64 encoded string. */
@property (nonatomic, readonly, copy) NSString * _Nullable idScanBase64;
/** A unique ID for the ZoOm ID Scan. */
@property (nonatomic, readonly, copy) NSString * _Nullable sessionId;
@end // ZoomIDScanMetrics

/**
 * Customize the shadow that can be applied to the ZoOm Frame and Feedback Bar.
 * Note: These properties and their behavior correlate to the shadow-related attributes available for CALayer objects.
 */
__attribute__((visibility("default")))
@interface ZoomShadow : NSObject

/**
 * Control the shadow's color.
 * Default is black.
 */
@property (nonatomic) UIColor * _Nonnull color;
/**
 * Control the shadow's opacity.
 * Default is 0.
 */
@property (nonatomic) float opacity;
/**
 * Control the shadow's radius.
 * Default is 0.
 */
@property (nonatomic) float radius;
/**
 * Control the shadow's offset.
 * Default is CGSizeZero.
 */
@property (nonatomic) CGSize offset;
/**
 * Control the insets from the parent's view frame for configuring the shadow's path.
 * Default is UIEdgeInsetsZero.
 */
@property (nonatomic) UIEdgeInsets insets;

- (nonnull instancetype) init;
- (nonnull instancetype) initWithColor:(UIColor * _Nonnull)color opacity:(float)opacity radius:(float)radius offset:(CGSize)offset insets:(UIEdgeInsets)insets;
@end

/**
 * Customize the timers used during the ZoOm Session and Identity Check Screens.
 */
__attribute__((visibility("default")))
@interface ZoomSessionTimerCustomization : NSObject
/**
 * The amount of seconds until the Liveness Check portion of the Session will be cancelled if there is no user interaction.
 * If a user presses a button or performs a Liveness Check, the timer that tracks this timeout is reset.
 * This value has to be between 40 and 60 (seconds). If it’s lower than 40 or higher than 60, it will be defaulted to 40 or 60 respectively.
 * The default value is -1, falling back to using the value configured with the deprecated API, ZoomSDK.activeTimeoutInSeconds. If ZoomSDK.activeTimeoutInSeconds() is not used, the Liveness Check timeout will be set to the default of 60 (seconds).
 */
@property (nonatomic) int livenessCheckNoInteractionTimeout;
/**
 * The amount of seconds until the ID Scan portion of the Session will be cancelled if there is no user interaction.
 * If a user presses a button or performs a scan, the timer that tracks this timeout is reset.
 * The default is 120s with a minimum value of 60s and a maximum of 180s.
 */
@property (nonatomic) int idScanNoInteractionTimeout;
- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Identity Check Screens.
 */
__attribute__((visibility("default")))
@interface ZoomIDScanCustomization : NSObject
/**
 * Color of the Identity Document Type Selection Screen background.
 * Default is white.
 */
@property (nonatomic, copy) NSArray<UIColor *> * _Nonnull selectionScreenBackgroundColors;
/**
 * Applies a blur effect over the background of the Identity Document Type Selection Screen.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle selectionScreenBlurEffectStyle;
/**
 * Control the opacity of the blur effect over the background of the Identity Document Type Selection Screen.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float selectionScreenBlurEffectOpacity;
/**
 * Color of the text displayed on the Identity Document Type Selection Screen (not including the action button text).
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull selectionScreenForegroundColor;
/**
 * Color of the Identity Document Review Screen background.
 * Default is white.
 */
@property (nonatomic, copy) NSArray<UIColor *> * _Nonnull reviewScreenBackgroundColors;
/**
 * Applies a blur effect over the background of the Identity Document Review Screen.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle reviewScreenBlurEffectStyle;
/**
 * Control the opacity of the blur effect over the background of the Identity Document Review Screen.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float reviewScreenBlurEffectOpacity;
/**
 * Color of the instruction message text displayed on the Identity Document Review Screen (not including the action button).
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull reviewScreenForegroundColor;
/**
 * Color of the text view background during the Identity Document Review Screen.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull reviewScreenTextBackgroundColor;
/**
 * Color of the text view background border during the Identity Document Review Screen.
 * Default is transparent.
 */
@property (nonatomic, strong) UIColor * _Nonnull reviewScreenTextBackgroundBorderColor;
/**
 * Corner radius of the text view background and border during the Identity Document Review Screen.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int reviewScreenTextBackgroundCornerRadius;
/**
 * Thickness of the text view background border during the Identity Document Review Screen.
 * Default is 0.
 */
@property (nonatomic) int reviewScreenTextBackgroundBorderWidth;
/**
 * Color of the instruction message text displayed on the Identity Document Capture Screen (not including the action button text or the tap-to-focus message text).
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureScreenForegroundColor;
/**
 * Color of the text view background during the Identity Document Capture Screen.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureScreenTextBackgroundColor;
/**
 * Color of the text view background border during the Identity Document Capture Screen.
 * Default is transparent.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureScreenTextBackgroundBorderColor;
/**
 * Corner radius of the text view background and border during the Identity Document Capture Screen.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int captureScreenTextBackgroundCornerRadius;
/**
 * Thickness of the text view background border during the Identity Document Capture Screen.
 * Default is 0.
 */
@property (nonatomic) int captureScreenTextBackgroundBorderWidth;
/**
 * Color of the tap-to-focus message text displayed below the Capture Frame during the Identity Document Capture Screen.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureScreenFocusMessageTextColor;
/**
 * Spacing between characters of the tap-to-focus message below the Capture Frame during the Identity Document Capture Screen.
 * Default is 0.
 */
@property (nonatomic) float captureScreenFocusMessageTextSpacing;
/**
 * Font of the tap-to-focus message text below the Capture Frame during the Identity Document Capture Screen.
 * Default is a light system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull captureScreenFocusMessageFont;
/**
 * Color of the action button's text during Identity Check Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextNormalColor;
/**
 * Color of the action button's background during Identity Check Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundNormalColor;
/**
 * Color of the action button's text when the button is pressed during the Identity Check Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextHighlightColor;
/**
 * Color of the action button's background when the button is pressed during the Identity Check Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundHighlightColor;
/**
 * Color of the action button's text when the button is disabled during Identity Check Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextDisabledColor;
/**
 * Color of the action button's background when the button is disabled during the Identity Check Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundDisabledColor;
/**
 * Color of the action button's border during Identity Check Screens.
 * Default is transparent.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBorderColor;
/**
 * Thickness of the action button's border during the Identity Check Screens.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int buttonBorderWidth;
/**
 * Corner radius of the action button's border during the Identity Check Screens.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int buttonCornerRadius;
/**
 * Control the percent of the available ZoOm Frame width to use for the action button during the Identity Check Screens.
 * Relative width percent is represented in decimal notation, ranging from 0.0 to 1.0.
 * If the value configured is equal to or greater than 1.0, the action button will be drawn to at max width within the ZoOm Frame.
 * If the value configured results in a width that is less than the action button's height, the action button's width will equal its height.
 * Note: The Identity Document Review Screen action buttons will be drawn at half the configured width.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) float buttonRelativeWidth;
/**
 * Font of the title during the Identity Document Type Selection Screen.
 * Default is a semi-bold system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull headerFont;
/**
 * Spacing between characters of the title during the Identity Document Type Selection Screen.
 * Default is 1.5.
 */
@property (nonatomic) float headerTextSpacing;
/**
 * Font of the instruction message text during the Identity Document Capture and Review Screens.
 * Default is a light system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull subtextFont;
/**
 * Spacing between characters of the instruction messages during the Identity Document Capture and Review Screens.
 * Default is 0.
 */
@property (nonatomic) float subtextTextSpacing;
/**
 * Font of the action button's text during the Identity Check Screens.
 * Default is a bold system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull buttonFont;
/**
 * Spacing between characters of the action button's text during the Identity Check Screens.
 * Default is 1.5.
 */
@property (nonatomic) float buttonTextSpacing;
/**
 * Controls whether to show the 'zoom_branding_logo_id_check' image (or image configured with .selectionScreenBrandingImage) on the Identity Document Type Selection Screen.
 * Default is true (visible).
 */
@property (nonatomic) BOOL showSelectionScreenBrandingImage;
/**
 * Image displayed on the Identity Document Type Selection Screen.
 * Default is configured to use image named 'zoom_branding_logo_id_check' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable selectionScreenBrandingImage;
/**
 * Color of the Identity Document Capture Screen's background.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureScreenBackgroundColor;
/**
 * Color of the Identity Document Capture Frame's stroke.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull captureFrameStrokeColor;
/**
 * Thickness of the Identity Document Capture Frame's stroke.
 * Default is dynamically configured per device at runtme.
 */
@property (nonatomic) int captureFrameStrokeWith;
/**
 * Corner radius of the Identity Document Capture Frame.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int captureFrameCornerRadius;
/**
 * Image displayed for the Torch button on the Identity Document Capture Screen when the torch/flashlight is active/on.
 * Default is configured to use image named 'zoom_active_torch' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable activeTorchButtonImage;
/**
 * Image displayed for the Torch button on the Identity Document Capture Screen when the torch/flashlight is inactive/off.
 * Default is configured to use image named 'zoom_inactive_torch' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable inactiveTorchButtonImage;

@end

/**
 * Customize the New User Guidance and Retry Screens.
 * New User Guidance Screens are shown before the ZoOm Session and Retry Screens are shown after an unsuccessful ZoOm Session.
 */
__attribute__((visibility("default")))
@interface ZoomGuidanceCustomization : NSObject
/**
 * Color of the background for the New User Guidance and Retry Screens.
 * Default is white.
 */
@property (nonatomic, copy) NSArray<UIColor *> * _Nonnull backgroundColors;
/**
 * Applies a blur effect over the background of the New User Guidance and Retry Screens.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle blurEffectStyle;
/**
 * Control the opacity of the blur effect over the background of the New User Guidance and Retry Screens.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float blurEffectOpacity;
/**
 * Color of the text displayed on the New User Guidance and Retry Screens (not including the action button text).
 * Note: This customization can be overridden for specific text using ZoomGuidanceCustomization.readyScreenHeaderTextColor, .readyScreenSubtextTextColor, .retryScreenHeaderTextColor, and/or .retryScreenSubtextTextColor.
 * Default is black.
 */
@property (nonatomic, strong) UIColor * _Nonnull foregroundColor;
/**
 * Color of the action button's text during the New User Guidance and Retry Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextNormalColor;
/**
 * Color of the action button's background during the New User Guidance and Retry Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundNormalColor;
/**
 * Color of the action button's text when the button is pressed during the New User Guidance and Retry Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextHighlightColor;
/**
 * Color of the action button's background when the button is pressed during the New User Guidance and Retry Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundHighlightColor;
/**
 * Color of the action button's text when the button is disabled during the New User Guidance and Retry Screens.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonTextDisabledColor;
/**
 * Color of the action button's background when the button is disabled during the New User Guidance and Retry Screens.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBackgroundDisabledColor;
/**
 * Color of the action button's border during the New User Guidance and Retry Screens.
 * Default is transparent.
 */
@property (nonatomic, strong) UIColor * _Nonnull buttonBorderColor;
/**
 * Thickness of the action button's border during the New User Guidance and Retry Screens.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int buttonBorderWidth;
/**
 * Corner radius of the action button's border during the New User Guidance and Retry Screens.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int buttonCornerRadius;
/**
 * Control the percent of the available ZoOm Frame width to use for the action button during the New User Guidance and Retry Screens.
 * Relative width percent is represented in decimal notation, ranging from 0.0 to 1.0.
 * If the value configured is equal to or greater than 1.0, the action button will be drawn to at max width within the ZoOm Frame.
 * If the value configured results in a width that is less than the action button's height, the action button's width will equal its height.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) float buttonRelativeWidth;
/**
 * Font of the title during the New User Guidance and Retry Screens.
 * Note: This customization can be overridden for specific text using ZoomGuidanceCustomization.readyScreenHeaderFont and/or .retryScreenHeaderFont.
 * Default is a semi-bold system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull headerFont;
/**
 * Spacing between characters of the title during the New User Guidance and Retry Screens.
 * Note: This customization can be overridden for specific text using ZoomGuidanceCustomization.readyScreenHeaderTextSpacing and/or .retryScreenHeaderTextSpacing.
 * Default is 1.5.
 */
@property (nonatomic) float headerTextSpacing;
/**
 * Font of the title's subtext and messages during the New User Guidance and Retry Screens.
 * Note: This customization can be overridden for specific text using ZoomGuidanceCustomization.readyScreenSubtextFont and/or .retryScreenSubtextFont.
 * Default is a light system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull subtextFont;
/**
 * Spacing between characters of the title's subtext and messages during the New User Guidance and Retry Screens.
 * Note: This customization can be overridden for specific text using ZoomGuidanceCustomization.readyScreenSubtextTextSpacing and/or .retryScreenSubtextTextSpacing.
 * Default is 0.
 */
@property (nonatomic) float subtextTextSpacing;
/**
 * Specify an attributed string to use instead of the localized string for the text of the title displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * If this value is nil, the localized string, ZoomStrings.zoom_instructions_header_ready, will be used for the text of the title displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Default is nil.
 */
@property (nonatomic, strong) NSAttributedString * _Nullable readyScreenHeaderAttributedString;
/**
 * Font of the title's text displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the header font configured with ZoomGuidanceCustomization.headerFont for the Get Ready To ZoOm Screen.
 * If this value is nil, ZoomGuidanceCustomization.headerFont will be used for the font of the title's text displayed on the Get Ready To ZoOm Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIFont * _Nullable readyScreenHeaderFont;
/**
 * Spacing between the characters of the title's text displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the header text spacing configured with ZoomGuidanceCustomization.headerTextSpacing for the Get Ready To ZoOm Screen.
 * If this value is -1.0f, ZoomGuidanceCustomization.headerTextSpacing will be used for the character spacing of the title's text displayed on the Get Ready To ZoOm Screen.
 * Default value is -1.0f.
 */
@property (nonatomic) float readyScreenHeaderTextSpacing;
/**
 * Color of the header text displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the header text color configured with ZoomGuidanceCustomization.foregroundColor for the Get Ready To ZoOm Screen.
 * If this value is nil, ZoomGuidanceCustomization.foregroundColor will be used for the color of the title's text displayed on the Get Ready To ZoOm Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIColor * _Nullable readyScreenHeaderTextColor;
/**
 * Specify an attributed string to use instead of the localized string for the text of the title's subtext displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * If this value is nil, the localized string, ZoomStrings.zoom_instructions_message_ready, will be used for the text of the title's subtext displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Default is nil.
 */
@property (nonatomic, strong) NSAttributedString * _Nullable readyScreenSubtextAttributedString;
/**
 * Font of the title's subtext displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the title's subtext font configured with ZoomGuidanceCustomization.subtextFont for the Get Ready To ZoOm Screen.
 * If this value is nil, ZoomGuidanceCustomization.subtextFont will be used for the font of the title's subtext displayed on the Get Ready To ZoOm Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIFont * _Nullable readyScreenSubtextFont;
/**
 * Spacing between the characters of the title's subtext displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the subtext text spacing configured with ZoomGuidanceCustomization.subtextTextSpacing for the Get Ready To ZoOm Screen.
 * If this value is -1.0f, ZoomGuidanceCustomization.subtextTextSpacing will be used for the character spacing of the title's subtext displayed on the Get Ready To ZoOm Screen.
 * Default value is -1.0f.
 */
@property (nonatomic) float readyScreenSubtextTextSpacing;
/**
 * Color of the title's subtext displayed on the Get Ready To ZoOm Screen during the New User Guidance and Retry Screens.
 * Note: This will override the title's subtext color configured with ZoomGuidanceCustomization.foregroundColor for the Get Ready To ZoOm Screen.
 * If this value is nil, ZoomGuidanceCustomization.foregroundColor will be used for the color of the title's subtext displayed on the Get Ready To ZoOm Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIColor * _Nullable readyScreenSubtextTextColor;
/**
 * Specify an attributed string to use instead of the localized string for the text of the title displayed on the first Retry Screen.
 * If this value is nil, the localized string, ZoomStrings.zoom_retry_header, will be used for the text of the title displayed on the first Retry Screen.
 * Default is nil.
 */
@property (nonatomic, strong) NSAttributedString * _Nullable retryScreenHeaderAttributedString;
/**
 * Font of the title's text displayed on the first Retry Screen.
 * Note: This will override the header font configured with ZoomGuidanceCustomization.headerFont for the first Retry Screen.
 * If this value is nil, ZoomGuidanceCustomization.headerFont will be used for the font of the title's text displayed on the first Retry Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIFont * _Nullable retryScreenHeaderFont;
/**
 * Spacing between the characters of the title's text displayed on the first Retry Screen.
 * Note: This will override the header text spacing configured with ZoomGuidanceCustomization.headerTextSpacing for the first Retry Screen.
 * If this value is -1.0f, ZoomGuidanceCustomization.headerTextSpacing will be used for the character spacing of the title's text displayed on the first Retry Screen.
 * Default value is -1.0f.
 */
@property (nonatomic) float retryScreenHeaderTextSpacing;
/**
 * Color of the header text displayed on the first Retry Screen.
 * Note: This will override the header text color configured with ZoomGuidanceCustomization.foregroundColor for the first Retry Screen.
 * If this value is nil, ZoomGuidanceCustomization.foregroundColor will be used for the color of the title's text displayed on the first Retry Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIColor * _Nullable retryScreenHeaderTextColor;
/**
 * Specify an attributed string to use instead of the localized string for the text of the title's subtext displayed on the first Retry Screen.
 * If this value is nil, the localized strings, ZoomStrings.zoom_retry_subheader_message, will be used for the text of the title's subtext displayed on the first Retry Screen.
 * Default is nil.
 */
@property (nonatomic, strong) NSAttributedString * _Nullable retryScreenSubtextAttributedString;
/**
 * Font of the title's subtext and messages displayed on the first Retry Screen.
 * Note: This will override the font of the title's subtext and messages configured with ZoomGuidanceCustomization.subtextFont for the first Retry Screen.
 * If this value is nil, ZoomGuidanceCustomization.subtextFont will be used for the font of the title's subtext and messages displayed on the first Retry Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIFont * _Nullable retryScreenSubtextFont;
/**
 * Spacing between the characters of the title's subtext and messages displayed on the first Retry Screen.
 * Note: This will override the subtext and message text spacing configured with ZoomGuidanceCustomization.subtextTextSpacing for the first Retry Screen.
 * If this value is -1.0f, ZoomGuidanceCustomization.subtextTextSpacing will be used for the character spacing of the title's subtext and messages displayed on the first Retry Screen.
 * Default value is -1.0f.
 */
@property (nonatomic) float retryScreenSubtextTextSpacing;
/**
 * Color of the title's subtext and messages displayed on the first Retry Screen.
 * Note: This will override the title's subtext and message color configured with ZoomGuidanceCustomization.foregroundColor for the first Retry Screen.
 * If this value is nil, ZoomGuidanceCustomization.foregroundColor will be used for the color of the title's subtext displayed on the first Retry Screen.
 * Default value is nil.
 */
@property (nonatomic, strong) UIColor * _Nullable retryScreenSubtextTextColor;
/**
 * Font of the action button's text during the New User Guidance and Retry Screens.
 * Default is a bold system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull buttonFont;
/**
 * Spacing between characters of the action button's text during the New User Guidance and Retry Screens.
 * Default is 1.5.
 */
@property (nonatomic) float buttonTextSpacing;
/**
 * Background color of the Get Ready To ZoOm Screen text views during the New User Guidance and Retry Screens.
 * This will only be visible on iPhone 4/4s models.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull readyScreenTextBackgroundColor;
/**
 * Background corner radius of the Get Ready To ZoOm Screen text views during the New User Guidance and Retry Screens.
 * This will only be visible on iPhone 4/4s models.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int readyScreenTextBackgroundCornerRadius;
/**
 * Color of the Get Ready To ZoOm Screen's oval fill.
 * Default is transparent.
 */
@property (nonatomic, strong) UIColor * _Nonnull readyScreenOvalFillColor;
/**
 * Image displayed as Ideal ZoOm example (right image) during the first Retry Screen.
 * Default is configured to use image named 'zoom_ideal' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable retryScreenIdealZoomImage;
/**
 * Images displayed as Ideal ZoOm examples (right image) during the first Retry Screen.
 * When configured to a non-empty array, these images will override the single image configured for .retryScreenIdealZoomImage or  imageCustomization.idealZoomImage (deprecated).
 * Default is an empty array.
 */
@property (nonatomic, strong) NSArray<UIImage *> * _Nullable retryScreenSlideshowImages;
/**
 * Control the time, in milliseconds, that each image is shown for before transitioning to the next image.
 * Default is 1500.
 */
@property (nonatomic) int retryScreenSlideshowInterval;
/**
 * Control whether to allow the slideshow images to appear in a randomized order during each Retry Screen.
 * Default is true (enabled).
 */
@property (nonatomic) BOOL enableRetryScreenSlideshowShuffle;
/**
 * Color of the image borders during the first Retry Screen.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull retryScreenImageBorderColor;
/**
 * Thickness of the image borders during the first Retry Screen.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int retryScreenImageBorderWidth;
/**
 * Corner radius of the image borders during the first Retry Screen.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int retryScreenImageCornerRadius;
/**
 * Color of the oval's stroke that overlay's the Ideal image example during the first Retry Screen.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull retryScreenOvalStrokeColor;
/**
 * Control whether to layout the Retry Screen's instruction messages using bullet-points.
 * Applicable localized instruction message strings include: zoom_retry_instruction_message_1, zoom_retry_instruction_message_2, zoom_retry_instruction_message_3.
 * If enabled, each instruction message will be placed on a new line, proceeded with a bullet-point, and will not extend to multiple lines.
 * If disabled, all instruction messages will be concatenated into a multi-line string.
 * Default is true (enabled).
 */
@property (nonatomic) BOOL enableRetryScreenBulletedInstructions;
/**
 * Image displayed on the Camera Permissions Screen.
 * Default is configured to use image named 'zoom_camera' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable cameraPermissionsScreenImage;
/** Customize the images used for the New User Guidance and Retry Screens. */
@property (nonatomic, strong) ZoomGuidanceImagesCustomization * _Nonnull imageCustomization DEPRECATED_MSG_ATTRIBUTE("Note: This customization property and class are deprecated and will be removed in an upcoming version of the ZoOm SDK. ZoomGuidanceImagesCustomization.idealZoomImage has been renamed and moved to ZoomGuidanceCustomization.retryScreenIdealZoomImage. ZoomGuidanceImagesCustomization.cameraPermissionsScreenImage has been moved to ZoomGuidanceCustomization.cameraPermissionsScreenImage.");
- (nonnull instancetype) init;
@end

/**
 * Customize the images used for the New User Guidance and Retry Screens.
 */
__attribute__((visibility("default")))
@interface ZoomGuidanceImagesCustomization : NSObject
/**
 * Image displayed as Ideal ZoOm example (right image) during the first Retry Screen.
 */
@property (nonatomic, strong) UIImage * _Nullable idealZoomImage DEPRECATED_MSG_ATTRIBUTE("Note: This customization property has been renamed and moved to ZoomGuidanceCustomization.retryScreenIdealZoomImage. This customization property location and class, ZoomGuidanceImagesCustomization, are deprecated and will be removed in an upcoming version of the ZoOm SDK.");
/**
 * Image displayed on the Camera Permissions Screen.
 */
@property (nonatomic, strong) UIImage * _Nullable cameraPermissionsScreenImage DEPRECATED_MSG_ATTRIBUTE("Note: This customization property has been moved to ZoomGuidanceCustomization.cameraPermissionsScreenImage. This customization property location and class, ZoomGuidanceImagesCustomization, are deprecated and will be removed in an upcoming version of the ZoOm SDK.");
- (nonnull instancetype) init;
- (nonnull instancetype)initWithGoodLightingImage:(UIImage * _Nullable)goodLightingImage goodAngleImage:(UIImage * _Nullable)goodAngleImage badLightingImage:(UIImage * _Nullable)badLightingImage badAngleImage:(UIImage * _Nullable)badAngleImage idealZoomImage:(UIImage * _Nullable)idealZoomImage cameraPermissionsScreenImage:(UIImage * _Nullable)cameraPermissionsScreenImage lockoutScreenLockedImage:(UIImage * _Nullable)lockoutScreenLockedImage lockoutScreenUnlockedImage:(UIImage * _Nullable)lockoutScreenUnlockedImage errorScreenImage:(UIImage *_Nullable)errorScreenImage skipGuidanceButtonImage:(UIImage *_Nullable)skipGuidanceButtonImage introScreenBrandingImage:(UIImage * _Nullable)introScreenBrandingImage NS_SWIFT_NAME(init(goodLightingImage:goodAngleImage:badLightingImage:badAngleImage:idealZoomImage:cameraPermissionsScreenImage:lockoutScreenLockedImage:lockoutScreenUnlockedImage:errorScreenImage:skipGuidanceButtonImage:introScreenBrandingImage:)) DEPRECATED_MSG_ATTRIBUTE("Note: This customization class and constructor are deprecated and will be removed in an upcoming version of the ZoOm SDK.");
@end

/**
 * Customize the Result Screen.
 * Shown for server-side work and response handling.
 */
__attribute__((visibility("default")))
@interface ZoomResultScreenCustomization : NSObject
/**
 * Color of the Result Screen's background.
 * Default is white.
 */
@property (nonatomic, copy) NSArray<UIColor *> * _Nonnull backgroundColors;
/**
 * Applies a blur effect over the background of the Result Screen.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle blurEffectStyle;
/**
 * Control the opacity of the blur effect over the background of the Result Screen.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float blurEffectOpacity;
/**
 * Color of the text displayed on the Result Screen.
 * Default is black.
 */
@property (nonatomic, strong) UIColor * _Nonnull foregroundColor;
/**
 * Font of the message text displayed on the Result Screen.
 * Default is a system font.
 */
@property (nonatomic, strong) UIFont * _Nonnull messageFont;
/**
 * Spacing between characters displayed on the Result Screen.
 * Default is 1.5.
 */
@property (nonatomic) float messageTextSpacing;
/**
 * Color of the activity indicator animation shown during server-side work.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull activityIndicatorColor;
/**
 * Image displayed and rotated during server-side work.
 * If image is configured, default activity indicator will be hidden.
 * Default is set to nil and will fallback to using the default activity indicator animation, which respects the color assigned to .activityIndicatorColor.
 */
@property (nonatomic, strong) UIImage * _Nullable customActivityIndicatorImage;
/**
 * Control the speed of the rotation for your custom activity indicator image.
 * Only applicable when image is configured for .customActivityIndicatorImage.
 * This value indicates the duration of each full rotation (in milliseconds).
 * Default is 1000.
 */
@property (nonatomic) int customActivityIndicatorRotationInterval;
/**
 * Color of the result animation's background.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull resultAnimationBackgroundColor;
/**
 * Color of the result animation's accent color.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull resultAnimationForegroundColor;
/**
 * Image displayed behind the result foreground animation for success scenarios.
 * If image is configured, default result background animation will be hidden.
 * Default is set to nil and will fallback to using the default result background animation, which respects the color assigned to .resultAnimationBackgroundColor.
 */
@property (nonatomic, strong) UIImage * _Nullable resultAnimationSuccessBackgroundImage;
/**
 * Image displayed behind the result foreground animation for unsuccess scenarios.
 * If image is configured, default result background animation will be hidden.
 * Default is set to nil and will fallback to using the default result background animation, which respects the color assigned to .resultAnimationBackgroundColor.
 */
@property (nonatomic, strong) UIImage * _Nullable resultAnimationUnsuccessBackgroundImage;
/**
 * Control whether to show or hide the upload progress bar during server-side work.
 * Default is true (shown).
 */
@property (nonatomic) BOOL showUploadProgressBar;
/**
 * Color of the upload progress bar's fill.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull uploadProgressFillColor;
/**
 * Color of upload progress bar's track.
 * Default is a semi-opaque shade of black.
 */
@property (nonatomic, strong) UIColor * _Nonnull uploadProgressTrackColor;
- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Oval and the ZoOm Progress Spinner animations.
 */
__attribute__((visibility("default")))
@interface ZoomOvalCustomization : NSObject
/**
 * Color of the ZoOm Oval outline.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull strokeColor;
/**
 * Thickness of the ZoOm Oval outline.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int strokeWidth;
/**
 * Color of the animated ZoOm Progress Spinner strokes.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) UIColor * _Nonnull progressColor1;
@property (nonatomic, strong) UIColor * _Nonnull progressColor2;
/**
 * Radial offset of the animated ZoOm Progress Spinner strokes relative to the outermost bounds of the ZoOm Oval outline.
 * As this value increases, the ZoOm Progress Spinner stroke animations move closer toward the ZoOm Oval's center.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int progressRadialOffset;
/**
 * Thickness of the animated ZoOm Progress Spinner strokes.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int progressStrokeWidth;
- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Feedback Bar.
 */
__attribute__((visibility("default")))
@interface ZoomFeedbackCustomization : NSObject
/**
 * Shadow displayed behind the ZoOm Feedback Bar.
 * This customization can be set to nil for no shadow, or it can be set to an instance of ZoomShadow.
 * Note: ZoomShadow's configurable properties correlate to the shadow-related attributes available for CALayer objects.
 * Default is a custom sized black shadow.
 */
@property (nonatomic) ZoomShadow * _Nullable shadow;
/**
 * Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.
 * Size of the ZoOm Feedback Bar, which is relative to the current .sizeRatio of the ZoOm Frame.
 * This customization is not available by default and exists for legacy support only.
 * Default is ZoOm picks the optimal Feedback Bar size depending on device size and camera support.
 */
@property (nonatomic) CGSize size;
/**
 * Vertical spacing of the Feedback Bar from the top boundary of the ZoOm Frame, which is relative to the current .sizeRatio of the ZoOm Frame.
 * This customization is not available by default and exists for legacy support only.
 * Default is ZoOm picks the optimal top margin depending on device size and camera support.
 */
@property (nonatomic) int topMargin;
/**
 * Corner radius of the ZoOm Feedback Bar.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int cornerRadius;
/**
 * Spacing between characters displayed within the ZoOm Feedback Bar.
 * Default is 1.5.
 */
@property (nonatomic) float textSpacing;
/**
 * Color of the text displayed within the ZoOm Feedback Bar.
 * Default is white.
 */
@property (nonatomic, strong) UIColor * _Nonnull textColor;
/**
 * Font of the text displayed within the ZoOm Feedback Bar.
 * Default is system font.
 */
@property (nonatomic) UIFont * _Nonnull textFont;
/**
 * Control whether to enable the pulsating-text animation within the ZoOm Feedback Bar.
 * Default is true (enabled).
 */
@property (nonatomic) BOOL enablePulsatingText;
/**
 * Color of the ZoOm Feedback Bar's background. Recommend making this have some transparency.
 * Default is custom ZoOm color.
 */
@property (nonatomic, strong) CAGradientLayer * _Nonnull backgroundColor;
/**
 * Control the percent of the available ZoOm Frame width to use for the ZoOm Feedback Bar's width.
 * Relative width percent is represented in decimal notation, ranging from 0.0 to 1.0.
 * If the value configured is equal to or greater than 1.0, the ZoOm Feedback Bar will be drawn to at max width within the ZoOm Frame.
 * If the value configured results in a width that is less than the minimum width, which is 2x the ZoOm Feedback Bar's height, then the ZoOm Feedback Bar's width will be set at the minimum.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) float relativeWidth;
- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Frame.
 */
__attribute__((visibility("default")))
@interface ZoomFrameCustomization : NSObject
/**
 * Shadow displayed behind the ZoOm Frame.
 * This customization can be set to nil for no shadow, or it can be set to an instance of ZoomShadow.
 * Note: ZoomShadow's configurable properties correlate to the shadow-related attributes available for CALayer objects.
 * Default is nil (no shadow).
 */
@property (nonatomic) ZoomShadow * _Nullable shadow;
/**
 * Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.
 * Size ratio of the ZoOm Frame's width relative to the width the the current device's display.
 * This customization is not available by default and exists for legacy support only.
 * Default is ZoOm picks the optimal frame size depending on device size and camera support.
 */
@property (nonatomic) float sizeRatio;
/**
 * Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.
 * Vertical spacing of the ZoOm Frame from the top boundary of the current device's display.
 * This customization is not available by default and exists for legacy support only.
 * Default is ZoOm picks the optimal top margin depending on device size and camera support.
 */
@property (nonatomic) int topMargin;
/**
 * Corner radius of the ZoOm Frame.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int cornerRadius;
/**
 * Thickness of the ZoOm Frame's border.
 * Default is dynamically configured per device at runtime.
 */
@property (nonatomic) int borderWidth;
/**
 * Color of the ZoOm Frame's border.
 * Default is custom ZoOm color.
 */
@property (nonatomic) UIColor * _Nonnull borderColor;
/**
 * Color of the background surrounding the oval outline during ZoOm.
 * Default is custom ZoOm color.
 */
@property (nonatomic) UIColor * _Nonnull backgroundColor;
/**
 * Applies a blur effect over the background surrounding the oval outline during ZoOm.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle blurEffectStyle;
/**
 * Control the opacity of the blur effect over the background surrounding the oval outline during ZoOm.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float blurEffectOpacity;
/**
 * Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.
 * Control behavior of the ZoOm Frame when .sizeRatio is set to 1.0.
 * Specific behavior for iPhone X models.
 * Default is ZoomFullScreenBehaviorAutomatic.
 */
@property (nonatomic) enum ZoomFullScreenBehavior fullScreenBehavior DEPRECATED_MSG_ATTRIBUTE("Note: This functionality no longer exists in the ZoOm SDK. Using this API is now a no-op.");
- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Cancel Button.
 * Shown during ZoOm, New User Guidance, Retry, and Identity Check Screens.
 */
__attribute__((visibility("default")))
@interface ZoomCancelButtonCustomization : NSObject
/**
 * Image displayed on the ZoOm Cancel Button.
 * Default is configured to use image named 'zoom_cancel' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable customImage;
/**
 * Location, or use, of the ZoOm Cancel Button.
 * Default is ButtonLocationTopLeft.
 */
@property (nonatomic) enum ZoomCancelButtonLocation location;
/**
 * The frame of the cancel button within the current screen's bounds.
 * Note: In order to use a custom-located cancel button, you MUST set .location to the enum value ZoomCancelButtonLocationCustom.
 * Default is a CGRect at origin 0,0 with a size of 0 by 0.
 */
@property (nonatomic) CGRect customLocation;

- (nonnull instancetype) init;
@end

/**
 * Customize the ZoOm Overlay.
 * The ZoOm Overlay separates the ZoOm Interface from the presenting application, covering the device's full screen.
 */
__attribute__((visibility("default")))
@interface ZoomOverlayCustomization : NSObject
/**
 * Color of the ZoOm Overlay background.
 * Default is transparent.
 */
@property (nonatomic, copy) UIColor * _Nonnull backgroundColor;
/**
 * Applies a blur effect over the background of the ZoOm Overlay.
 * Default is ZoomBlurEffectOff.
 */
@property (nonatomic) enum ZoomBlurEffectStyle blurEffectStyle;
/**
 * Control the opacity of the blur effect over the background of the ZoOm Overlay.
 * Values must be between 0 and 1.
 * Default is 1.
 */
@property (nonatomic) float blurEffectOpacity;
/**
 * Image displayed below the ZoOm Frame on top of the ZoOm Overlay.
 * Default is configured to use image named 'zoom_your_app_logo' located in application's Assets folder.
 */
@property (nonatomic, strong) UIImage * _Nullable brandingImage;
/**
 * Control whether to show the branding image below the ZoOm Frame on top of the ZoOm Overlay.
 * Default is true (shown).
 */
@property (nonatomic) BOOL showBrandingImage;
- (nonnull instancetype) init;
@end

enum ZoomSDKStatus : NSInteger;
enum ZoomSessionStatus: NSInteger;
enum ZoomIDScanStatus: NSInteger;
enum ZoomIDType: NSInteger;
enum ZoomIDScanRetryMode: NSInteger;
enum ZoomIDScanNextStep: NSInteger;

@class UIViewController;
@protocol ZoomSessionDelegate;
@protocol ZoomFaceMapProcessorDelegate;
@protocol ZoomFaceMapResultCallback;
@protocol ZoomIDScanProcessorDelegate;
@protocol ZoomIDScanResultCallback;

/**
 The ZoomSDKProtocol exposes methods the app can use to configure the behavior of ZoOm.
 */
@protocol ZoomSDKProtocol

/**
 Initialize the ZoOm SDK using your license key identifier for online validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.

 @param licenseKeyIdentifier Identifies the client for determination of license capabilities
 @param faceMapEncryptionKey The encryption key to be used for ZoOm Server FaceMaps
 @param preloadZoomSDK boolean to execute preload()
 @param completion Callback after license validation has completed
 */
- (void)initialize:(NSString * _Nonnull)licenseKeyIdentifier faceMapEncryptionKey:(NSString * _Nonnull)faceMapEncryptionKey preloadZoomSDK:(BOOL)preloadZoomSDK completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseKeyIdentifier:faceMapEncryptionKey:preloadZoomSDK:completion:));

/**
 Initialize the ZoOm SDK using your license key identifier for online validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseKeyIdentifier Identifies the client for determination of license capabilities
 @param faceMapEncryptionKey The encryption key to be used for ZoOm Server FaceMaps
 @param completion Callback after license validation has completed
 */
- (void)initialize:(NSString * _Nonnull)licenseKeyIdentifier faceMapEncryptionKey:(NSString * _Nonnull)faceMapEncryptionKey completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseKeyIdentifier:faceMapEncryptionKey:completion:));

/**
 Initialize the ZoOm SDK using your license key identifier for online validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.

 @param licenseKeyIdentifier Identifies the client for determination of license capabilities
 @param preloadZoomSDK boolean to execute preload()
 @param completion Callback after license validation has completed
 */
- (void)initialize:(NSString * _Nonnull)licenseKeyIdentifier preloadZoomSDK:(BOOL)preloadZoomSDK completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseKeyIdentifier:preloadZoomSDK:completion:)) DEPRECATED_MSG_ATTRIBUTE("Use initialize(licenseKeyIdentifier:faceMapEncryptionKey:completion:)");

/**
 @deprecated
 Initialize the ZoOm SDK using your license key identifier for online validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseKeyIdentifier Identifies the client for determination of license capabilities
 @param completion Callback after license validation has completed
 */
- (void)initialize:(NSString * _Nonnull)licenseKeyIdentifier completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseKeyIdentifier:completion:))  DEPRECATED_MSG_ATTRIBUTE("Use initialize(licenseKeyIdentifier:faceMapEncryptionKey:completion:)");

/**
 Initialize the ZoOm SDK using your license file for offline validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseText The string contents of the license file
 @param licenseKeyIdentifier Identifies the client
 @param faceMapEncryptionKey The encryption key to be used for ZoOm Server FaceMaps
 @param preloadZoomSDK boolean to execute preload()
 @param completion Callback after license validation has completed
 */
- (void)initializeWithLicense:(NSString * _Nonnull)licenseText licenseKeyIdentifier:(NSString * _Nonnull)licenseKeyIdentifier faceMapEncryptionKey:(NSString * _Nonnull)faceMapEncryptionKey preloadZoomSDK:(BOOL)preloadZoomSDK completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseText:licenseKeyIdentifier:faceMapEncryptionKey:preloadZoomSDK:completion:));

/**
 Initialize the ZoOm SDK using your license file for offline validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseText The string contents of the license file
 @param licenseKeyIdentifier Identifies the client
 @param faceMapEncryptionKey The encryption key to be used for ZoOm Server FaceMaps
 @param completion Callback after license validation has completed
 */
- (void)initializeWithLicense:(NSString * _Nonnull)licenseText licenseKeyIdentifier:(NSString * _Nonnull)licenseKeyIdentifier faceMapEncryptionKey:(NSString * _Nonnull)faceMapEncryptionKey completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseText:licenseKeyIdentifier:faceMapEncryptionKey:completion:));

/**
 Initialize the ZoOm SDK using your license file for offline validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseText The string contents of the license file
 @param licenseKeyIdentifier Identifies the client
 @param preloadZoomSDK boolean to execute preload()
 @param completion Callback after license validation has completed
 */
- (void)initializeWithLicense:(NSString * _Nonnull)licenseText licenseKeyIdentifier:(NSString * _Nonnull)licenseKeyIdentifier preloadZoomSDK:(BOOL)preloadZoomSDK completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseText:licenseKeyIdentifier:preloadZoomSDK:completion:)) DEPRECATED_MSG_ATTRIBUTE("Use initialize(licenseText:licenseKeyIdentifier:faceMapEncryptionKey:preloadZoomSDK:completion:)");

/**
 Initialize the ZoOm SDK using your license file for offline validation.
 This <em>must</em> be called at least once by the application before invoking any SDK operations.
 This function may be called repeatedly without harm.
 
 @param licenseText The string contents of the license file
 @param licenseKeyIdentifier Identifies the client
 @param completion Callback after license validation has completed
 */
- (void)initializeWithLicense:(NSString * _Nonnull)licenseText licenseKeyIdentifier:(NSString * _Nonnull)licenseKeyIdentifier completion:(void (^ _Nullable)(BOOL))completion NS_SWIFT_NAME(initialize(licenseText:licenseKeyIdentifier:completion:)) DEPRECATED_MSG_ATTRIBUTE("User initialize(licenseText:licenseKeyIdentifier:faceMapEncryptionKey:completion:)");

/**
 Configures the look and feel of ZoOm.

 @param customization An instance of ZoomCustomization
 */
- (void)setCustomization:(ZoomCustomization * _Nonnull)customization;

/**
 Configures the look and feel of ZoOm when low light mode is active. If not configured or set to nil, we will fallback to using the ZoomCustomization instance configured with setCustomization(), or our default customizations if setCustomization() has not been called.

 @param lowLightCustomization An instance of ZoomCustomization or nil
 */
- (void)setLowLightCustomization:(ZoomCustomization * _Nullable)lowLightCustomization;

/**
 Convenience method to check if the ZoOm Device License Key Identifier is valid.
 ZoOm requires that the app successfully initializes the SDK and receives confirmation of a valid Device License Key Identifier at least once before launching a ZoOm session.  After the initial validation, the SDK will allow a limited number of sessions without any further requirement for successful round-trip connection to the ZoOm server. This allows the app to use ZoOm for a limited time without network connectivity.  During this ‘grace period’, the function will return ‘true’.

 @return True, if the SDK license has been validated, false otherwise.
 */
- (BOOL)isLicenseValid;

/**
 Returns the current status of the ZoOm SDK.
 @return ZoomSDKStatusInitialized, if ready to be used.
 */
- (enum ZoomSDKStatus)getStatus;

/**
 Convenience method to get the time when a lockout will end.
 This will be null if the user is not locked out
 @return NSDate
 */
- (NSDate * _Nullable)getLockoutEndTime;

/**
 * @return True if the user is locked out of ZoOm
 */
- (BOOL)isLockedOut;

/**
 Preload ZoOm – this can be used to reduce the amount of time it takes to initialize a ZoOm view controller.  Preload is automatically called during initialize.
 You may want to call this function when transitioning to a ViewController in your application from which you intend to launch ZoOm.
 This ensures that ZoOm will launch as quickly as possible when requested.
 */
- (void)preload;

/**
 Unload resources related to ZoOm.
 */
- (void)unload;

/**
 Convenience method to check for camera permissions.
 This function is used to check the camera permission status prior to using ZoOm.  If camera permission has not been previously granted,
 ZoOm will display a UI asking the user to allow permission.  Some applications may wish to manage camera permission themselves - those applications
 should verify camera permissions prior to transitioning to ZoOm.

 @return Value representing the current camera permission status
 */
@property (nonatomic, readonly) enum ZoomCameraPermissionStatus cameraPermissionStatus;

/** Sets a preferred language to be used for all strings. */
- (void)setLanguage:(NSString * _Nonnull)language;

/**
 Configure where the ZoOm SDK looks for custom localized strings.
 @param table Optional name of the string table to look in.  By default, this is "Zoom" and string will be read from Zoom.strings.
 @param bundle Optional NSBundle instance to search for ZoOm string definitions in.  This will be searched after the main bundle and before ZoOm's default strings.
 */
- (void)configureLocalizationWithTable:(NSString * _Nullable)table bundle:(NSBundle * _Nullable)bundle;

/**
 Configure where the ZoOm SDK looks for custom ZoOm images. If you use this API, you MUST call this function prior to initializing the ZoomCustomization object.
@param bundle Optional NSBundle instance to search for ZoOm images.
*/
- (void)setBundleForZoomImages:(NSBundle * _Nullable)bundle;

/**
 Sets the type of audit trail images to be collected.
 If this property is not set to Disabled, then ZoOm will include a sample of some of the camera frames collected during the ZoOm session.
 */
@property (nonatomic) enum ZoomAuditTrailType auditTrailType;

/**
 Note: This API method is deprecated and will be removed in an upcoming version of the SDK. To configure the Liveness Check timeout length, set the value of ZoomCustomization.ZoomSessionTimerCustomization.livenessCheckNoInteractionTimeout and apply with ZoomSDK.setCustomization().
 Sets the time in seconds before a timeout occurs in the ZoOm session.
 This value has to be between 30 and 60 seconds. If it’s lower than 30 or higher than 60, it
 will be defaulted to 30 or 60 respectively.
 */
@property (nonatomic) NSInteger activeTimeoutInSeconds DEPRECATED_MSG_ATTRIBUTE("This API method is deprecated and will be removed in an upcoming version of the SDK. To configure the Liveness Check timeout length, set the value of ZoomCustomization.ZoomSessionTimerCustomization.livenessCheckNoInteractionTimeout and apply with ZoomSDK.setCustomization().");

/**
 Fetches the version number of the current ZoOm SDK release
 
 @return Version number of sdk release package
 */
@property (nonatomic, readonly, copy) NSString * _Nonnull version;

/**
 Set the encryption key to be used for ZoOm Server FaceMaps
 
 @param publicKey RSA public key to be used in PEM format
 
 @return TRUE if the key was valid
 */
- (bool)setFaceMapEncryptionKeyWithPublicKey:(NSString * _Nonnull)publicKey NS_SWIFT_NAME(setFaceMapEncryptionKey(publicKey:)) DEPRECATED_MSG_ATTRIBUTE("Use ZoomSDKProtocol.initialize() to supply encryption key");

/**
 * Method to create a valid string to pass as the value for the User-Agent header when calling the FaceTec Managed API.
 * @param sessionId Unique Id for a ZoOm session. This can be obtained from ZoomSessionResult.
 * @return a string that can be used as the value for the User-Agent header.
 */
- (NSString * _Nonnull)createZoomAPIUserAgentString:(NSString * _Nonnull)sessionId;

/**
 Configures and returns a new UIViewController for a ZoOm session with a ZoomFaceMapProcessor.
 Caller should call presentViewController on returned object only once.

 @param delegate The delegate on which the application wishes to receive status results from the session.
 @param faceMapProcessorDelegate The delegate to process ZoOm FaceMaps
*/
- (UIViewController * _Nonnull)createSessionVCWithDelegate:(id <ZoomSessionDelegate> _Nonnull)delegate
                                  faceMapProcessorDelegate:(id <ZoomFaceMapProcessorDelegate> _Nullable)faceMapProcessorDelegate
    NS_SWIFT_NAME(createSessionVC(delegate:faceMapProcessorDelegate:));

/**
 Configures and returns a new UIViewController for a ZoOm session with a ZoomFaceMapProcessor.
 Caller should call presentViewController on returned object only once.

 @param delegate The delegate on which the application wishes to receive status results from the session.
 @param faceMapProcessorDelegate The delegate to process ZoOm FaceMaps.
 @param serverSessionToken A ZoOm Server session token
*/
- (UIViewController * _Nonnull)createSessionVCWithDelegate:(id <ZoomSessionDelegate> _Nonnull)delegate
                                  faceMapProcessorDelegate:(id <ZoomFaceMapProcessorDelegate> _Nullable)faceMapProcessorDelegate
                                        serverSessionToken:(NSString *_Nonnull)serverSessionToken
    NS_SWIFT_NAME(createSessionVC(delegate:faceMapProcessorDelegate:serverSessionToken:));

/**
 Configures and returns a new UIViewController for a ZoOm session with a ZoomFaceMapProcessor and ZoomIDScanProcessor.
 Caller should call presentViewController on returned object only once.

 @param delegate The delegate on which the application wishes to receive status results from the session.
 @param faceMapProcessorDelegate The delegate to process ZoOm FaceMaps.
 @param zoomIDScanProcessorDelegate The delegate to process ID scans.
*/
- (UIViewController * _Nonnull)createSessionVCWithDelegate:(id <ZoomSessionDelegate> _Nonnull)delegate
                                  faceMapProcessorDelegate:(id <ZoomFaceMapProcessorDelegate> _Nullable)faceMapProcessorDelegate
                               zoomIDScanProcessorDelegate:(id <ZoomIDScanProcessorDelegate> _Nullable)zoomIDScanProcessorDelegate
    NS_SWIFT_NAME(createSessionVC(delegate:faceMapProcessorDelegate:zoomIDScanProcessorDelegate:));

/**
 Configures and returns a new UIViewController for a ZoOm session with a ZoomFaceMapProcessor and ZoomIDScanProcessor.
 Caller should call presentViewController on returned object only once.

 @param delegate The delegate on which the application wishes to receive status results from the session.
 @param faceMapProcessorDelegate The delegate to process ZoOm FaceMaps.
 @param zoomIDScanProcessorDelegate The delegate to process ID scans.
 @param serverSessionToken A ZoOm Server session token.
*/
- (UIViewController * _Nonnull)createSessionVCWithDelegate:(id <ZoomSessionDelegate> _Nonnull)delegate
                                  faceMapProcessorDelegate:(id <ZoomFaceMapProcessorDelegate> _Nullable)faceMapProcessorDelegate
                               zoomIDScanProcessorDelegate:(id <ZoomIDScanProcessorDelegate> _Nullable)zoomIDScanProcessorDelegate
                                        serverSessionToken:(NSString *_Nonnull)serverSessionToken
    NS_SWIFT_NAME(createSessionVC(delegate:faceMapProcessorDelegate:zoomIDScanProcessorDelegate:serverSessionToken:));

/** Returns a description string for a ZoomSessionStatus value */
- (NSString * _Nonnull)descriptionForSessionStatus:(enum ZoomSessionStatus)status;

/** Returns a description string for a ZoomSessionStatus value */
- (NSString * _Nonnull)descriptionForIDScanStatus:(enum ZoomIDScanStatus)status;

/** Returns a description string for a ZoomSDKStatus value */
- (NSString * _Nonnull)descriptionForSDKStatus:(enum ZoomSDKStatus)status;
@end

/** Represents the status of the SDK */
typedef NS_ENUM(NSInteger, ZoomSDKStatus) {
    /** Initialize was never attempted. */
    ZoomSDKStatusNeverInitialized = 0,
    /** The License provided was verified. */
    ZoomSDKStatusInitialized = 1,
    /** The Device License Key Identifier could not be verified due to connectivity issues on the user's device. */
    ZoomSDKStatusNetworkIssues = 2,
    /** The Device License Key Identifier provided was invalid. */
    ZoomSDKStatusInvalidDeviceLicenseKeyIdentifier = 3,
    /** This version of the ZoOm SDK is deprecated. */
    ZoomSDKStatusVersionDeprecated = 4,
    /** The Device License Key Identifier needs to be verified again. */
    ZoomSDKStatusOfflineSessionsExceeded = 5,
    /** An unknown error occurred. */
    ZoomSDKStatusUnknownError = 6,
    /** Device is locked out due to too many failures. */
    ZoomSDKStatusDeviceLockedOut = 7,
    /** Device is in landscape display orientation. ZoOm can only be used in portrait display orientation. */
    ZoomSDKStatusDeviceInLandscapeMode = 8,
    /** Device is in reverse portrait mode. ZoOm can only be used in portrait display orientation. */
    ZoomSDKStatusDeviceInReversePortraitMode = 9,
    /** License was expired, contained invalid text, or you are attempting to initialize in an App that is not specified in your license. */
    ZoomSDKStatusLicenseExpiredOrInvalid,
    /** The provided public encryption key is missing or invalid. */
    ZoomSDKStatusEncryptionKeyInvalid,
};

@protocol ZoomSessionResult;
@protocol ZoomIDScanResult;
enum ZoomSessionStatus : NSInteger;

/**
 Applications should implement this delegate to receive results from a ZoomSession UIViewController.
 */
@protocol ZoomSessionDelegate <NSObject>
/**
 This method will be called exactly once after the ZoOm Session has completed and when using the ZoomSession constructor with a ZoomFaceMapProcessor.
 */
@optional
- (void)onZoomSessionComplete NS_SWIFT_NAME(onZoomSessionComplete());
@end

/**
 ZoomFaceMapProcessorDelegate
 */
@protocol ZoomFaceMapProcessorDelegate <NSObject>
- (void)processZoomSessionResultWhileZoomWaits:(id<ZoomSessionResult> _Nonnull)zoomSessionResult zoomFaceMapResultCallback:(id<ZoomFaceMapResultCallback> _Nonnull)zoomFaceMapResultCallback NS_SWIFT_NAME(processZoomSessionResultWhileZoomWaits(zoomSessionResult:zoomFaceMapResultCallback:));
@end

/**
 ZoomIDScanProcessorDelegate
 */
@protocol ZoomIDScanProcessorDelegate <NSObject>
- (void)processZoomIDScanResultWhileZoomWaits:(id<ZoomIDScanResult> _Nonnull)zoomIDScanResult zoomIDScanResultCallback:(id<ZoomIDScanResultCallback> _Nonnull)zoomIDScanResultCallback NS_SWIFT_NAME(processZoomIDScanResultWhileZoomWaits(zoomIDScanResult:zoomIDScanResultCallback:));
@end


/**
 ZoomFaceMapResultCallback
 */
@protocol ZoomFaceMapResultCallback <NSObject>
- (void)onFaceMapUploadMessageOverride:(NSMutableAttributedString * _Nonnull)uploadMessageOverride NS_SWIFT_NAME(onFaceMapUploadMessageOverride(uploadMessageOverride:));
- (void)onFaceMapUploadProgress:(float)uploadedPercent NS_SWIFT_NAME(onFaceMapUploadProgress(uploadedPercent:));
- (void)onFaceMapResultSucceed NS_SWIFT_NAME(onFaceMapResultSucceed());
- (void)onFaceMapResultSucceedWithIDScanNextStep:(enum ZoomIDScanNextStep)idScanNextStep NS_SWIFT_NAME(onFaceMapResultSucceed(idScanNextStep:));
- (void)onFaceMapResultRetry NS_SWIFT_NAME(onFaceMapResultRetry());
- (void)onFaceMapResultCancel NS_SWIFT_NAME(onFaceMapResultCancel());
@end

/**
 ZoomIDScanResultCallback
 */
@protocol ZoomIDScanResultCallback <NSObject>
- (void)onIDScanUploadMessageOverride:(NSMutableAttributedString * _Nonnull)uploadMessageOverride NS_SWIFT_NAME(onIDScanUploadMessageOverride(uploadMessageOverride:));
- (void)onIDScanUploadProgress:(float)uploadedPercent NS_SWIFT_NAME(onIDScanUploadProgress(uploadedPercent:));
- (void)onIDScanResultSucceed NS_SWIFT_NAME(onIDScanResultSucceed());
- (void)onIDScanResultRetry:(enum ZoomIDScanRetryMode)retryMode NS_SWIFT_NAME(onIDScanResultRetry(retryMode:));
- (void)onIDScanResultRetry:(enum ZoomIDScanRetryMode)retryMode unsuccessMessage:(NSString * _Nullable)message NS_SWIFT_NAME(onIDScanResultRetry(retryMode:unsuccessMessage:));
- (void)onIDScanResultCancel NS_SWIFT_NAME(onIDScanResultCancel());
@end

/** Represents results of a Zoom Session Request */
@protocol ZoomSessionResult <NSObject>
/** Indicates whether the ZoOm Session was completed successfully or the cause of the unsuccess. */
@property (nonatomic, readonly) enum ZoomSessionStatus status;
/** Metrics collected during the ZoOm Session. */
@property (nonatomic, readonly, strong) id<ZoomFaceBiometricMetrics> _Nullable faceMetrics;
/** Number of full sessions (both retry and success) that the user performed from the time ZoOm was invoked to the time control is handed back to the application. */
@property (nonatomic, readonly) NSInteger countOfZoomSessionsPerformed;
/** Unique id for a ZoOm Session. */
@property (nonatomic, readonly, copy) NSString * _Nonnull sessionId;
@end

/** Represents results of a Zoom ID Scan */
@protocol ZoomIDScanResult <NSObject>
/** Indicates whether the ID Scan succeeded or the cause of failure. */
@property (nonatomic, readonly) enum ZoomIDScanStatus status;
/** Indicates the ID type. */
@property (nonatomic, readonly) enum ZoomIDType idType;
/** ID Scan Metrics */
@property (nonatomic, readonly, strong) id<ZoomIDScanMetrics> _Nullable idScanMetrics;
@end

/** Represents the various end states of a ZoOm Session */
typedef NS_ENUM(NSInteger, ZoomSessionStatus) {
    /**
     The ZoOm Session was performed successfully and a FaceMap was generated.  Pass the FaceMap to ZoOm Server for further processing.
     */
    ZoomSessionStatusSessionCompletedSuccessfully,
    /**
     The ZoOm Session was not performed successfully and a FaceMap was not generated.  In general, other statuses will be sent to the developer for specific unsuccess reasons.
     */
    ZoomSessionStatusSessionUnsuccessful,
    /**
     The user pressed the cancel button and did not complete the ZoOm Session.
     */
    ZoomSessionStatusUserCancelled,
    /**
     This status will never be returned in a properly configured or production app.
     This status is returned if your license is invalid or network connectivity issues occur during a session when the application is not in production.
     */
    ZoomSessionStatusNonProductionModeLicenseInvalid,
    /**
     The camera access is prevented because either the user has explicitly denied permission or the user's device is configured to not allow access by a device policy.
     For more information on restricted by policy case, please see the the Apple Developer documentation on AVAuthorizationStatus.restricted.
     */
    ZoomSessionStatusCameraPermissionDenied,
    /**
     The ZoOm Session was cancelled due to the app being terminated, put to sleep, an OS notification, or the app was placed in the background.
     */
    ZoomSessionStatusContextSwitch,
    /**
     The ZoOm Session was cancelled because device is in landscape mode.
     The user experience of devices in these orientations is poor and thus portrait is required.
     */
    ZoomSessionStatusLandscapeModeNotAllowed,
    /**
     The ZoOm Session was cancelled because device is in reverse portrait mode.
     The user experience of devices in these orientations is poor and thus portrait is required.
     */
    ZoomSessionStatusReversePortraitNotAllowed,
    /**
     The ZoOm Session was cancelled because the user was unable to complete a ZoOm Session in the default allotted time or the timeout set by the developer.
     */
    ZoomSessionStatusTimeout,
    /**
     The ZoOm Session was cancelled due to memory pressure.
     */
    ZoomSessionStatusLowMemory,
    /**
     The ZoOm Session was cancelled because your App is not in production and requires a network connection.
     */
    ZoomSessionStatusNonProductionModeNetworkRequired,
    /**
     The ZoOm Session was cancelled because your License needs to be validated again.
     */
    ZoomSessionStatusGracePeriodExceeded,
    /**
     The ZoOm Session was cancelled because the developer-configured encryption key was not valid.
     */
    ZoomSessionStatusEncryptionKeyInvalid,
    /**
     The ZoOm Session was cancelled because not all guidance images were configured.
     */
     ZoomSessionStatusMissingGuidanceImages,
    /**
     The ZoOm Session was cancelled because ZoOm was unable to start the camera on this device.
     */
     ZoomSessionStatusCameraInitializationIssue,
    /**
     The ZoOm Session was cancelled because the user was in a locked out state.
     */
    ZoomSessionStatusLockedOut,
    /**
     The ZoOm Session was cancelled because of an unknown and unexpected error.  ZoOm leverages a variety of iOS APIs including camera, storage, security, networking, and more.
     This return value is a catch-all for errors experienced during normal usage of these APIs.
     */
    ZoomSessionStatusUnknownInternalError,
    /**
     The ZoOm Session cancelled because user pressed the Get Ready screen subtext message.
     Note: This functionality is not available by default, and must be requested from FaceTec in order to enable.
     */
    ZoomSessionStatusUserCancelledViaClickableReadyScreenSubtext
};

/** Represents the various end states of an ID Scan Session */
typedef NS_ENUM(NSInteger, ZoomIDScanStatus) {
    /**
     The ID Scan was performed successfully and identity document data was generated.
     */
    ZoomIDScanStatusSuccess,
    /**
     The ID Scan was not performed successfully and identity document data was not generated.
     In general, other statuses will be sent to the developer for specific unsuccess reasons.
     */
    ZoomIDScanStatusUnsuccess,
    /**
     The user pressed the cancel button and did not complete the ID Scan process.
     */
    ZoomIDScanStatusUserCancelled,
    /**
     The ID Scan was cancelled because the user was unable to complete an ID Scan in the default allotted time or the timeout set by the developer.
     */
    ZoomIDScanStatusTimedOut,
    /**
     The ID Scan was cancelled due to the app being terminated, put to sleep, an OS notification, or the app was placed in the background.
     */
    ZoomIDScanStatusContextSwitch,
    /**
     The ID Scan was cancelled due to an internal camera error.
     */
    ZoomIDScanStatusCameraError,
    /**
     The ID Scan was cancelled because a network connection is required.
     */
    ZoomIDScanStatusNetworkError,
    /**
     ID Scan cancelled because device is in landscape mode.
     The user experience of devices in these orientations is poor and thus portrait is required.
     */
    ZoomIDScanStatusLandscapeModeNotAllowed,
    /**
     ID Scan cancelled because device is in reverse portrait mode.
     The user experience of devices in these orientations is poor and thus portrait is required.
     */
    ZoomIDScanStatusReversePortraitNotAllowed,
    /**
     ID Scan was skipped.
     */
    ZoomIDScanStatusSkipped
};

/** Represents the type of identity document for ID Scan */
typedef NS_ENUM(NSInteger, ZoomIDType) {
    /**
     ID card type
     */
    ZoomIDTypeIDCard,
    /**
     Passport type
     */
    ZoomIDTypePassport,
    /**
     ID type was not selected so it is unknown
     */
    ZoomIDTypeNotSelected
};

/** Represents the optionals available for retrying part or all of the ID Scan process */
typedef NS_ENUM(NSInteger, ZoomIDScanRetryMode) {
    ZoomIDScanRetryModeFront,
    ZoomIDScanRetryModeBack,
    ZoomIDScanRetryModeFrontAndBack
};

/**
 Describes the next step to go into during the Photo ID Match process. By default, when ZoomFaceMapResultCallback.onFaceMapResultSucceed() is called, the User is taken to the ID Document Type Selection Screen. Passing different values of ZoomIDScanNextStep as a parameter for ZoomFaceMapResultCallback.succeed() allows you to control whether you want to skip directly to either the ID Card (with Front and Back) capture process, or the Passport capture process, or to  skip the ID Scan process altogether. You may want to skip directly to a specific type of ID Scan if you know that your Users are only using one particular type of ID. You may want to skip the ID Scan process altogether if you have custom server-side logic that in some cases deems the Photo ID Match flow as not necessary.
*/
typedef NS_ENUM(NSInteger, ZoomIDScanNextStep) {
    /**
     Start ID Scan process with showing the Selection Screen, allowing the user to select their ID document type.
     This is the default behavior.
     */
    ZoomIDScanNextStepSelectionScreen,
    /**
     Start ID Scan process with the Capture Screen, pre-configured for an ID card document-type, skipping the Selection Screen.
     */
    ZoomIDScanNextStepSelectIDCard,
    /**
     Start ID Scan process with the Capture Screen, pre-configured for a passport document-type, skipping the Selection Screen.
     */
    ZoomIDScanNextStepSelectPassport,
    /**
     Skip the entire ID Scan process, exiting from the ZoOm SDK interface after a successful ZoOm Session.
     */
    ZoomIDScanNextStepSkip
};
