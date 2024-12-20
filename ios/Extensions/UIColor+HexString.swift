//
//  UIColor+HexString.swift
//  FaceTec
//
//  Created by Alex Serdukov on 22.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

// html color to UIColor coverter
// used in customization
extension UIColor {
    convenience init(_ hexString: String) {
        var int = UInt64()
        let a, r, g, b: UInt64
        let hex = hexString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        let hexDigits = hex.count
        
        Scanner(string: hex).scanHexInt64(&int)
        
        switch hexDigits {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }        
        
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }
}
