//
//  FaceTec.swift
//
//  Created by Alex Serdukov on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit
// import FaceTecAuthentication
// import LocalAuthentication

@objc(FaceTec)
open class FaceTec: RCTEventEmitter {
    override static public func moduleName() -> String! {
        return "FaceTec";
    }

    override static public func requiresMainQueueSetup() -> Bool {
        return true
    }

    override public init() {
        super.init()

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
        /*
        // should do it manually as FaceTecSDKStatus is an NS_ENUM so
        //    a) it doesn't implements CaseIterable
        //    b) we couldn't define all cases then map to the dictionary
        //       as NS_ENUM's doesn't supports enum case name to string conversion
        let sdkStatuses = [
            // common statuses (status names are aligned with the web sdk)
            "NeverInitialized": FaceTecSDKStatus.neverInitialized,
            "Initialized": FaceTecSDKStatus.initialized,
            "NetworkIssues": FaceTecSDKStatus.networkIssues,
            "InvalidDeviceLicenseKeyIdentifier": FaceTecSDKStatus.invalidDeviceLicenseKeyIdentifier,
            "VersionDeprecated": FaceTecSDKStatus.versionDeprecated,
            "DeviceNotSupported": FaceTecSDKStatus.unknownError,
            "DeviceInLandscapeMode": FaceTecSDKStatus.deviceInLandscapeMode,
            "DeviceInReversePortraitMode": FaceTecSDKStatus.deviceInReversePortraitMode,
            "DeviceLockedOut": FaceTecSDKStatus.deviceLockedOut,
            "LicenseExpiredOrInvalid": FaceTecSDKStatus.licenseExpiredOrInvalid,
            // native-specific statuses
            "EncryptionKeyInvalid": FaceTecSDKStatus.encryptionKeyInvalid,
            "OfflineSessionsExceeded": FaceTecSDKStatus.offlineSessionsExceeded,
        ]

        let sessionStatuses = [
            // common statuses (status names are aligned with the web sdk)
            "SessionCompletedSuccessfully": FaceTecSessionStatus.sessionCompletedSuccessfully,
            "MissingGuidanceImages": FaceTecSessionStatus.missingGuidanceImages,
            "NonProductionModeNetworkRequired": FaceTecSessionStatus.nonProductionModeNetworkRequired,
            "Timeout": FaceTecSessionStatus.timeout,
            "ContextSwitch": FaceTecSessionStatus.contextSwitch,
            "LandscapeModeNotAllowed": FaceTecSessionStatus.landscapeModeNotAllowed,
            "ReversePortraitNotAllowed": FaceTecSessionStatus.reversePortraitNotAllowed,
            "UserCancelled": FaceTecSessionStatus.userCancelled,
            "UserCancelledViaClickableReadyScreenSubtext": FaceTecSessionStatus.userCancelledViaClickableReadyScreenSubtext,
            "UserCancelledWhenAttemptingToGetCameraPermissions": FaceTecSessionStatus.cameraPermissionDenied,
            "LockedOut": FaceTecSessionStatus.lockedOut,
            "NonProductionModeLicenseInvalid": FaceTecSessionStatus.nonProductionModeLicenseInvalid,
            "UnmanagedSessionVideoInitializationNotCompleted": FaceTecSessionStatus.cameraInitializationIssue,
            "UnknownInternalError": FaceTecSessionStatus.unknownInternalError,
            // native-specific statuses
            "SessionUnsuccessful": FaceTecSessionStatus.sessionUnsuccessful,
            "LowMemory": FaceTecSessionStatus.lowMemory,
            "GracePeriodExceeded": FaceTecSessionStatus.gracePeriodExceeded,
            "EncryptionKeyInvalid": FaceTecSessionStatus.encryptionKeyInvalid,
        ]*/

        let uxEvents = UXEvent.allCases.reduce(into: [String: UXEvent]()) {
            $0[String(describing: $1)] = $1
        }

        return [
            // returning .rawValue explicitly, due to the reason described above
            "FaceTecUXEvent": uxEvents.mapValues({ $0.rawValue }),
            "FaceTecSDKStatus": [:], // sdkStatuses.mapValues({ $0.rawValue }),
            "FaceTecSessionStatus": [:] // sessionStatuses.mapValues({ $0.rawValue })
        ]
    }

    @objc(initialize:jwtAccessToken:licenseKey:encryptionKey:licenseText:resolver:rejecter:)
    open func initialize(_ serverURL: String, jwtAccessToken: String,
        licenseKey: String, encryptionKey: String, licenseText: String,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        resolve(nil)
        /*if FaceTecSDKStatus.initialized == FaceTec.sdk.getStatus() {
            resolve(nil)
            return
        }

        FaceTecGlobalState.DeviceLicenseKeyIdentifier = licenseKey
        FaceTecGlobalState.GoodServerURL = goodServerURL
        FaceTecGlobalState.FaceTecServerBaseURL = faceTecServerURL

        FaceTec.sdk.initialize(
            licenseKeyIdentifier: licenseKey,
            faceMapEncryptionKey: FaceTecGlobalState.PublicFaceMapEncryptionKey,
            preloadFaceTecSDK: preloadSDK,
            completion: { initializationSuccessful in
                if initializationSuccessful {
                    resolve(initializationSuccessful)
                    return
                }

                let status = FaceTec.sdk.getStatus()
                let message = FaceTecSDKStatus.neverInitialized != status
                    ? FaceTec.sdk.description(for: status)
                    : """
                Initialize wasn't attempted as Simulator has been detected. \
                FaceTec FaceTecSDK could be ran on the real devices only
                """

                FaceTecRCTUtils.rejectWith(message, status.rawValue, rejecter: reject)
        })*/
    }

    @objc(faceVerification:maxRetries:resolver:rejecter:)
    open func faceVerification(
        _ enrollmentIdentifier: String, maxRetries: Int,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        // let presentedVC = RCTPresentedViewController()!
        let delegate = FaceTecRCTPromiseProcessingDelegate(resolver: resolve, rejecter: reject)
        /* let processor = EnrollmentProcessor(fromVC: presentedVC, delegate: delegate)

        processor.enroll(enrollmentIdentifier, jwtAccessToken) */
        delegate.onProcessingComplete(isSuccess: true)
    }
}
