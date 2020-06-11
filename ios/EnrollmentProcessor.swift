import UIKit
import Foundation
import ZoomAuthentication

class EnrollmentProcessor: NSObject, URLSessionDelegate, ZoomFaceMapProcessorDelegate, ZoomSessionDelegate {
    var zoomFaceMapResultCallback: ZoomFaceMapResultCallback!
    var latestZoomSessionResult: ZoomSessionResult?
    var latestZoomSessionMessage: String?
    var latestEnrollmentIdentidier: String!
    var latestJWTAccessToken: String!
    var isSuccess = false
    
    var delegate: ProcessingDelegate
    var presentSessionVCFrom: UIViewController
    
    init(fromVC: UIViewController, delegate: ProcessingDelegate) {
        self.delegate = delegate
        self.presentSessionVCFrom = fromVC
        
        super.init()
    }
    
    func enroll(_ enrollmentIdentifier: String, _ jwtAccessToken: String) {
        startSession() { (sessionToken) in
            // When session token retrieved - setting latest id and jwt
            self.latestJWTAccessToken = jwtAccessToken
            self.latestEnrollmentIdentidier = enrollmentIdentifier
            
            // request camera accesses (should do it before showing Zoom UI to avoid Zoom's camera access screen
            // as it couldn't be disabled on native like on the web SDK
            requestCameraPermissions() {
                // Launch the ZoOm Session.
                let sessionVC = Zoom.sdk.createSessionVC(delegate: self, faceMapProcessorDelegate: self, serverSessionToken: serverSessionToken)
                
                self.presentSessionVCFrom.present(sessionVC, animated: true, completion: {
                    EventEmitter.shared.dispatch(.UI_READY)
                })
            }
        }
    }
    
    func startSession(sessionTokenCallback: @escaping (String) -> Void) {
        NetworkingHelpers.getSessionToken() { (sessionToken) in
            guard let serverSessionToken: String = sessionToken else {
                self.delegate.onSessionTokenError()
                return
            }
            
            sessionTokenCallback(sessionToken)
        }
    }
    
    func requestCameraPermissions(requestCallback: @escaping () -> Void) {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            requestCallback()
            
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { (granted) in
                if (granted) {
                    requestCallback()
                } else {
                    self.delegate.onCameraAccessError()
                }
            }
            
        case .denied,
             .restricted:
            delegate.onCameraAccessError()
        }
    }
    
    // Required function that handles calling ZoOm Server to get result and decides how to continue.
    func processZoomSessionResultWhileZoomWaits(zoomSessionResult: ZoomSessionResult, zoomFaceMapResultCallback: ZoomFaceMapResultCallback) {
        self.latestZoomSessionResult = zoomSessionResult
        self.zoomFaceMapResultCallback = zoomFaceMapResultCallback
        
        // cancellation, timeout, etc.
        if zoomSessionResult.status != .sessionCompletedSuccessfully || zoomSessionResult.faceMetrics?.faceMap == nil {
            zoomFaceMapResultCallback.onFaceMapResultCancel();
            return
        }
        
        // notidying that capturing is done
        EventEmitter.shared.dispatch(.CAPTURE_DONE)
        
        // setting initial progress to 0 for freeze progress bar
        zoomFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: 0)
        
        // Create and parse request to GoodServer.
        NetworkingHelpers.getEnrollmentResponseFromGoodServer(
            enrollmentIdentifier: latestEnrollmentIdentidier,
            zoomSessionResult: zoomSessionResult,
            jwtAccessToken: latestJWTAccessToken,
            urlSessionDelegate: self
        ) { (nextStep, lastMessage) in
            // last 20% progress bar will stuck in 'almost completed' state
            // white GoodServer will process uploaded FaceMap
            zoomFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: 1)
            
            // Storing the last enrollment message.
            self.latestZoomSessionMessage = lastMessage
            
            // Dynamically set the message in the UI
            if lastMessage != nil {
                ZoomCustomization.setOverrideResultScreenSuccessMessage(lastMessage!)
            }
            
            switch nextStep {
            case .Succeed:
                zoomFaceMapResultCallback.onFaceMapResultSucceed()
                self.isSuccess = true
            case .Retry:
                zoomFaceMapResultCallback.onFaceMapResultRetry()
            case .Cancel:
                zoomFaceMapResultCallback.onFaceMapResultCancel()
            }
        }
    }
    
    // iOS way to get upload progress and update ZoOm UI.
    func urlSession(_ session: URLSession, task: URLSessionTask,
                    didSendBodyData bytesSent: Int64, totalBytesSent: Int64, totalBytesExpectedToSend: Int64
        ) {
        // handling URLSession upload progress from 10 to 80%
        let uploadProgress: Float = 0.1 + 0.7 * Float(totalBytesSent) / Float(totalBytesExpectedToSend)
        
        zoomFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: uploadProgress)
    }
    
    // The final callback ZoOm SDK calls when done with everything.
    func onZoomSessionComplete() {
        delegate.onProcessingComplete(isSuccess: isSuccess,
                                      zoomSessionResult: latestZoomSessionResult,
                                      zoomSessionMessage: latestZoomSessionMessage
        )
    }
}
