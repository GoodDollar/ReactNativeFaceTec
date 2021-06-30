//
//  FaceTec.swift
//
//  Created by Alex Serdukov on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit
import FaceTecSDK
import LocalAuthentication

@objc(FaceTecModule)
open class FaceTecModule: RCTEventEmitter {
    override static public func requiresMainQueueSetup() -> Bool {
        return true
    }

    override public init() {
        super.init()

        FaceTec.sdk.setCustomization(Customization.UICustomization)
        EventEmitter.shared.register(withRCTEventEmitter: self)
    }

    @objc
    override open func supportedEvents() -> [String]! {
        return UXEvent.allCases.map({ $0.rawValue })
    }

    @objc
    override open func startObserving() {
        EventEmitter.shared.restore()
    }

    @objc
    override open func stopObserving() {
        EventEmitter.shared.suspend()
    }

    @objc
    override open func constantsToExport() -> [AnyHashable : Any]! {
        let sdkStatuses: [String: FaceTecSDKStatus] = [
            // common statuses (status names are aligned with the web sdk)
            "NeverInitialized": .neverInitialized,
            "Initialized": .initialized,
            "NetworkIssues": .networkIssues,
            "InvalidDeviceKeyIdentifier": .invalidDeviceKeyIdentifier,
            "VersionDeprecated": .versionDeprecated,
            "DeviceNotSupported": .unknownError,
            "DeviceInLandscapeMode": .deviceInLandscapeMode,
            "DeviceInReversePortraitMode": .deviceInReversePortraitMode,
            "DeviceLockedOut": .deviceLockedOut,
            "KeyExpiredOrInvalid": .keyExpiredOrInvalid,

            // native-specific statuses
            "EncryptionKeyInvalid": .encryptionKeyInvalid,
            "OfflineSessionsExceeded": .offlineSessionsExceeded
        ]

        let sessionStatuses: [String: FaceTecSessionStatus] = [
            // common statuses (status names are aligned with the web sdk)
            "SessionCompletedSuccessfully": .sessionCompletedSuccessfully,
            "MissingGuidanceImages": .missingGuidanceImages,
            "Timeout": .timeout,
            "ContextSwitch": .contextSwitch,
            "ProgrammaticallyCancelled": .sessionUnsuccessful,
            "OrientationChangeDuringSession": .reversePortraitNotAllowed,
            "LandscapeModeNotAllowed": .landscapeModeNotAllowed,
            "UserCancelled": .userCancelled,
            "UserCancelledWhenAttemptingToGetCameraPermissions": .cameraPermissionDenied,
            "CameraNotEnabled": .cameraInitializationIssue,
            "LockedOut": .lockedOut,
            "NonProductionModeDeviceKeyIdentifierInvalid": .nonProductionModeKeyInvalid,
            "UnknownInternalError": .unknownInternalError,
            "UserCancelledViaClickableReadyScreenSubtext": .userCancelledViaClickableReadyScreenSubtext,

            // native-specific statuses
            "LowMemory": .lowMemory,
            "NetworkRequired": .nonProductionModeNetworkRequired,
            "GracePeriodExceeded": .gracePeriodExceeded,
            "EncryptionKeyInvalid": .encryptionKeyInvalid
        ]

        let uxEvents = UXEvent.allCases.reduce(into: [String: UXEvent]()) {
            $0[String(describing: $1)] = $1
        }

        return [
            "FaceTecUxEvent": uxEvents.rawValues(),
            "FaceTecSDKStatus": sdkStatuses.rawValues(),
            "FaceTecSessionStatus": sessionStatuses.rawValues()
        ]
    }

    @objc(initializeSDK:jwtAccessToken:licenseKey:encryptionKey:licenseText:resolver:rejecter:)
    open func initializeSDK(_ serverURL: String, jwtAccessToken: String,
        licenseKey: String, encryptionKey: String, licenseText: String? = nil,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let promise = Promise(resolve, reject)

        getSDKStatus() { sdkStatus in
            switch sdkStatus {
            case .initialized, .deviceInLandscapeMode, .deviceInReversePortraitMode:
                FaceTec.sdk.setDynamicStrings(Customization.UITextStrings)
                promise.resolve(true)
            case .neverInitialized, .networkIssues:
                FaceVerification.shared.register(serverURL, jwtAccessToken)
                /*
                   TODO Clean this up task: https://www.notion.so/chippercash/Facetec-pass-down-public-key-to-native-code-cef7d7c00ef24aadbebf296d83c16a48
                   Diff from fork, passing this down from the Javascript in encryptionKey was causing errors.
                   I'm assuming something is going wrong with the new lines and JS to Swift Strings,
                   for now it's hard coded below.
                */
                 let PublicFaceScanEncryptionKey =
                        "-----BEGIN PUBLIC KEY-----\n" +
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5PxZ3DLj+zP6T6HFgzzk\n" +
                        "M77LdzP3fojBoLasw7EfzvLMnJNUlyRb5m8e5QyyJxI+wRjsALHvFgLzGwxM8ehz\n" +
                        "DqqBZed+f4w33GgQXFZOS4AOvyPbALgCYoLehigLAbbCNTkeY5RDcmmSI/sbp+s6\n" +
                        "mAiAKKvCdIqe17bltZ/rfEoL3gPKEfLXeN549LTj3XBp0hvG4loQ6eC1E1tRzSkf\n" +
                        "GJD4GIVvR+j12gXAaftj3ahfYxioBH7F7HQxzmWkwDyn3bqU54eaiB7f0ftsPpWM\n" +
                        "ceUaqkL2DZUvgN0efEJjnWy5y1/Gkq5GGWCROI9XG/SwXJ30BbVUehTbVcD70+ZF\n" +
                        "8QIDAQAB\n" +
                        "-----END PUBLIC KEY-----"

                if !licenseText.isEmptyOrNil {
                    FaceTec.sdk.initializeInProductionMode(
                        productionKeyText: licenseText!,
                        deviceKeyIdentifier: licenseKey,
                        // Diff from fork, previously this used the encryptionKey passed down from the JavaScript code.
                        faceScanEncryptionKey: PublicFaceScanEncryptionKey
                    ) { success in self.onInitializationAttempt(promise, success) }

                    return
                }

                FaceTec.sdk.initializeInDevelopmentMode(
                    deviceKeyIdentifier: licenseKey,
                    faceScanEncryptionKey: encryptionKey
                ) { success in self.onInitializationAttempt(promise, success) }
            default:
                promise.reject(sdkStatus)
            }
        }
    }

    @objc(faceVerification:maxRetries:timeout:resolver:rejecter:)
    open func faceVerification(
        _ enrollmentIdentifier: String, maxRetries: Int, timeout: Int,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let promise = Promise(resolve, reject)
        let delegate = PromiseProcessingDelegate(promise)

        getPresentedViewController() { presentedVC in
            let processor = EnrollmentProcessor(fromVC: presentedVC, delegate: delegate)

            processor.enroll(enrollmentIdentifier, maxRetries, timeout)
        }
    }

    private func onInitializationAttempt(
        _ promise: PromiseDelegate,
        _ initializationSuccessful: Bool
    ) -> Void {
        if initializationSuccessful {
            FaceTec.sdk.setDynamicStrings(Customization.UITextStrings)
            promise.resolve(initializationSuccessful)
            return
        }

        getSDKStatus() { sdkStatus in
            var customMessage: String? = nil

            if .neverInitialized == sdkStatus {
                customMessage = """
                Initialize wasn't attempted as Simulator has been detected. \
                FaceTec SDK could be ran on the real devices only
                """
            }

            promise.reject(sdkStatus, customMessage)
        }
    }

    private func getSDKStatus(completion: @escaping (FaceTecSDKStatus) -> Void) -> Void {
        DispatchQueue.main.async {
            completion(FaceTec.sdk.getStatus())
        }
    }

    private func getPresentedViewController(completion: @escaping (UIViewController) -> Void) -> Void {
        DispatchQueue.main.async {
            completion(RCTPresentedViewController()!)
        }
    }
}
