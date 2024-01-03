//
//  Customization.swift
//  FaceTec
//
//  Created by Alex Serdukov on 22.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

final class Customization {
    public static let resultSuccessMessage = "You’re a beautiful\n& unique unicorn!"

    private static let isLargeDevice = UIScreen.main.bounds.width >= 395

    private static let black = UIColor.black
    private static let white = UIColor.white
    private static let green = UIColor.green
    private static let darkGray = UIColor.darkGray
    private static let lightGray = UIColor.lightGray

    private static let primary = UIColor("#00afff")
    private static let gray50Percent = UIColor("#cbcbcb")

    private static let defaultCornerRadius: Int32 = 5
    private static let defaultFont = UIFont.robotoFont(12)

    private static let defaultBundle: Bundle = {
        let podBundle = Bundle(for: Customization.self)
        let bundleUrl = podBundle.url(forResource: "FaceTec", withExtension: "bundle")!

        return Bundle(url: bundleUrl)!
    }()
  
    static let UICustomization = createBasicCustomization()

    static let LowLightModeCustomization: FaceTecCustomization = {
        let ui = createBasicCustomization()
        let guidance = ui.guidanceCustomization
        
        guidance.readyScreenHeaderTextColor = white
        guidance.readyScreenSubtextTextColor = white

        return ui
    }()
    
    static let DynamicModeCustomization: FaceTecCustomization = {
      let ui = createBasicCustomization()
      let guidance = ui.guidanceCustomization
      let cancelButton = ui.cancelButtonCustomization
      
      cancelButton.customImage = UIImage(named: "CancelWhite.png", in: defaultBundle, compatibleWith: nil)!
      guidance.readyScreenHeaderTextColor = black
      guidance.readyScreenSubtextTextColor = black

      return ui
    }()

    private static func createBasicCustomization() -> FaceTecCustomization {
      let ui = FaceTecCustomization()

      let oval = ui.ovalCustomization
      let frame = ui.frameCustomization
      let overlay = ui.overlayCustomization
      let feedback = ui.feedbackCustomization
      let guidance = ui.guidanceCustomization
      let resultScreen = ui.resultScreenCustomization
      let cancelButton = ui.cancelButtonCustomization

      // removing branding image from overlay
      overlay.showBrandingImage = false
      overlay.backgroundColor = white.withAlphaComponent(0.5)

      // setting custom location & image of cancel button
      cancelButton.location = .topRight
      cancelButton.customImage = UIImage(named: "Cancel.png", in: defaultBundle, compatibleWith: nil)!

      // configuring feedback bar typography & border radius
      feedback.backgroundColor = CAGradientLayer.solidFill(color: black)
      feedback.cornerRadius = defaultCornerRadius
      feedback.textColor = white
      feedback.textFont = UIFont.robotoFont(.bold, 24)

      // setting oval border color & width
      oval.strokeWidth = 6
      oval.strokeColor = primary
      oval.progressColor1 = green
      oval.progressColor2 = green

      // frame (zoom's popup) customizations
      // setting frame border, radius & shadow
      frame.borderColor = white.withAlphaComponent(0)
      frame.cornerRadius = defaultCornerRadius
      frame.borderWidth = 0
      frame.shadow = FaceTecShadow.css(boxShadow: [0, 19, 38, 0], black, 0.42)

      // setting Zoom UI background color
      frame.backgroundColor = white

      // guidance screens ("frame your face", "retry" etc) customizations
      // setting setting Zoom UI default text color
      guidance.foregroundColor = darkGray

      // customizing buttons
      guidance.buttonFont = defaultFont
      guidance.buttonBorderWidth = 0
      guidance.buttonCornerRadius = defaultCornerRadius
      guidance.buttonTextNormalColor = white
      guidance.buttonTextHighlightColor = white
      guidance.buttonTextDisabledColor = white
      guidance.buttonBackgroundNormalColor = primary
      guidance.buttonBackgroundHighlightColor = primary
      guidance.buttonBackgroundDisabledColor = primary

      // customizing header / subtext
      guidance.headerFont = UIFont.robotoFont(.medium, isLargeDevice ? 22 : 20)

      // subtext
      guidance.subtextFont = defaultFont

      // configuring guidance images on retry screen
      guidance.retryScreenOvalStrokeColor = primary
      guidance.retryScreenImageBorderColor = primary
      guidance.retryScreenImageBorderWidth = 4
      guidance.retryScreenImageCornerRadius = defaultCornerRadius

      // customizing result screen - progress bar & success animation
      resultScreen.foregroundColor = darkGray
      resultScreen.messageFont = defaultFont.withSize(16)
      resultScreen.showUploadProgressBar = true
      resultScreen.uploadProgressFillColor = primary
      resultScreen.uploadProgressTrackColor = lightGray
      resultScreen.resultAnimationBackgroundColor = white
      resultScreen.resultAnimationForegroundColor = primary
      resultScreen.customActivityIndicatorImage = UIImage(named: "ActivityIndicator.png", in: defaultBundle, compatibleWith: nil)!
      resultScreen.customActivityIndicatorRotationInterval = 3000

      return ui
    }
}
