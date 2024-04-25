//
//  String+Capitalize.swift
//  FaceTec
//
//  Created by Alex Serdukov on 28.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

// lodash-like capitalize
extension String {
    func capitalize() -> String {
        return prefix(1).capitalized + dropFirst()
    }
}
