//
//  Optional+Collection.swift
//  FaceTec
//
//  Created by Alex Serdukov on 30.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

extension Optional where Wrapped: Collection {
    var isEmptyOrNil: Bool {
        return self?.isEmpty ?? true
    }
}
