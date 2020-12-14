//
//  FaceTecAuthenticationModuleBridge.m
//  FaceTecVerificationNative
//
//  Created by Alex Serdukov on 4/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(FaceTec, NSObject)

RCT_EXTERN_METHOD(
  initialize:(NSString *)serverURL
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
  resolver:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)

@end
