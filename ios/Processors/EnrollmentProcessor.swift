import UIKit
import Foundation
import FaceTecSDK

class EnrollmentProcessor: NSObject {
    /*, URLSessionDelegate, FaceTecFaceMapProcessorDelegate, FaceTecSessionDelegate*/ 
    /*var faceTecFaceMapResultCallback: FaceTecFaceMapResultCallback!
    var latestFaceTecSessionResult: FaceTecSessionResult?
    var latestFaceTecSessionMessage: String?
    var latestEnrollmentIdentidier: String!
    var latestJWTAccessToken: String!
    var isSuccess = false*/

    var delegate: ProcessingDelegate
    var presentSessionVCFrom: UIViewController

    init(fromVC: UIViewController, delegate: ProcessingDelegate) {
        self.delegate = delegate
        self.presentSessionVCFrom = fromVC

        super.init()
    }

    func enroll(_ enrollmentIdentifier: String, _ maxRetries: Int?) {
        EventEmitter.shared.dispatch(UXEvent.UI_READY)
        
        delegate.onProcessingComplete(isSuccess: true, sessionResult: nil, sessionMessage: Customization.resultSuccessMessage)
    }

    /*
    func enroll(_ enrollmentIdentifier: String, _ jwtAccessToken: String) {
        startSession() { (sessionToken) in
            // When session token retrieved - setting latest id and jwt
            self.latestJWTAccessToken = jwtAccessToken
            self.latestEnrollmentIdentidier = enrollmentIdentifier

            // request camera accesses (should do it before showing FaceTec UI to avoid FaceTec's camera access screen
            // as it couldn't be disabled on native like on the web SDK
            requestCameraPermissions() {
                // Launch the FaceTec Session.
                let sessionVC = FaceTec.sdk.createSessionVC(delegate: self, faceMapProcessorDelegate: self, serverSessionToken: serverSessionToken)

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

    // Required function that handles calling FaceTec Server to get result and decides how to continue.
    func processFaceTecSessionResultWhileFaceTecWaits(faceTecSessionResult: FaceTecSessionResult, faceTecFaceMapResultCallback: FaceTecFaceMapResultCallback) {
        self.latestFaceTecSessionResult = faceTecSessionResult
        self.faceTecFaceMapResultCallback = faceTecFaceMapResultCallback

        // cancellation, timeout, etc.
        if faceTecSessionResult.status != .sessionCompletedSuccessfully || faceTecSessionResult.faceMetrics?.faceMap == nil {
            faceTecFaceMapResultCallback.onFaceMapResultCancel();
            return
        }

        // notifying that capturing is done
        EventEmitter.shared.dispatch(.CAPTURE_DONE)

        // setting initial progress to 0 for freeze progress bar
        faceTecFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: 0)

        // Create and parse request to GoodServer.
        NetworkingHelpers.getEnrollmentResponseFromGoodServer(
            enrollmentIdentifier: latestEnrollmentIdentidier,
            faceTecSessionResult: faceTecSessionResult,
            jwtAccessToken: latestJWTAccessToken,
            urlSessionDelegate: self
        ) { (nextStep, lastMessage, lastResult) in
            // last 20% progress bar will stuck in 'almost completed' state
            // white GoodServer will process uploaded FaceMap
            faceTecFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: 1)

            // Storing the last enrollment message.
            self.latestFaceTecSessionMessage = lastMessage

            // Dynamically set the message in the UI
            if lastMessage != nil {
                FaceTecCustomization.setOverrideResultScreenSuccessMessage(lastMessage!)
            }

            switch nextStep {
            case .Succeed:
                faceTecFaceMapResultCallback.onFaceMapResultSucceed()
                self.isSuccess = true
            case .Retry:
                EventEmitter.shared.dispatch(.FV_RETRY, [
                    "reason": lastMessage,
                    "liveness": lastResult?["isLive"],
                    "enroller": lastResult?["isEnrolled"],
                ] as NSDictionary)

                faceTecFaceMapResultCallback.onFaceMapResultRetry()
            case .Cancel:
                faceTecFaceMapResultCallback.onFaceMapResultCancel()
            }
        }
    }

    // iOS way to get upload progress and update FaceTec UI.
    func urlSession(_ session: URLSession, task: URLSessionTask,
                    didSendBodyData bytesSent: Int64, totalBytesSent: Int64, totalBytesExpectedToSend: Int64
        ) {
        // handling URLSession upload progress from 10 to 80%
        let uploadProgress: Float = 0.1 + 0.7 * Float(totalBytesSent) / Float(totalBytesExpectedToSend)

        // faceTecFaceMapResultCallback.onFaceMapUploadProgress(uploadedPercent: uploadProgress)
    }

    
    // The final callback FaceTec SDK calls when done with everything.
    func onFaceTecSessionComplete() {
        delegate.onProcessingComplete(isSuccess: isSuccess,
                                      faceTecSessionResult: latestFaceTecSessionResult,
                                      faceTecSessionMessage: latestFaceTecSessionMessage
        )
    }*/
}
