//
//  EnrollmentProcessingDelegate.swift
//  FaceTec
//
//  Created by Alex Serdukov on 12/21/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import FaceTecSDK

class PromiseProcessingDelegate: NSObject, ProcessingDelegate {
    private var promise: PromiseDelegate
    
    init(_ promise: PromiseDelegate) {
        self.promise = promise
    }
    
    func onProcessingComplete(isSuccess: Bool, sessionResult: FaceTecSessionResult?, sessionMessage: String?) {
        if isSuccess {
            promise.resolve(sessionMessage)
            return
        }
         
        let status = sessionResult?.status
         
        if status == nil {
            onSessionTokenError()
            return
        }
         
        promise.reject(status!, sessionMessage)
    }
    
    func onSessionTokenError() {
        let message = FaceVerificationError.unexpectedResponse.message
        
        promise.reject(FaceTecSessionStatus.unknownInternalError, message)
    }
    
    func onCameraAccessError() {
        promise.reject(FaceTecSessionStatus.cameraPermissionDenied)
    }
}
