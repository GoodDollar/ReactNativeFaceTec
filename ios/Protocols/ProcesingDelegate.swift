// Helpful interfaces and enums

protocol ProcessingDelegate: AnyObject {
    func onProcessingComplete(isSuccess: Bool, sessionResult: FaceTecSessionResult?, sessionMessage: String?)
    func onSessionTokenError()
    func onCameraAccessError()
}
