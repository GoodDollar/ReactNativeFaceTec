//
//  CAGradientLayer+SolidFill.swift
//  FaceTec
//
//  Created by Alex Serdukov on 29.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit

extension CAGradientLayer {
    static func solidFill(color: UIColor, alpha: CGFloat = 1) -> Self {
        let layer = Self.init()
        let bgColor = color.withAlphaComponent(alpha)
        
        layer.colors = [bgColor.cgColor, bgColor.cgColor]
        layer.locations = [0, 1]
        layer.startPoint = CGPoint.init(x: 0, y: 0)
        layer.endPoint = CGPoint.init(x: 1, y: 0)
        
        return layer
    }
}
