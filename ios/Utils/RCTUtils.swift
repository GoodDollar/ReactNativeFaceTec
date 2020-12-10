//
//  RCTUtils.swift
//  FaceTecVerificationNative
//
//  Created by Alex Serdukov on 5/13/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
// import FaceTecAuthentication

class FaceTecRCTPromiseProcessingDelegate: NSObject, ProcessingDelegate {

    private var resolver: RCTPromiseResolveBlock
    private var rejecter: RCTPromiseRejectBlock

    init(resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        self.resolver = resolver
        self.rejecter = rejecter
    }

    func onProcessingComplete(isSuccess: Bool/*, faceTecSessionResult: FaceTecSessionResult?, faceTecSessionMessage: String?*/) {
        resolver(faceTecSessionMessage)
        /*if isSuccess {
            resolver(faceTecSessionMessage)
            return
        }

        let status = faceTecSessionResult?.status

        if status == nil {
            onSessionTokenError()
            return
        }

        let message = faceTecSessionMessage ?? FaceTec.sdk.description(for: status!)

        FaceTecRCTUtils.rejectWith(message, status!.rawValue, rejecter: rejecter)*/
    }

    func onSessionTokenError() {
        let message = "Session could not be started due to an unexpected issue during the network request."

        FaceTecRCTUtils.rejectWith(message, -1/*FaceTecSessionStatus.unknownInternalError.rawValue*/, rejecter: rejecter)
    }

    func onCameraAccessError() {
        /*let noCameraAccess = FaceTecSessionStatus.cameraPermissionDenied
        let message = FaceTec.sdk.description(for: noCameraAccess)

        FaceTecRCTUtils.rejectWith(message, noCameraAccess.rawValue, rejecter: rejecter)*/
        FaceTecRCTUtils.rejectWith("Camera not accessible", 0, rejecter: rejecter)
    }
}

class FaceTecRCTUtils {
    static func rejectWith(_ message: String, _ code: Int, rejecter: RCTPromiseRejectBlock) -> Void {
        let exception = NSError(domain: message, code: code)

        rejecter(String(code), message, exception)
    }
}
