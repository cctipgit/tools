//
//  ToolModule.m
//  cryptovise
//
//  Created by admin on 2023/7/10.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(ToolModule, NSObject)

RCT_EXTERN_METHOD(openNative:(RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject);
RCT_EXTERN_METHOD(getAppsFlyerConversionData:(RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject);
RCT_EXTERN_METHOD(logEvent:(NSString *)parameter resolve:(RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject);

RCT_EXTERN_METHOD(supportedEvents)

+ (BOOL)requiresMainQueueSetup {
    return YES;
}
@end
