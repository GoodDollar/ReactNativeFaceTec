//
//  FaceTecAuthenticationModuleBridge.m
//  FaceTecVerificationNative
//
//  Created by Alex Serdukov on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

// Exports Swift implementation of the NativeModule to ObjC runtime to be accessible for the RCTBridge
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(FaceTecModule, NSObject)

RCT_EXTERN_METHOD(
  initializeSDK:(NSString *)serverURL
  jwtAccessToken:(NSString *)jwtAccessToken
  licenseKey:(NSString *)licenseKey
  encryptionKey:(NSString *)encryptionKey
  licenseText:(NSString *)licenseText
  resolver: (RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
  faceVerification:(NSString *)enrollmentIdentifier
  maxRetries:(NSNumber *)maxRetries
  timeout:(NSNumber *)timeout
  resolver:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)

@end
