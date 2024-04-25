//
//  SessionDelegate.h
//  FaceTec
//
//  Created by Alex Serdukov on 17.11.2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

// This is actually a copy of FaceTecFaceScanProcessorDelegate
// it could be defined also on Swift but there's an additional
// research is needed to understand why onFaceTecSessionResult
// doesn't exposed to the auto generated -Swift.h header
// so now it's written on Objective C too
@protocol SessionDelegate<NSObject>

  - (void)onFaceTecSessionResult
      :(id<FaceTecSessionResult> _Nonnull) sessionResult
      faceScanResultCallback :(id<FaceTecFaceScanResultCallback> _Nonnull) faceScanResultCallback
      NS_SWIFT_NAME(onFaceTecSessionResult(sessionResult:faceScanResultCallback:));

  - (void)onFaceTecSessionDone NS_SWIFT_NAME(onFaceTecSessionDone());

@end
