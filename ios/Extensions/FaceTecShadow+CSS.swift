//
//  FaceTecShadow+CSS.swift
//  FaceTec
//
//  Created by Alex Serdukov on 29.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit
import FaceTecSDK

extension FaceTecShadow {
    static func css(boxShadow: [Int], _ color: UIColor, _ alpha: Float = 1) -> Self {
        let radius = boxShadow[2]
        let halfRadius = CGFloat(radius) / 2
        let offset = CGSize(width: boxShadow[0], height: boxShadow[1])
        
        let insets = UIEdgeInsets(
            top: -halfRadius + offset.height,
            left: -halfRadius + offset.width,
            bottom: -halfRadius - offset.height,
            right: -halfRadius - offset.width
        )
        
        return Self.init(color: color, opacity: alpha, radius: Float(radius), offset: offset, insets: insets)
    }
}
