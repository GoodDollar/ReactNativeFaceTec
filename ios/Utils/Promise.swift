//
//  RCTUtils.swift
//  FaceTecVerificationNative
//
//  Created by Alex Serdukov on 5/13/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import FaceTecSDK

class Promise: PromiseDelegate {
    private var resolver: RCTPromiseResolveBlock
    private var rejecter: RCTPromiseRejectBlock
    
    init(resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        self.resolver = resolver
        self.rejecter = rejecter
    }
    
    func resolve(_ result: Any?) -> Void {
        resolver(result)
    }
    
    func reject(_ status: FaceTecSessionStatus, _ customMessage: String? = nil) -> Void {
        rejectWith(customMessage ?? FaceTec.sdk.description(for: status), status.rawValue)
    }
    
    func reject(_ status: FaceTecSDKStatus, _ customMessage: String? = nil) -> Void {
        rejectWith(customMessage ?? FaceTec.sdk.description(for: status), status.rawValue)
    }
    
    private func rejectWith(_ message: String, _ code: Int) -> Void {
        let exception = NSError(domain: message, code: code)

        rejecter(String(code), message, exception)
    }
}
