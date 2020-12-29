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
    static func css(boxShadow: [Int], _ color: UIColor, _ alpha: CGFloat = 1) -> Self {
        let radius = boxShadow[2]
        let halfRadius = radius / 2
        let offset = CGSize(width: boxShadow[0], height: boxShadow[1])
        
        let insets = UIEdgeInsets(
            top: -halfRadius + offset.height,
            left: -halfRadius + offset.width,
            bottom: -halfRadius - offset.height,
            right: -halfRadius - offset.width
        )
        
        return self.init(color: color, opacity: alpha, radius: radius, offset: offset, insets: insets)
    }
}
