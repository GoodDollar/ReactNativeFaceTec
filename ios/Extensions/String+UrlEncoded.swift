//
//  String+UrlEncoded.swift
//  FaceTec
//
//  Created by Alex Serdukov on 25.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

// encodeURIComponent implementation in Swift
extension String {
    func urlEncoded() -> String {
        return self.addingPercentEncoding(withAllowedCharacters: NSMutableCharacterSet.urlQueryAllowed)!
    }
}
