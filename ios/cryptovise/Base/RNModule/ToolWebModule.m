//
//  ToolWebModule.m
//  cryptovise
//
//  Created by admin on 2023/7/21.
//

#import <Foundation/Foundation.h>
#import "React/RCTViewManager.h"
#import "React/RCTBridgeModule.h"

/// CompoentName: ToolBigWebView
/// ToolWebModule: Class name
/// RCTViewManager: Super name
@interface RCT_EXTERN_REMAP_MODULE(NativeWebView, ToolWebModule, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(url, NSString)
RCT_EXPORT_VIEW_PROPERTY(onMessage, RCTBubblingEventBlock)

+ (BOOL)requiresMainQueueSetup {
    return YES;
}
@end
