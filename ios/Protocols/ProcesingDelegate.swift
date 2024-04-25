// Helpful interfaces and enums

// Processin protocol (interface) 
// The same as ProcessingSubscriber.java
// Implemented in PromiseProcessingDelegate

protocol ProcessingDelegate: AnyObject {
    func onProcessingComplete(isSuccess: Bool, sessionResult: FaceTecSessionResult?, sessionMessage: String?)
    func onSessionTokenError()
    func onCameraAccessError()
}
