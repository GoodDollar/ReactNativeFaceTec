//
//  SessionProcessingDelegate.m
//  FaceTec
//
//  Created by Alex Serdukov on 16.11.2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "SessionProcessingDelegate.h"

@implementation SessionProcessingDelegate
  - (instancetype) initWithSession:(id <SessionDelegate> _Nonnull)session {
    self = [super init];
    
    if (self) {
      _session = session;
    }
    
    return self;
  }

  - (void) onFaceTecSDKCompletelyDone {
    NSLog(@"Session done");
  }

  - (void) processSessionWhileFaceTecSDKWaits:(id<FaceTecSessionResult> _Nonnull)sessionResult faceScanResultCallback:(id<FaceTecFaceScanResultCallback> _Nonnull)faceScanResultCallback {
    NSLog(@"Received face photo");
  }

@end
