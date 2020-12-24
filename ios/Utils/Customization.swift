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
    public static let resultFacescanUploadMessage = "Uploading Your face snapshot to verify";
    public static let resultFacescanProcessingMessage = "Verifying you're\none of a kind";
        
    // TODO: define color constants from GD theme (e.g. lightBlue800, primary, green, gray50Percent, darkGray etc)
    // some of them are defined as UIColor class vars (e.g. white)
    private static let faceTecNS: String = "FaceTec"
    private static let primary: UIColor = UIColor("#00AFFF")
    
    private(set) static var UICustomization: FaceTecCustomization = {
        let ui = FaceTecCustomization()
        let overlay = ui.overlayCustomization
        let feedback = ui.feedbackCustomization
        let backgroundLayer = CAGradientLayer.init()
        
        backgroundLayer.colors = [primary.cgColor, primary.cgColor]
        backgroundLayer.locations = [0,1]
        backgroundLayer.startPoint = CGPoint.init(x: 0, y: 0)
        backgroundLayer.endPoint = CGPoint.init(x: 1, y: 0)
        
        overlay.showBrandingImage = false
        overlay.backgroundColor = UIColor.white.withAlphaComponent(0.5)
        
        feedback.backgroundColor = backgroundLayer
        
        return ui
    }()
    
    private(set) static var UITextStrings: [String: String] = {
        let i18n: [String: String] = [
            "resultSuccessMessage": resultSuccessMessage,
            "resultFacescanUploadMessage": resultFacescanUploadMessage,
            
            "retryInstructionMessage1": "Hold Your Camera at Eye Level.",
            "retryInstructionMessage2": "Light Your Face Evenly.\nAvoid Smiling & Back Light",
            
            "instructionsMessageReady": "Please Frame Your Face In The Small\nOval, Then The Big Oval"
        ]
        
        return i18n.reduce(into: [String: String]()) { result, keyValue in
            result["\(faceTecNS)_\(keyValue.key.snakeCased())"] = keyValue.value
        }
    }()
}
