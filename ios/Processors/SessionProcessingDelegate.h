//
//  SessionProcessingDelegate.h
//  FaceTec
//
//  Created by Alex Serdukov on 16.11.2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FaceTecSDK/FaceTecSDK.h>

@protocol SessionDelegate;

@interface SessionProcessingDelegate : NSObject<FaceTecFaceScanProcessorDelegate>
  
  @property (nonatomic) id<SessionDelegate> _Nonnull session;

- (instancetype _Nonnull ) initWithSession:(id <SessionDelegate> _Nonnull)session;

@end
