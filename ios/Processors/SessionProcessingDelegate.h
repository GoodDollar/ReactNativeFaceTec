//
//  SessionProcessingDelegate.h
//  FaceTec
//
//  Created by Alex Serdukov on 16.11.2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FaceTecSDK/FaceTecSDK.h>
#import "../Protocols/SessionDelegate.h"

// Due to the unknown reason compiler crashes when you're trying
// to implement FaceTecFaceScanProcessorDelegate from the Swift class
// AND building a ReactNative project. On separate iOS app everything is fine
// To workaround i had to define this ObjectiveC class and pass its instance
// to the createSessionVC()

@interface SessionProcessingDelegate : NSObject<FaceTecFaceScanProcessorDelegate>
  
  @property (nonatomic, retain) id<SessionDelegate> _Nonnull session;

  // class receives a pointer to the SessionDelegate which is just
  // a copy of FaceTecFaceScanProcessorDelegate
- (instancetype _Nonnull) initWithSession:(id <SessionDelegate> _Nonnull)session;

@end
