//
//  Array+RawRepresentable.swift
//  FaceTec
//
//  Created by Alex Serdukov on 25.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

extension Dictionary where Value: RawRepresentable {
    func rawValues() -> [Self.Key: Value.RawValue] {
        return self.mapValues({ $0.rawValue })
    }
}
