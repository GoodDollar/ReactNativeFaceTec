// Helpful interfaces and enums

// import FaceTecAuthentication

protocol ProcessingDelegate: class {
    func onProcessingComplete(isSuccess: Bool/*, faceTecSessionResult: FaceTecSessionResult?, faceTecSessionMessage: String?*/)
    func onSessionTokenError()
    func onCameraAccessError()
}
