//
//  UIFont+Roboto.swift
//  FaceTec
//
//  Created by Alex Serdukov on 29.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit
import MDFRobotoFontLoader

extension UIFont {
    fileprivate static lazy var robotoLoader = MDFRobotoFontLoader.sharedInstance()
    
    private(set) static lazy var Roboto: Self = robotoFont(systemFontSize)
    
    static func robotoFont(_ size: CGFloat) -> Self {
        return robotoFont(.regular, size)
    }
    
    static func robotoFont(_ weight: UIFont.Weight) -> Self {
        return robotoFont(weight, systemFontSize)
    }
    
    static func robotoFont(_ weight: UIFont.Weight, _ size: CGFloat) -> Self {
        return makeRobotoFont(weight: weight, size: size)
            ?? loadRobotoFontDynamically(weight: weight, size: size)
            ?? systemFont(ofSize: size, weight: weight)
    }
    
    fileprivate static func makeRobotoFont(weight: UIFont.Weight, size: CGFloat) -> Self? {
        let fontFamily = "Roboto"
        var fontName = fontFamily
        
        switch (weight) {
        case .medium:
            fontName += "-Medium"
        case .bold:
            fontName += "-Bold"
        }
        
        return Self.init(name: fontName, size: size)
    }
    
    fileprivate static func loadRobotoFontDynamically(weight: UIFont.Weight, size: CGFloat) -> Self? {
        switch (weight) {
        case .medium:
            return robotoLoader.mediumFontOfSize(size)
        case .bold:
            return robotoLoader.boldFontOfSize(size)
        default:
            return robotoLoader.regularFontOfSize(size)
        }
    }
}
