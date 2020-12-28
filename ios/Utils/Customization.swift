//
//  Customization.swift
//  FaceTec
//
//  Created by Alex Serdukov on 22.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import UIKit
import FaceTecSDK

final class Customization {
    public static let resultSuccessMessage = "You’re a beautiful\n& unique unicorn!"
    public static let resultFacescanUploadMessage = "Uploading Your face\nsnapshot to verify";
    public static let resultFacescanProcessingMessage = "Verifying you're\none of a kind";
        
    private static let faceTecNS: String = "FaceTec"
    
    private static let black = UIColor.black
    private static let white = UIColor.white
    private static let green = UIColor.green
    private static let darkGray = UIColor.darkGray
    private static let lightGray = UIColor.lightGray
    
    private static let primary = UIColor("#00afff")
    private static let gray50Percent = UIColor("#cbcbcb")
    
    private static let defaultCornerRadius: Int = 5
    private static lazy var defaultFont: UIFont = {
        let defaultSize = 12
        let font = UIFont.init(name: family, size: defaultSize)
        
        if font == nil {
            return UIFont.systemFont(ofSize: size)
        }
        
        return font
    }()
    
    private(set) static lazy var UICustomization: FaceTecCustomization = {
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
        overlay.backgroundColor = FaceTecColor(white, 0.5)
        
        // setting custom location & image of cancel button
        cancelButton.location = .topRight
        cancelButton.customImage = FaceTecImage("cancel")
        
        // configuring feedback bar typography & border radius
        feedback.backgroundColor = primary
        feedback.cornerRadius = defaultCornerRadius
        feedback.textColor = white
        feedback.textFont = FaceTecFont(defaultFont, 24)
        // TODO: bold font
        
        // setting oval border color & width
        oval.strokeWidth = 6
        oval.strokeColor = primary
        oval.progressColor1 = green
        oval.progressColor2 = green
        
        // frame (zoom's popup) customizations
        
        // setting frame border, radius & shadow
        frame.borderColor = FaceTecColor(white, 0)
        frame.cornerRadius = defaultCornerRadius
        frame.borderWidth = 0
        //TODO: shadow
        
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
        guidance.headerFont = FaceTecFont(defaultFont, 22)
        // TODO: set 20 for small screens
        // TODO: set medium font weight
        
        // subtext
        guidance.subtextFont = defaultFont
        
        // enabling additional instructions on retry screen
        guidance.enableRetryScreenBulletedInstructions = true
        
        // configuring guidance images on retry screen
        guidance.retryScreenOvalStrokeColor = primary
        guidance.retryScreenImageBorderColor = primary
        guidance.retryScreenImageBorderWidth = 4
        guidance.retryScreenImageCornerRadius = defaultCornerRadius
        
        // customizing result screen - progress bar & success animation
        resultScreen.foregroundColor = darkGray
        resultScreen.messageFont = FaceTecFont(defaultFont, 16)
        resultScreen.messageTextSpacing = 0.08
        resultScreen.showUploadProgressBar = true
        resultScreen.uploadProgressFillColor = primary
        resultScreen.uploadProgressTrackColor = lightGray
        resultScreen.resultAnimationBackgroundColor = white
        resultScreen.resultAnimationForegroundColor = primary
        // TODO: find Earth icon with pole side view (to be rotated clockwise, not by 3d)
        
        return ui
    }()
    
    private(set) static lazy var UITextStrings: [String: String] = {
        let i18n: [String: String] = [
            "resultSuccessMessage": resultSuccessMessage,
            "resultFacescanUploadMessage": resultFacescanUploadMessage,
            
            "retryInstructionMessage1": "Hold Your Camera at Eye Level.",
            "retryInstructionMessage2": "Light Your Face Evenly.",
            "retryInstructionMessage3": "Avoid Smiling & Back Light",
            
            "instructionsMessageReady": "Please Frame Your Face In The Small Oval, Then The Big Oval"
        ]
        
        return i18n.reduce(into: [String: String]()) { result, keyValue in
            result["\(faceTecNS)_\(keyValue.key.snakeCased())"] = keyValue.value
        }
    }()
    
    private static func FaceTecColor(_ color: UIColor, _ alpha: CGFloat? = nil) -> UIColor {
        return alpha == nil ? alpha : color.withAlphaComponent(alpha)
    }
    
    private static func FaceTecBackground(_ color: UIColor, _ alpha: CGFloat? = nil) -> CAGradientLayer {
        let backgroundLayer = CAGradientLayer.init()
        let bgColor = FaceTecColor(color, alpha)
        
        backgroundLayer.colors = [bgColor.cgColor, bgColor.cgColor]
        backgroundLayer.locations = [0, 1]
        backgroundLayer.startPoint = CGPoint.init(x: 0, y: 0)
        backgroundLayer.endPoint = CGPoint.init(x: 1, y: 0)
    }
    
    private static func FaceTecImage(_ name: String) -> UIImage {
        return UIImage(named: faceTecNS + name.capitalize())
    }
    
    private static func FaceTecFont(_ font: UIFont, _ size: CGFloat? = nil) -> UIFont {
        return size == nil ? font : font.withSize(size)
    }
}
