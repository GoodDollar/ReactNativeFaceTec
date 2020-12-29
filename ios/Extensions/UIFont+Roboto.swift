//
//  UIFont+Roboto.swift
//  FaceTec
//
//  Created by Alex Serdukov on 29.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit

extension UIFont {
    static func robotoFont(_ size: CGFloat) -> Self {
        return robotoFont(.regular, size)
    }

    static func robotoFont(_ weight: UIFont.Weight) -> Self {
        return robotoFont(weight, systemFontSize)
    }

    static func robotoFont(_ weight: UIFont.Weight, _ size: CGFloat) -> Self {
        return loadRobotoFont(weight: weight, size: size)
            ?? systemFont(ofSize: size, weight: weight) as! Self
    }

    fileprivate static func loadRobotoFont(weight: UIFont.Weight, size: CGFloat) -> Self? {
        let fontFamily = "Roboto"
        var fontName = fontFamily

        switch (weight) {
        case .medium:
            fontName += "-Medium"
        case .bold:
            fontName += "-Bold"
        default:
            break
        }

        return Self.init(name: fontName, size: size)
    }
}
