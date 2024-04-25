//
//  FaceTec.swift
//
//  Created by Alex Serdukov on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

// In total, native implementations are very similar
// The main flow and SDK interfaces are 100% identical
// So here i will comment only iOS specific things

import UIKit
import LocalAuthentication
import FaceTecSDK

@objc(FaceTecModule)
open class FaceTecModule: RCTEventEmitter {
    // feature flag required for FaceTec
    override static public func requiresMainQueueSetup() -> Bool {
        return true
    }

    // module (not SDK) initializer, same as `void initialize()` in FaceTecModule.java
    override public init() {
        super.init()

        FaceTec.sdk.setCustomization(Customization.UICustomization)
        FaceTec.sdk.setLowLightCustomization(Customization.LowLightModeCustomization)
        FaceTec.sdk.setDynamicDimmingCustomization(Customization.DynamicModeCustomization)
        EventEmitter.shared.register(withRCTEventEmitter: self)
    }

    @objc
    override open func supportedEvents() -> [String]! {
        return UXEvent.allCases.map({ $0.rawValue })
    }

    // EventEmitter optimization. In iOS ReactBridge calls startObserving when 
    // first event listener was added and stopObserving when last one was removed
    // So we couldn't dispatch events if nothing listens
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

    // a) @objc annotation exports method to the ObjC runtime, then it also requires to be linked to the RTCBridge at FaceTecBridge.m
    // b) Instead of the Promise class wrapping JS promises, in Swift/ObjC we receive resolve/reject callback functions
    @objc(initializeSDK:jwtAccessToken:licenseKey:encryptionKey:licenseText:resolver:rejecter:)
    open func initializeSDK(_ serverURL: String, jwtAccessToken: String,
        licenseKey: String, encryptionKey: String, licenseText: String? = nil,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let promise = Promise(resolve, reject)

        // Insead of passing "callback" class instance having onSuccess/onFailed methods
        // (or any other methods defined at the interface which "async" Java method accepts)
        // in Swift the @escaping callback are used. There are something like anonymous callback functions
        // something like 'do' blocks in Ruby or lambdas at C++11 and above
        getSDKStatus() { sdkStatus in
            switch sdkStatus {
            case .initialized, .deviceInLandscapeMode, .deviceInReversePortraitMode:
                FaceTec.sdk.setDynamicStrings(Customization.UITextStrings)
                promise.resolve(true)
            case .neverInitialized, .networkIssues:
                FaceVerification.shared.register(serverURL, jwtAccessToken)

                if !licenseText.isEmptyOrNil {
                    FaceTec.sdk.initializeInProductionMode(
                        productionKeyText: licenseText!,
                        deviceKeyIdentifier: licenseKey,
                        faceScanEncryptionKey: encryptionKey
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

    // iOS implementation is too old and does not support v2/v1 identifiers
    @objc(faceVerification:maxRetries:timeout:resolver:rejecter:)
    open func faceVerification(
        _ enrollmentIdentifier: String, maxRetries: Int, timeout: Int,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let promise = Promise(resolve, reject)
        let delegate = PromiseProcessingDelegate(promise) // in Swift/iOS any kind of sucscribers or any object 
        // implementing some interface required by another object are generally callaed "delegates"
        // so this name was used here instead of 'processing subscriber'

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

    // same as getCurrentActivity() in Android
    // to represent 'screens' Android apps are using Activity class
    // iOS apps - view controllers inherited from UIViewController
    // both ios + android SDKs initialize UI over the current 'screen'
    // so, iOS SDK method requires view contoller, Android one - activity
    private func getPresentedViewController(completion: @escaping (UIViewController) -> Void) -> Void {
        DispatchQueue.main.async {
            completion(RCTPresentedViewController()!)
        }
    }
}
